package dev.blechschmidt.quocojms.core;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import dev.blechschmidt.quocojms.message.QuotationRequestMessage;
import dev.blechschmidt.quocojms.message.QuotationResponseMessage;

public class Quoter {
    QuotationService service;

    String id;
    String url;

    public Quoter(String[] args, QuotationService service) throws Exception {
        String url = System.getenv("MQ");
        String id = System.getenv("ID");

        // Prefer CLI args over environment variables
        if (args.length == 2) {
            id = args[0];
            url = args[1];
        }

        // Require both url and ID to be set
        if (url == null || id == null) {
            throw new Exception(
                    "Expected two CLI arguments (id, brokerUrl) or the environment variables (MQ, ID) to be set");
        }

        this.service = service;
        this.url = url;
        this.id = id;
    }

    public void run() throws JMSException {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
        factory.setTrustAllPackages(true);

        Connection connection = factory.createConnection();
        connection.setClientID(id);
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        Topic requests = session.createTopic("quoterRequestTopic");
        Topic responses = session.createTopic("quoterResponseTopic");
        MessageConsumer consumer = session.createConsumer(requests);
        MessageProducer producer = session.createProducer(responses);

        connection.start();

        while (true) {
            Message message = consumer.receive();

            if (message instanceof ObjectMessage) {
                Object content = ((ObjectMessage) message).getObject();

                if (content instanceof QuotationRequestMessage) {
                    QuotationRequestMessage request = (QuotationRequestMessage) content;

                    Quotation quotation = service.generateQuotation(request.info);
                    Message response = session
                            .createObjectMessage(new QuotationResponseMessage(request.id, quotation));
                    producer.send(response);
                }
            } else {
                System.out.println("Unknown message type: " + message.getClass().getCanonicalName());
            }
        }
    }
}
