package dev.blechschmidt.quocoactor.service.messages;

import dev.blechschmidt.quocoactor.core.QuotationService;

public class QuoterInit {
    private QuotationService quotationService;

    public QuoterInit(QuotationService quotationService) {
        this.quotationService = quotationService;
    }

    public QuoterInit() {
    }

    public QuotationService getQuotationService() {
        return quotationService;
    }

    public void setQuotationService(QuotationService quotationService) {
        this.quotationService = quotationService;
    }
}
