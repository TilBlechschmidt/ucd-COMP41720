package dev.blechschmidt.quocojms;

import dev.blechschmidt.quocojms.core.Quoter;

public class Main {
    public static void main(String[] args) throws Exception {
        new Quoter(args, new DDQService()).run();
    }
}
