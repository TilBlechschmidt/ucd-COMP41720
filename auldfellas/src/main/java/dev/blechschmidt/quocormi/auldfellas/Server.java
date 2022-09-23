package dev.blechschmidt.quocormi.auldfellas;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import dev.blechschmidt.quocormi.core.Constants;
import dev.blechschmidt.quocormi.core.QuotationService;

public class Server {
    public static void main(String[] args) {
        QuotationService afqService = new AFQService();

        try {
            // Connect to the RMI Registry - creating the registry will be the responsibility of the broker
            Registry registry = LocateRegistry.createRegistry(1099);

            // Create the Remote Object
            QuotationService quotationService = (QuotationService) UnicastRemoteObject.exportObject(afqService, 0);

            // Register the object with the RMI Registry
            registry.bind(Constants.AULD_FELLAS_SERVICE, quotationService);

            System.out.println("AFQService registered to registry on port 1099");

            while (true) {
                Thread.sleep(Integer.MAX_VALUE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
