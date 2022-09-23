import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import dev.blechschmidt.quocormi.core.Constants;
import dev.blechschmidt.quocormi.core.QuotationService;
import dev.blechschmidt.quocormi.core.ClientInfo;
import dev.blechschmidt.quocormi.auldfellas.AFQService;

import org.junit.*;

import static org.junit.Assert.assertNotNull;

public class AuldfellasUnitTest {
    private static Registry registry;

    @BeforeClass
    public static void setup() {
        QuotationService afqService = new AFQService();

        try {
            registry = LocateRegistry.createRegistry(1099);
            QuotationService quotationService = (QuotationService) UnicastRemoteObject.exportObject(afqService,0);
            registry.bind(Constants.AULD_FELLAS_SERVICE, quotationService); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void connectionTest() throws Exception {
        QuotationService service = (QuotationService) registry.lookup(Constants.AULD_FELLAS_SERVICE);
        assertNotNull(service);
    }

    @Test
    public void generateQuotationTest() throws Exception {
        QuotationService service = (QuotationService) registry.lookup(Constants.AULD_FELLAS_SERVICE);
        ClientInfo info = new ClientInfo("John", ClientInfo.MALE, 42, 1337, 69, "RZ-TB-363");
        service.generateQuotation(info);
    }
}
