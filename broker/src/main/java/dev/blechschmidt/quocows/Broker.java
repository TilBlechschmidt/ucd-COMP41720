package dev.blechschmidt.quocows;

import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.*;
import javax.xml.ws.Endpoint;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Implementation of the broker service that uses the Service Registry.
 * 
 * @author Rem, Til Blechschmidt <til.blechschmidt@ucdconnect.ie>
 *
 */
@WebService
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL)
public class Broker {
	QuotationServiceAnnouncer announcer;

	public static void main(String[] args) throws UnknownHostException, IOException {
		Endpoint.publish("http://0.0.0.0:9000/quotations", new Broker());
	}

	public Broker() throws UnknownHostException, IOException {
		this.announcer = new QuotationServiceAnnouncer();
		this.announcer.startDiscovery();
	}

	@WebMethod
	public LinkedList<Quotation> getQuotations(ClientInfo info) {
		// TODO Handle potential runtime exceptions (like ECONNREFUSED)
		return announcer.getDiscoveredServices().stream().map(s -> s.generateQuotation(info))
				.collect(Collectors.toCollection(LinkedList::new));
	}
}
