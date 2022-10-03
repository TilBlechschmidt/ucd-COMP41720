package dev.blechschmidt.quocows;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.*;
import javax.xml.ws.Endpoint;

import dev.blechschmidt.quocows.AbstractQuotationService;
import dev.blechschmidt.quocows.ClientInfo;
import dev.blechschmidt.quocows.Quotation;

/**
 * Implementation of the Girl Power insurance quotation service.
 * 
 * @author Rem, Til Blechschmidt <til.blechschmidt@ucdconnect.ie>
 *
 */
@WebService
@SOAPBinding(style = Style.RPC, use = Use.LITERAL)
public class Quoter extends AbstractQuotationService implements QuoterService {
    // All references are to be prefixed with an DD (e.g. DD001000)
    public static final String PREFIX = "GP";
    public static final String COMPANY = "Girl Power Inc.";

    public static void main(String[] args) throws UnknownHostException, IOException {
		String envport = System.getenv("QUOTER_PORT");
		int port = args.length == 1 ? Integer.parseInt(args[0]) : (envport != null ? Integer.parseInt(envport) : 9000);

		Endpoint.publish("http://0.0.0.0:" + port + "/quotation", new Quoter());

        QuotationServiceAnnouncer announcer = new QuotationServiceAnnouncer();
        announcer.register("girlpower", port, "path=quotation");
    }

    /**
     * Quote generation:
     * 50% discount for being female
     * 20% discount for no penalty points
     * 15% discount for < 3 penalty points
     * no discount for 3-5 penalty points
     * 100% penalty for > 5 penalty points
     * 5% discount per year no claims
     */
    @WebMethod
    public Quotation generateQuotation(ClientInfo info) {
        // Create an initial quotation between 600 and 1000
        double price = generatePrice(600, 400);

        // Automatic 50% discount for being female
        int discount = (info.gender == ClientInfo.FEMALE) ? 50 : 0;

        // Add a points discount
        discount += getPointsDiscount(info);

        // Add a no claims discount
        discount += getNoClaimsDiscount(info);

        // Generate the quotation and send it back
        return new Quotation(COMPANY, generateReference(PREFIX), (price * (100 - discount)) / 100);
    }

    private int getNoClaimsDiscount(ClientInfo info) {
        return 5 * info.noClaims;
    }

    private int getPointsDiscount(ClientInfo info) {
        if (info.points == 0)
            return 20;
        if (info.points < 3)
            return 15;
        if (info.points < 6)
            return 0;
        return -100;
    }
}
