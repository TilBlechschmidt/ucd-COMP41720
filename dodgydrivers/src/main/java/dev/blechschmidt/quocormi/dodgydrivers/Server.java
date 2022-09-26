package dev.blechschmidt.quocormi.dodgydrivers;

import dev.blechschmidt.quocormi.core.Constants;
import dev.blechschmidt.quocormi.core.ServiceRegistration;

/**
 * Executable main class which hosts the DodgyDrivers quotation service
 * 
 * @author Til Blechschmidt <til.blechschmidt@ucdconnect.ie>
 *
 */
public class Server {
    public static void main(String[] args) {
        try {
            ServiceRegistration registration = new ServiceRegistration(Constants.DODGY_DRIVERS_SERVICE, new DDQService(), args);
            registration.register();

            System.out.println("DDQService registered");

            while (true) {
                Thread.sleep(Integer.MAX_VALUE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
