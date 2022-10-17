package dev.blechschmidt.quocorest.broker;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import dev.blechschmidt.quocorest.core.ClientApplication;
import dev.blechschmidt.quocorest.core.ClientInfo;
import dev.blechschmidt.quocorest.core.Quotation;

@RestController
public class Broker {
    Map<Long, ClientApplication> applications = new HashMap<>();
    long applicationCounter = 0;

    @Value("${broker.endpoints}")
    List<URI> endpoints;

    @RequestMapping(value = "/applications", method = RequestMethod.POST)
    public ResponseEntity<ClientApplication> createApplication(@RequestBody ClientInfo info) throws URISyntaxException {
        ClientApplication application = collectApplication(info);
        applications.put(application.getApplicationNumber(), application);

        String path = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + "/applications/"
                + application.getApplicationNumber();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(path));
        return new ResponseEntity<>(application, headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/applications/{applicationNumber}", method = RequestMethod.GET)
    public ClientApplication getResource(@PathVariable("applicationNumber") long reference) {
        ClientApplication application = applications.get(reference);
        if (application == null)
            throw new NoSuchApplicationException();
        return application;
    }

    @RequestMapping(value = "/applications", method = RequestMethod.GET)
    public Collection<ClientApplication> getResources() {
        return applications.values();
    }

    public ClientApplication collectApplication(ClientInfo info) {
        ClientApplication application = new ClientApplication(applicationCounter++, info, new ArrayList<>());

        for (URI quoter : endpoints) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                HttpEntity<ClientInfo> request = new HttpEntity<>(info);
                Quotation quotation = restTemplate.postForObject(quoter, request, Quotation.class);
                if (quotation != null) {
                    application.getQuotations().add(quotation);
                } else {
                    System.out.println("Received null value from quoter @ " + quoter.toString());
                }
            } catch (Exception e) {
                new Exception("failed to get quote from " + quoter.toString(), e).printStackTrace();
            }
        }

        return application;
    }
}
