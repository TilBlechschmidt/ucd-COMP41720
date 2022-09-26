package dev.blechschmidt.quocormi.girlpower;

import dev.blechschmidt.quocormi.core.Constants;
import dev.blechschmidt.quocormi.core.ServiceRegistration;

/**
 * Executable main class which hosts the GirlPower quotation service
 * 
 * @author Til Blechschmidt <til.blechschmidt@ucdconnect.ie>
 *
 */
public class Server {
    public static void main(String[] args) {
        try {
            ServiceRegistration registration = new ServiceRegistration(Constants.GIRL_POWER_SERVICE, new GPQService(),
                    args);
            registration.register();

            System.out.println("GPQService registered");

            while (true) {
                Thread.sleep(Integer.MAX_VALUE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
