package dev.blechschmidt.quocormi.broker;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import dev.blechschmidt.quocormi.core.BrokerService;
import dev.blechschmidt.quocormi.core.Constants;

public class Server {
    public static void main(String[] args) {
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
            BrokerService brokerService = (BrokerService) UnicastRemoteObject.exportObject(new LocalBrokerService(registry), 0);

            // Register the object with the RMI Registry
            registry.bind(Constants.BROKER_SERVICE, brokerService);

            System.out.println("LocalBrokerService registered");

            while (true) {
                Thread.sleep(Integer.MAX_VALUE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
