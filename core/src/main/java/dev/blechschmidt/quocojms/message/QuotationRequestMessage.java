package dev.blechschmidt.quocojms.message;

import java.io.Serializable;

import dev.blechschmidt.quocojms.core.ClientInfo;

public class QuotationRequestMessage implements Serializable {
    public int id;
    public ClientInfo info;

    public QuotationRequestMessage(int id, ClientInfo info) {
        this.id = id;
        this.info = info;
    }
}
