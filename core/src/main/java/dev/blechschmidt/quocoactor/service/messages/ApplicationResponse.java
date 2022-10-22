package dev.blechschmidt.quocoactor.service.messages;

import java.util.List;

import dev.blechschmidt.quocoactor.core.Quotation;

public class ApplicationResponse implements CustomSerializable {
    private int id;
    private List<Quotation> quotations;

    public ApplicationResponse() {
    }

    public ApplicationResponse(int id, List<Quotation> quotations) {
        this.id = id;
        this.quotations = quotations;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Quotation> getQuotations() {
        return quotations;
    }

    public void setQuotations(List<Quotation> quotations) {
        this.quotations = quotations;
    }
}
