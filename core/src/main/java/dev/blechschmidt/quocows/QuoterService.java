package dev.blechschmidt.quocows;

import javax.jws.WebMethod;
import javax.jws.WebService;

// This interface only defines the structure of the WebService and thus could be part of a specification
// so that none of the clients have to actually depend on the core package. However, since in this case everything
// resides in one repository, the interface has been placed in the shared core module to prevent accidental API mismatches.
@WebService
public interface QuoterService {
    @WebMethod
    Quotation generateQuotation(ClientInfo info);
}
