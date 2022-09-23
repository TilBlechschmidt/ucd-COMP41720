import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import dev.blechschmidt.quocormi.core.Constants;
import dev.blechschmidt.quocormi.core.QuotationService;
import dev.blechschmidt.quocormi.core.ClientInfo;
import dev.blechschmidt.quocormi.dodgydrivers.DDQService;

import org.junit.*;

import static org.junit.Assert.assertNotNull;

public class DodgydriversUnitTest {
    private static Registry registry;

    @BeforeClass
    public static void setup() {
        QuotationService ddqService = new DDQService();

        try {
            registry = LocateRegistry.createRegistry(1099);
            QuotationService quotationService = (QuotationService) UnicastRemoteObject.exportObject(ddqService, 0);
            registry.bind(Constants.DODGY_DRIVERS_SERVICE, quotationService); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void connectionTest() throws Exception {
        QuotationService service = (QuotationService) registry.lookup(Constants.DODGY_DRIVERS_SERVICE);
        assertNotNull(service);
    }

    @Test
    public void generateQuotationTest() throws Exception {
        QuotationService service = (QuotationService) registry.lookup(Constants.DODGY_DRIVERS_SERVICE);
        ClientInfo info = new ClientInfo("John", ClientInfo.MALE, 42, 1337, 69, "RZ-TB-363");
        service.generateQuotation(info);
    }
}
