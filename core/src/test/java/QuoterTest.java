import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import akka.testkit.javadsl.TestKit;
import dev.blechschmidt.quocoactor.core.ClientInfo;
import dev.blechschmidt.quocoactor.core.Quotation;
import dev.blechschmidt.quocoactor.core.QuotationService;
import dev.blechschmidt.quocoactor.service.actor.Quoter;
import dev.blechschmidt.quocoactor.service.messages.QuoterInit;
import dev.blechschmidt.quocoactor.service.messages.QuotationRequest;
import dev.blechschmidt.quocoactor.service.messages.QuotationResponse;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

class TQService implements QuotationService {
    @Override
    public Quotation generateQuotation(ClientInfo info) {
        return new Quotation("TEST", "TREF-1", 1337.42);
    }
}

public class QuoterTest {
    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testQuoter() {
        ActorRef quoterRef = system.actorOf(Props.create(Quoter.class), "test");
        TestKit probe = new TestKit(system);
        quoterRef.tell(new QuoterInit(new TQService()), null);
        quoterRef.tell(new QuotationRequest(1,
                new ClientInfo("Niki Collier", ClientInfo.FEMALE, 43, 0, 5, "PQR254/1")),
                probe.getRef());
        probe.awaitCond(probe::msgAvailable);
        probe.expectMsgClass(QuotationResponse.class);
    }
}
