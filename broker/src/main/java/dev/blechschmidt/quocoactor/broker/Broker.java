package dev.blechschmidt.quocoactor.broker;

import java.util.HashSet;
import java.util.Set;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import dev.blechschmidt.quocoactor.service.messages.QuotationResponse;

public class Broker extends AbstractActor {
    Set<ActorRef> actorRefs = new HashSet<>();

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class,
                        msg -> {
                            if (!msg.equals("register"))
                                return;

                            actorRefs.add(getSender());

                            System.out.println("Quoter registered: " + getSender().toString());
                        })
                .match(QuotationResponse.class,
                        msg -> {
                            // TODO Do something with it
                            System.out.println("Got a quotation response o.O");
                        })
                .build();
    }
}
