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
    MessageBrokerSettings settings;

    public Quoter(String[] args, QuotationService service) throws Exception {
        this.settings = new MessageBrokerSettings(args);
        this.service = service;
    }

    public void run() throws JMSException {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(settings.url);
        factory.setTrustAllPackages(true);

        Connection connection = factory.createConnection();
        connection.setClientID(settings.id);
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        Topic requests = session.createTopic(Constants.TOPIC_QUOTER_REQ);
        Topic responses = session.createTopic(Constants.TOPIC_QUOTER_RES);
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
