package dev.blechschmidt.quocoactor.service.messages;

import dev.blechschmidt.quocoactor.core.QuotationService;

public class Init {
    private QuotationService quotationService;

    public Init(QuotationService quotationService) {
        this.quotationService = quotationService;
    }

    public Init() {
    }

    public QuotationService getQuotationService() {
        return quotationService;
    }

    public void setQuotationService(QuotationService quotationService) {
        this.quotationService = quotationService;
    }
}
