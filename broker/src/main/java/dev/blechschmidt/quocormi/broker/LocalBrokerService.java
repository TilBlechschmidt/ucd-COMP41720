package dev.blechschmidt.quocormi.broker;

import java.util.LinkedList;
import java.util.List;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import dev.blechschmidt.quocormi.core.BrokerService;
import dev.blechschmidt.quocormi.core.ClientInfo;
import dev.blechschmidt.quocormi.core.Quotation;
import dev.blechschmidt.quocormi.core.QuotationService;

/**
 * Implementation of the broker service that uses the Service Registry.
 * 
 * @author Rem
 *
 */
public class LocalBrokerService implements BrokerService {
    Registry registry;

    public LocalBrokerService(Registry registry) {
        this.registry = registry;
    }

	public List<Quotation> getQuotations(ClientInfo info) throws RemoteException {
		List<Quotation> quotations = new LinkedList<Quotation>();

		for (String name : this.registry.list()) {
			if (name.startsWith("qs-")) {
                try {
                    QuotationService service = (QuotationService) this.registry.lookup(name);
                    quotations.add(service.generateQuotation(info));
                } catch (NotBoundException e) {
                    e.printStackTrace();
                }
			}
		}

		return quotations;
	}
}
