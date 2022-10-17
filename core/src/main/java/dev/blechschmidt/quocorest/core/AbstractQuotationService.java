package dev.blechschmidt.quocorest.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Random;

public abstract class AbstractQuotationService {
	private int counter = 1000;
	private Random random = new Random();

	private Map<String, Quotation> quotations = new HashMap<>();

	@RequestMapping(value = "/quotations", method = RequestMethod.POST)
	public ResponseEntity<Quotation> createQuotation(ClientInfo info) throws URISyntaxException {
		Quotation quotation = generateQuotation(info);
		quotations.put(quotation.getReference(), quotation);
		String path = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + "/quotations/"
				+ quotation.getReference();
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(new URI(path));
		return new ResponseEntity<>(quotation, headers, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/quotations/{reference}", method = RequestMethod.GET)
	public Quotation getResource(String reference) {
		Quotation quotation = quotations.get(reference);
		if (quotation == null)
			throw new NoSuchQuotationException();
		return quotation;
	}

	protected abstract Quotation generateQuotation(ClientInfo info);

	protected String generateReference(String prefix) {
		String ref = prefix;
		int length = 100000;
		while (length > 1000) {
			if (counter / length == 0)
				ref += "0";
			length = length / 10;
		}
		return ref + counter++;
	}

	protected double generatePrice(double min, int range) {
		return min + (double) random.nextInt(range);
	}
}
