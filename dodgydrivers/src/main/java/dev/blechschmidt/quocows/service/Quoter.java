package dev.blechschmidt.quocows.service;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.*;
import javax.xml.ws.Endpoint;

/**
 * Implementation of Quotation Service for Dodgy Drivers Insurance Company.
 * 
 * @author Rem, Til Blechschmidt <til.blechschmidt@ucdconnect.ie>
 *
 */
@WebService
@SOAPBinding(style = Style.RPC, use = Use.LITERAL)
public class Quoter extends AbstractQuotationService implements QuoterService {
    // All references are to be prefixed with an DD (e.g. DD001000)
    public static final String PREFIX = "DD";
    public static final String COMPANY = "Dodgy Drivers Corp.";

    public static void main(String[] args) throws UnknownHostException, IOException {
        String envport = System.getenv("QUOTER_PORT");
        int port = args.length == 1 ? Integer.parseInt(args[0]) : (envport != null ? Integer.parseInt(envport) : 9000);

        Endpoint.publish("http://0.0.0.0:" + port + "/quotation", new Quoter());

        QuotationServiceAnnouncer announcer = new QuotationServiceAnnouncer();
        announcer.register("dodgydrivers", port, "path=quotation");
    }

    /**
     * Quote generation:
     * 5% discount per penalty point (3 points required for qualification)
     * 50% penalty for <= 3 penalty points
     * 10% discount per year no claims
     */
    @WebMethod
    public Quotation generateQuotation(ClientInfo info) {
        // Create an initial quotation between 800 and 1000
        double price = generatePrice(800, 200);

        // 5% discount per penalty point (3 points required for qualification)
        int discount = (info.points > 3) ? 5 * info.points : -50;

        // Add a no claims discount
        discount += getNoClaimsDiscount(info);

        // Generate the quotation and send it back
        return new Quotation(COMPANY, generateReference(PREFIX), (price * (100 - discount)) / 100);
    }

    private int getNoClaimsDiscount(ClientInfo info) {
        return 10 * info.noClaims;
    }
}
