package dev.blechschmidt.quocojms.message;

import java.io.Serializable;
import java.util.List;

import dev.blechschmidt.quocojms.core.Quotation;

public class QuotationsResponseMessage implements Serializable {
    public int id;
    public List<Quotation> quotations;

    public QuotationsResponseMessage(int id, List<Quotation> quotations) {
        this.id = id;
        this.quotations = quotations;
    }
}
