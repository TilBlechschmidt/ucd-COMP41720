package dev.blechschmidt.quocojms.message;

import java.io.Serializable;

import dev.blechschmidt.quocojms.core.Quotation;

public class QuotationResponseMessage implements Serializable {
    public int id;
    public Quotation quotation;

    public QuotationResponseMessage(int id, Quotation quotation) {
        this.id = id;
        this.quotation = quotation;
    }
}
