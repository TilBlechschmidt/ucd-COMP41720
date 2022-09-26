import java.rmi.registry.Registry;

import static org.junit.Assert.assertEquals;

import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import dev.blechschmidt.quocormi.core.Constants;
import dev.blechschmidt.quocormi.core.Quotation;
import dev.blechschmidt.quocormi.core.QuotationService;
import dev.blechschmidt.quocormi.core.BrokerService;
import dev.blechschmidt.quocormi.core.ClientInfo;
import dev.blechschmidt.quocormi.broker.RemoteBrokerService;

import dev.blechschmidt.quocormi.auldfellas.AFQService;

import org.junit.*;

// We are not using a static registry field w/ BeforeClass here because the two test cases require different setups
public class BrokerUnitTest {
    private final static ClientInfo info = new ClientInfo("John", ClientInfo.MALE, 42, 1337, 69, "RZ-TB-363");

    @Test
    public void generateQuotationsTest() throws Exception {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);

            QuotationService afqService = new AFQService();
            BrokerService brokerService = new RemoteBrokerService(registry);

            QuotationService quotationService = (QuotationService) UnicastRemoteObject.exportObject(afqService, 0);
            registry.bind(Constants.AULD_FELLAS_SERVICE, quotationService);

            BrokerService broker = (BrokerService) UnicastRemoteObject.exportObject(brokerService, 0);
            registry.bind(Constants.BROKER_SERVICE, broker);

            BrokerService service = (BrokerService) registry.lookup(Constants.BROKER_SERVICE);
            List<Quotation> quotations = service.getQuotations(info);
            assertEquals(quotations.size(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void generateQuotationsWithoutServicesTest() throws Exception {
        // A different port has to be used because the tests may otherwise collide
        // ("old" registry is still listening / the port is not yet freed)
        Registry registry = LocateRegistry.createRegistry(1098);
        BrokerService brokerService = new RemoteBrokerService(registry);

        List<Quotation> quotations = brokerService.getQuotations(info);
        assertEquals(quotations.size(), 0);
    }
}
