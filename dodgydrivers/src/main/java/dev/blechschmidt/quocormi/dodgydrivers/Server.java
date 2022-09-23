package dev.blechschmidt.quocormi.dodgydrivers;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import dev.blechschmidt.quocormi.core.Constants;
import dev.blechschmidt.quocormi.core.QuotationService;

public class Server {
    public static void main(String[] args) {
        QuotationService ddqService = new DDQService();

        try {
            // Create / connect to the registry
            Registry registry = null;
            if (args.length == 0) {
                System.out.println("Hosting registry on port 1099");
                registry = LocateRegistry.createRegistry(1099);
            } else {
                System.out.println("Connecting to registry @ '" + args[0] + ":1099'");
                registry = LocateRegistry.getRegistry(args[0], 1099);
            }

            // Create the Remote Object
            QuotationService quotationService = (QuotationService) UnicastRemoteObject.exportObject(ddqService, 0);

            // Register the object with the RMI Registry
            registry.bind(Constants.DODGY_DRIVERS_SERVICE, quotationService);

            System.out.println("DDQService registered");

            while (true) {
                Thread.sleep(Integer.MAX_VALUE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
