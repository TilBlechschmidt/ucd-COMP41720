package dev.blechschmidt.quocoactor.service.messages;

import dev.blechschmidt.quocoactor.core.Quotation;

public class QuotationResponse implements CustomSerializable {
    private int id;
    private Quotation quotation;

    public QuotationResponse(int id, Quotation quotation) {
        this.id = id;
        this.quotation = quotation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Quotation getQuotation() {
        return quotation;
    }

    public void setQuotation(Quotation quotation) {
        this.quotation = quotation;
    }
}