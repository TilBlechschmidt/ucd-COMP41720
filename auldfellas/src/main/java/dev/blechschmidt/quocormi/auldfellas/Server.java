package dev.blechschmidt.quocormi.auldfellas;

import dev.blechschmidt.quocormi.core.Constants;
import dev.blechschmidt.quocormi.core.ServiceRegistration;

/**
 * Executable main class which hosts the AuldFellas quotation service
 * 
 * @author Til Blechschmidt <til.blechschmidt@ucdconnect.ie>
 *
 */
public class Server {
    public static void main(String[] args) {
        try {
            ServiceRegistration registration = new ServiceRegistration(Constants.AULD_FELLAS_SERVICE, new AFQService(), args);
            registration.register();

            System.out.println("AFQService registered");

            while (true) {
                Thread.sleep(Integer.MAX_VALUE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
