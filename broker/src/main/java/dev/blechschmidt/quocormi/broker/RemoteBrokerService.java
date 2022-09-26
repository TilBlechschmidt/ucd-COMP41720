package dev.blechschmidt.quocormi.broker;

import java.util.LinkedList;
import java.util.List;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import dev.blechschmidt.quocormi.core.BrokerService;
import dev.blechschmidt.quocormi.core.ClientInfo;
import dev.blechschmidt.quocormi.core.Quotation;
import dev.blechschmidt.quocormi.core.QuotationService;

/**
 * Implementation of the broker service that uses the Service Registry.
 * 
 * @author Rem, Til Blechschmidt <til.blechschmidt@ucdconnect.ie>
 *
 */
public class RemoteBrokerService implements BrokerService {
    Registry registry;

    public RemoteBrokerService(Registry registry) {
        this.registry = registry;
    }

    public List<Quotation> getQuotations(ClientInfo info) throws RemoteException {
        List<Quotation> quotations = new LinkedList<Quotation>();

        for (String name : registry.list()) {
            if (name.startsWith("qs-")) {
                try {
                    QuotationService service = (QuotationService) registry.lookup(name);
                    quotations.add(service.generateQuotation(info));
                } catch (Exception e) {
                    // We do not want the client to ever encounter an error, so instead we print the
                    // error and softly fall back to just returning the quotations that we can get.
                    //
                    // It should be noted that unresponsive services might block for a while before
                    // returning errors, thus increasing response time.
                    new Exception("Failed to query quotation service '" + name + "'", e).printStackTrace();
                }
            }
        }

        return quotations;
    }
}
