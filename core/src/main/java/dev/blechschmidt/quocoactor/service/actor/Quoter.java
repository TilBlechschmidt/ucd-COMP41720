package dev.blechschmidt.quocoactor.service.actor;

import akka.actor.AbstractActor;
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
}
