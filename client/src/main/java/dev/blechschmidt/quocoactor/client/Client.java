package dev.blechschmidt.quocoactor.client;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import akka.actor.AbstractActor;
import dev.blechschmidt.quocoactor.core.ClientInfo;
import dev.blechschmidt.quocoactor.core.Quotation;
import dev.blechschmidt.quocoactor.service.messages.ApplicationResponse;
import dev.blechschmidt.quocoactor.service.messages.QuotationRequest;

public class Client extends AbstractActor {
    private Map<Integer, QuotationRequest> requests = Collections.synchronizedMap(new HashMap<>());

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(QuotationRequest.class,
                        msg -> requests.put(msg.getId(), msg))
                .match(ApplicationResponse.class, msg -> displayResponse(msg))
                .build();
    }

    void displayResponse(ApplicationResponse response) {
        QuotationRequest request = requests.get(response.getId());
        if (request == null)
            return;

        displayProfile(request.getClientInfo());
        response.getQuotations().forEach(q -> displayQuotation(q));
    }

    /**
     * Display the client info nicely.
     * 
     * @param info
     */
    public static void displayProfile(ClientInfo info) {
        System.out.println(
                "|=================================================================================================================|");
        System.out.println(
                "|                                     |                                     |                                     |");
        System.out.println(
                "| Name: " + String.format("%1$-29s", info.getName()) +
                        " | Gender: "
                        + String.format("%1$-27s", (info.getGender() == ClientInfo.MALE ? "Male" : "Female")) +
                        " | Age: " + String.format("%1$-30s", info.getAge()) + " |");
        System.out.println(
                "| License Number: " + String.format("%1$-19s", info.getLicenseNumber()) +
                        " | No Claims: " + String.format("%1$-24s", info.getNoClaims() + " years") +
                        " | Penalty Points: " + String.format("%1$-19s", info.getPoints()) + " |");
        System.out.println(
                "|                                     |                                     |                                     |");
        System.out.println(
                "|=================================================================================================================|");
    }

    /**
     * Display a quotation nicely - note that the assumption is that the quotation
     * will follow
     * immediately after the profile (so the top of the quotation box is missing).
     * 
     * @param quotation
     */
    public static void displayQuotation(Quotation quotation) {
        System.out.println(
                "| Company: " + String.format("%1$-26s", quotation.getCompany()) +
                        " | Reference: " + String.format("%1$-24s", quotation.getReference()) +
                        " | Price: "
                        + String.format("%1$-28s", NumberFormat.getCurrencyInstance().format(quotation.getPrice()))
                        + " |");
        System.out.println(
                "|=================================================================================================================|");
    }
}
