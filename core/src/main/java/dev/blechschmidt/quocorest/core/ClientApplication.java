package dev.blechschmidt.quocorest.core;

import java.util.ArrayList;
import java.util.List;

public class ClientApplication {
    private long applicationNumber;
    private ClientInfo info;
    private ArrayList<Quotation> quotations;

    public ClientApplication(long applicationNumber, ClientInfo info, ArrayList<Quotation> quotations) {
        this.applicationNumber = applicationNumber;
        this.info = info;
        this.quotations = quotations;
    }

    public ClientApplication() {
    }

    public long getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(long applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public ClientInfo getInfo() {
        return info;
    }

    public void setInfo(ClientInfo info) {
        this.info = info;
    }

    public List<Quotation> getQuotations() {
        return quotations;
    }

    public void setQuotations(ArrayList<Quotation> quotations) {
        this.quotations = quotations;
    }
}
