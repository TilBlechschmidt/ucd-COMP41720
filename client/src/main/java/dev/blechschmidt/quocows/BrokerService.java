package dev.blechschmidt.quocows;

import java.util.LinkedList;
import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface BrokerService {
    @WebMethod
    LinkedList<Quotation> getQuotations(ClientInfo info);
}
