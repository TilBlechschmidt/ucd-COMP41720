package dev.blechschmidt.quocows;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.*;
import javax.xml.ws.Endpoint;

import dev.blechschmidt.quocows.QuotationServiceAnnouncer;
import dev.blechschmidt.quocows.AbstractQuotationService;
import dev.blechschmidt.quocows.ClientInfo;
import dev.blechschmidt.quocows.Quotation;

/**
 * Implementation of the AuldFellas insurance quotation service.
 * 
 * @author Rem
 *
 */
@WebService
@SOAPBinding(style = Style.RPC, use = Use.LITERAL)
public class Quoter extends AbstractQuotationService implements QuoterService {
	// All references are to be prefixed with an AF (e.g. AF001000)
	public static final String PREFIX = "AF";
	public static final String COMPANY = "Auld Fellas Ltd.";

	public static void main(String[] args) throws Exception {
		Endpoint.publish("http://0.0.0.0:9001/quotation", new Quoter());

		QuotationServiceAnnouncer announcer = new QuotationServiceAnnouncer();
		announcer.register("auldfellas", 9001, "path=quotation");
	}

	/**
	 * Quote generation:
	 * 30% discount for being male
	 * 2% discount per year over 60
	 * 20% discount for less than 3 penalty points
	 * 50% penalty (i.e. reduction in discount) for more than 60 penalty points
	 */
	@WebMethod
	public Quotation generateQuotation(ClientInfo info) {
		// Create an initial quotation between 600 and 1200
		double price = generatePrice(600, 600);

		// Automatic 30% discount for being male
		int discount = (info.gender == ClientInfo.MALE) ? 30 : 0;

		// Automatic 2% discount per year over 60...
		discount += (info.age > 60) ? (2 * (info.age - 60)) : 0;

		// Add a points discount
		discount += getPointsDiscount(info);

		// Generate the quotation and send it back
		return new Quotation(COMPANY, generateReference(PREFIX), (price * (100 - discount)) / 100);
	}

	private int getPointsDiscount(ClientInfo info) {
		if (info.points < 3)
			return 20;
		if (info.points <= 6)
			return 0;
		return -50;

	}

}
