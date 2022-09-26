package dev.blechschmidt.quocormi.broker;

import java.rmi.registry.Registry;

import dev.blechschmidt.quocormi.core.Constants;
import dev.blechschmidt.quocormi.core.ServiceRegistration;

/**
 * Executable main class which hosts the broker service connecting multiple
 * quotation services together
 * 
 * @author Til Blechschmidt <til.blechschmidt@ucdconnect.ie>
 *
 */
public class Server {
    public static void main(String[] args) {
        try {
            Registry registry = ServiceRegistration.buildRegistry(args);
            RemoteBrokerService localBrokerService = new RemoteBrokerService(registry);
            ServiceRegistration registration = new ServiceRegistration(Constants.BROKER_SERVICE, localBrokerService,
                    registry);
            registration.register();

            System.out.println("RemoteBrokerService registered");

            while (true) {
                Thread.sleep(Integer.MAX_VALUE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
