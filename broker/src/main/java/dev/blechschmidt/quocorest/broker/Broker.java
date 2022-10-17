package dev.blechschmidt.quocorest.broker;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;
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

@RestController
public class Broker {
    Map<Long, ClientApplication> applications = new HashMap<>();

    @RequestMapping(value = "/applications", method = RequestMethod.POST)
    public ResponseEntity<ClientApplication> createApplication(@RequestBody ClientInfo info) throws URISyntaxException {
        ClientApplication application = collectApplication(info);
        String path = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + "/applications/"
                + application.getApplicationNumber();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(path));
        return new ResponseEntity<>(application, headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/applications/{reference}", method = RequestMethod.GET)
    public ClientApplication getResource(@PathVariable("reference") long reference) {
        ClientApplication application = applications.get(reference);
        if (application == null)
            throw new NoSuchApplicationException();
        return application;
    }

    public ClientApplication collectApplication(ClientInfo info) {
        ClientApplication application = new ClientApplication();
        // TODO Request data from the quoters
        return application;
    }
}
