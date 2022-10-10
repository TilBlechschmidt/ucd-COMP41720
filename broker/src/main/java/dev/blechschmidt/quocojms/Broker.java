package dev.blechschmidt.quocojms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import dev.blechschmidt.quocojms.core.Constants;
import dev.blechschmidt.quocojms.core.MessageBrokerSettings;
import dev.blechschmidt.quocojms.core.Quotation;
import dev.blechschmidt.quocojms.message.QuotationRequestMessage;
import dev.blechschmidt.quocojms.message.QuotationResponseMessage;
import dev.blechschmidt.quocojms.message.QuotationsResponseMessage;

public class Broker {
    private static final long DEFAULT_DEADLINE = 1000;

    QuotationThread quotationThread;
    DeadlineThread deadlineThread;
    Thread deadlineThreadHandle;

    Session session;
    MessageProducer quoterRequestChannel;
    MessageProducer clientResponseChannel;
    MessageConsumer clientRequestChannel;

    public static void main(String[] args) throws Exception {
        new Broker(new MessageBrokerSettings(args)).run();
    }

    public Broker(MessageBrokerSettings settings) throws JMSException {
        // Connect to the message broker
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(settings.url);
        factory.setTrustAllPackages(true);

        Connection connection = factory.createConnection();
        connection.setClientID(settings.id);
        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        // Subscribe to the relevant topics/queues
        Queue clientRequestQueue = session.createQueue(Constants.QUEUE_CLIENT_REQ);
        Topic clientResponseTopic = session.createTopic(Constants.TOPIC_CLIENT_RES);
        Topic quoterRequestTopic = session.createTopic(Constants.TOPIC_QUOTER_REQ);
        Topic quoterResponseTopic = session.createTopic(Constants.TOPIC_QUOTER_RES);

        MessageConsumer quoterResponseChannel = session.createConsumer(quoterResponseTopic);
        quoterRequestChannel = session.createProducer(quoterRequestTopic);
        clientResponseChannel = session.createProducer(clientResponseTopic);
        clientRequestChannel = session.createConsumer(clientRequestQueue);

        connection.start();

        // Build the threads
        quotationThread = new QuotationThread(quoterResponseChannel);
        deadlineThread = new DeadlineThread(i -> this.handleDeadline(i));
    }

    private void addDeadline(long duration, int id) {
        deadlineThread.deadlines.add(new Deadline(duration, id));
        deadlineThreadHandle.interrupt();
    }

    private void handleDeadline(int id) {
        System.out.println("Deadline " + id + " has elapsed!");
        List<Quotation> quotes = quotationThread.getResponses(id);

        try {
            Message response = session.createObjectMessage(new QuotationsResponseMessage(id, quotes));
            clientResponseChannel.send(response);
        } catch (JMSException e) {
            new Exception("Failed to send response", e).printStackTrace();
        }
    }

    private void requestQuotes(QuotationRequestMessage message) throws JMSException {
        // Using a client-provided ID is technically "dangerous" as we are relying on
        // the clients to produce non-colliding IDs. For this setup, it is fine — in a
        // production scenario, mapping them onto internal request IDs would probably
        // make sense. However, given that all clients are in the same channel they
        // could still collide between each other even if we do that.
        quotationThread.expectResponses(message.id);
        addDeadline(DEFAULT_DEADLINE, message.id);

        Message request = session.createObjectMessage(message);
        quoterRequestChannel.send(request);
    }

    public void run() {
        // Start the background threads
        new Thread(quotationThread).start();
        deadlineThreadHandle = new Thread(deadlineThread);
        deadlineThreadHandle.start();

        // Begin receiving client requests until eternity
        while (true) {
            try {
                Message message = clientRequestChannel.receive();
                if (!(message instanceof ObjectMessage)) {
                    System.err.println("Received message with unknown type: " + message.getClass().getCanonicalName());
                    continue;
                }

                Object content = ((ObjectMessage) message).getObject();
                if (!(content instanceof QuotationRequestMessage)) {
                    System.err.println(
                            "Received message content with unknown type: " + content.getClass().getCanonicalName());
                    continue;
                }

                requestQuotes((QuotationRequestMessage) content);
                message.acknowledge();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 * Receives messages of type QuotationResponseMessage and stores them in a map
 * 
 * Note that it only stores responses that are expected (i.e. the
 * createResponseList method has been called)
 */
class QuotationThread implements Runnable {
    Map<Integer, List<Quotation>> responses = Collections.synchronizedMap(new HashMap<>());
    MessageConsumer quoterResponseChannel;

    public QuotationThread(MessageConsumer quoterResponseChannel) {
        this.quoterResponseChannel = quoterResponseChannel;
    }

    public void expectResponses(int id) {
        responses.put(id, new ArrayList<>());
    }

    public List<Quotation> getResponses(int id) {
        return responses.remove(id);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Message message = quoterResponseChannel.receive();
                if (!(message instanceof ObjectMessage)) {
                    System.err.println("Received message with unknown type: " + message.getClass().getCanonicalName());
                    continue;
                }

                Object content = ((ObjectMessage) message).getObject();
                if (!(content instanceof QuotationResponseMessage)) {
                    System.err.println(
                            "Received message content with unknown type: " + content.getClass().getCanonicalName());
                    continue;
                }

                QuotationResponseMessage response = (QuotationResponseMessage) content;
                List<Quotation> responseList = responses.get(response.id);

                if (responseList != null)
                    responseList.add(response.quotation);

                System.out.println("Received quotation for #" + response.id);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}

class Deadline {
    long instant;
    int id;

    public Deadline(long duration, int id) {
        this.instant = System.currentTimeMillis() + duration;
        this.id = id;
    }
}

/**
 * Waits for deadlines in a list and calls a closure when they elapse
 */
class DeadlineThread implements Runnable {
    List<Deadline> deadlines;
    Consumer<Integer> callback;

    public DeadlineThread(Consumer<Integer> callback) {
        // Use a synchronised list so we do not have to use synchronised blocks
        this.deadlines = Collections.synchronizedList(new ArrayList<>());
        this.callback = callback;
    }

    @Override
    public void run() {
        while (true) {
            try {
                long now = System.currentTimeMillis();
                long delay = deadlines.get(0).instant - now;

                // Wait until the next deadline elapses
                if (delay > 0) {
                    Thread.sleep(delay);
                }

                // Remove the deadline from the queue
                Deadline deadline = deadlines.remove(0);
                callback.accept(deadline.id);
            } catch (InterruptedException e) {
                System.out.println("New deadline added.");
            } catch (IndexOutOfBoundsException e) {
                System.out.println("All deadlines expired, deadline thread going to sleep.");
                try {
                    // Wait until a new deadline is pushed
                    Thread.sleep(Long.MAX_VALUE);
                } catch (InterruptedException e2) {
                    // Oh hey, somebody woke us up!
                    System.out.println("Deadline thread woken up.");
                }
            }
        }
    }
}
