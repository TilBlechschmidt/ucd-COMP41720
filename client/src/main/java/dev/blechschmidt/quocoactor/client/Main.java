package dev.blechschmidt.quocoactor.client;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import dev.blechschmidt.quocoactor.core.ClientInfo;
import dev.blechschmidt.quocoactor.service.messages.QuotationRequest;

public class Main {
    /**
     * Test Data
     */
    public static final ClientInfo[] clients = {
            new ClientInfo("Niki Collier", ClientInfo.FEMALE, 43, 0, 5, "PQR254/1"),
            new ClientInfo("Old Geeza", ClientInfo.MALE, 65, 0, 2, "ABC123/4"),
            new ClientInfo("Hannah Montana", ClientInfo.FEMALE, 16, 10, 0, "HMA304/9"),
            new ClientInfo("Rem Collier", ClientInfo.MALE, 44, 5, 3, "COL123/3"),
            new ClientInfo("Jim Quinn", ClientInfo.MALE, 55, 4, 7, "QUN987/4"),
            new ClientInfo("Donald Duck", ClientInfo.MALE, 35, 5, 2, "XYZ567/9")
    };

    public static void main(String[] args) throws InterruptedException {
        ActorSystem system = ActorSystem.create();

        ActorRef client = system.actorOf(Props.create(Client.class), "client");
        ActorSelection broker = findBroker(args, system);

        // Notify the client to expect a response and send the request to the broker
        //
        // Technically the client could send the request as well but to keep things
        // simple (and not hand the broker ActorSelection into the client) we are just
        // doing it from out here.
        for (int i = 0; i < clients.length; i++) {
            QuotationRequest request = new QuotationRequest(i, clients[i]);
            client.tell(request, null);
            broker.tell(request, client);
        }

        Thread.sleep(2000);
        System.exit(0);
    }

    private static ActorSelection findBroker(String[] args, ActorSystem system) {
        String brokerHost = System.getenv("BROKER");

        if (args.length == 1) {
            brokerHost = args[0];
        }

        return system.actorSelection("akka.tcp://default@" + brokerHost + "/user/broker");
    }
}
