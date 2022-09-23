import java.rmi.registry.Registry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import dev.blechschmidt.quocormi.core.Constants;
import dev.blechschmidt.quocormi.core.Quotation;
import dev.blechschmidt.quocormi.core.QuotationService;
import dev.blechschmidt.quocormi.core.BrokerService;
import dev.blechschmidt.quocormi.core.ClientInfo;
import dev.blechschmidt.quocormi.broker.LocalBrokerService;

import dev.blechschmidt.quocormi.auldfellas.AFQService;

import org.junit.*;

public class BrokerUnitTest {
    private static Registry registry;

    @BeforeClass
    public static void setup() {
        try {
            registry = LocateRegistry.createRegistry(1099);

            QuotationService afqService = new AFQService();
            BrokerService brokerService = new LocalBrokerService(registry);

            QuotationService quotationService = (QuotationService) UnicastRemoteObject.exportObject(afqService,0);
            registry.bind(Constants.AULD_FELLAS_SERVICE, quotationService); 

            BrokerService broker = (BrokerService) UnicastRemoteObject.exportObject(brokerService, 0);
            registry.bind(Constants.BROKER_SERVICE, broker);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void generateQuotationsTest() throws Exception {
        BrokerService service = (BrokerService) registry.lookup(Constants.BROKER_SERVICE);
        ClientInfo info = new ClientInfo("John", ClientInfo.MALE, 42, 1337, 69, "RZ-TB-363");
        List<Quotation> quotations = service.getQuotations(info);
        assertEquals(quotations.size(), 1);
    }
}
