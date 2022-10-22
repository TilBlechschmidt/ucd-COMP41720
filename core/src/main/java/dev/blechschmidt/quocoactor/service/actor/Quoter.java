package dev.blechschmidt.quocoactor.service.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import dev.blechschmidt.quocoactor.core.Quotation;
import dev.blechschmidt.quocoactor.core.QuotationService;
import dev.blechschmidt.quocoactor.service.messages.QuoterInit;
import dev.blechschmidt.quocoactor.service.messages.QuotationRequest;
import dev.blechschmidt.quocoactor.service.messages.QuotationResponse;

public class Quoter extends AbstractActor {
    private QuotationService service;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(QuotationRequest.class,
                        msg -> {
                            Quotation quotation = service.generateQuotation(msg.getClientInfo());
                            getSender().tell(
                                    new QuotationResponse(msg.getId(), quotation), getSelf());
                        })
                .match(QuoterInit.class,
                        msg -> {
                            service = msg.getQuotationService();
                        })
                .build();
    }

    public static void registerWithBroker(String[] args, ActorSystem system, ActorRef ref) {
        String brokerHost = System.getenv("BROKER");

        if (args.length == 1) {
            brokerHost = args[0];
        }

        ActorSelection selection = system.actorSelection("akka.tcp://default@" + brokerHost + "/user/broker");
        selection.tell("register", ref);
    }
}
