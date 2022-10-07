package dev.blechschmidt.quocojms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import dev.blechschmidt.quocojms.message.QuotationRequestMessage;
import dev.blechschmidt.quocojms.message.QuotationResponseMessage;
import dev.blechschmidt.quocojms.core.Quotation;

public class Receiver {
    private static final AFQService service = new AFQService();

    public static void main(String[] args) throws JMSException {
        // TODO Take in authority for activemq server
        String url = "failover://tcp://localhost:61616";

        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
        factory.setTrustAllPackages(true);

        Connection connection = factory.createConnection();
        connection.setClientID("auldfellas");
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        // TODO Make these "global" constants
        Queue queue = session.createQueue("QUOTATIONS");
        Topic topic = session.createTopic("APPLICATIONS");
        MessageConsumer consumer = session.createConsumer(topic);
        MessageProducer producer = session.createProducer(queue);

        connection.start();

        while (true) {
            Message message = consumer.receive();

            if (message instanceof ObjectMessage) {
                Object content = ((ObjectMessage) message).getObject();

                if (content instanceof QuotationRequestMessage) {
                    QuotationRequestMessage request = (QuotationRequestMessage) content;

                    Quotation quotation = service.generateQuotation(request.info);
                    Message response = session.createObjectMessage(new QuotationResponseMessage(request.id, quotation));
                    producer.send(response);
                }
            } else {
                System.out.println("Unknown message type: " + message.getClass().getCanonicalName());
            }
        }
    }
}
