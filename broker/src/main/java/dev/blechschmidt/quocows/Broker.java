package dev.blechschmidt.quocows;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.*;
import javax.xml.ws.Endpoint;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

/**
 * Implementation of the broker service that uses the Service Registry.
 * 
 * @author Rem, Til Blechschmidt <til.blechschmidt@ucdconnect.ie>
 *
 */
@WebService
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL)
public class Broker {
	List<QuoterService> services;

	public static void main(String[] args) throws MalformedURLException {
		Endpoint.publish("http://0.0.0.0:9000/quotations", new Broker(args));
	}

	public Broker(List<QuoterService> services) {
		this.services = services;
	}

	public Broker(String[] urls) throws MalformedURLException {
		List<QuoterService> services = new LinkedList<>();

		for (String url : urls) {
			URL wsdlUrl = new URL(url + "?wsdl");
			QName serviceName = new QName("http://quocows.blechschmidt.dev/", "QuoterService");
			Service service = Service.create(wsdlUrl, serviceName);
			QName portName = new QName("http://quocows.blechschmidt.dev/", "QuoterPort");
			QuoterService quotationService = service.getPort(portName, QuoterService.class);
			services.add(quotationService);
		}

		this.services = services;
	}

	@WebMethod
	public LinkedList<Quotation> getQuotations(ClientInfo info) {
		return services.stream().map(s -> s.generateQuotation(info)).collect(Collectors.toCollection(LinkedList::new));
	}
}
