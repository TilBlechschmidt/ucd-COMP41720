package dev.blechschmidt.quocoactor.broker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import dev.blechschmidt.quocoactor.core.Quotation;
import dev.blechschmidt.quocoactor.service.messages.ApplicationResponse;
import dev.blechschmidt.quocoactor.service.messages.QuotationRequest;
import dev.blechschmidt.quocoactor.service.messages.QuotationResponse;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

public class Broker extends AbstractActor {
    private static final FiniteDuration DEADLINE = Duration.create(2, TimeUnit.SECONDS);

    // Just in case, we make the collections synchronised — no idea if Akka
    // dispatches the match blocks on the same thread.
    private Set<ActorRef> quoterRefs = Collections.synchronizedSet(new HashSet<>());
    private Map<Integer, Client> clients = Collections.synchronizedMap(new HashMap<>());
    private int clientCounter = 0;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class,
                        msg -> {
                            if (!msg.equals("register"))
                                return;

                            quoterRefs.add(getSender());

                            System.out.println("Quoter registered: " + getSender().toString());
                        })
                .match(QuotationRequest.class,
                        msg -> {
                            int clientId = requestQuotes(msg);
                            scheduleDeadline(clientId);
                        })
                .match(QuotationResponse.class,
                        msg -> {
                            Client client = clients.get(msg.getId());
                            if (client != null)
                                client.add(msg.getQuotation());
                        })
                .match(RequestDeadline.class,
                        msg -> {
                            Client client = clients.get(msg.clientKey);

                            if (client != null)
                                client.sendResponse();
                        })
                .build();
    }

    int requestQuotes(QuotationRequest originalRequest) {
        int clientId = clientCounter++;
        clients.put(clientId, new Client(getSender(), originalRequest));

        QuotationRequest request = new QuotationRequest(clientId, originalRequest.getClientInfo());
        for (ActorRef quoterRef : quoterRefs) {
            quoterRef.tell(request, getSelf());
        }

        return clientId;
    }

    void scheduleDeadline(int clientId) {
        getContext().system().scheduler().scheduleOnce(DEADLINE,
                getSelf(), new RequestDeadline(clientCounter++),
                getContext().dispatcher(),
                null);
    }

    class Client {
        QuotationRequest request;
        List<Quotation> quotations = new ArrayList<>();
        ActorRef clientRef;

        public Client(ActorRef clientRef, QuotationRequest request) {
            this.clientRef = clientRef;
            this.request = request;
        }

        void add(Quotation quotation) {
            quotations.add(quotation);
        }

        void sendResponse() {
            clientRef.tell(new ApplicationResponse(request.getId(), quotations), getSelf());
        }
    }

    class RequestDeadline {
        int clientKey;

        public RequestDeadline(int clientKey) {
            this.clientKey = clientKey;
        }
    }
}
