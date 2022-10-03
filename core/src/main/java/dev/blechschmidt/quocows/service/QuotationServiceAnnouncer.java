package dev.blechschmidt.quocows.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class QuotationServiceAnnouncer {
    private static final String QUOTATION_SERVICE_TYPE = "_quocows._tcp.local.";

    JmDNS jmdns;
    QuoterServiceListener listener;

    public QuotationServiceAnnouncer() throws UnknownHostException, IOException {
        jmdns = JmDNS.create(InetAddress.getLocalHost());
        listener = new QuoterServiceListener();
    }

    public void register(String name, int port, String path) throws IOException {
        ServiceInfo serviceInfo = ServiceInfo.create(QUOTATION_SERVICE_TYPE, name, port, path);
        jmdns.registerService(serviceInfo);
    }

    public void startDiscovery() {
        jmdns.addServiceListener(QUOTATION_SERVICE_TYPE, listener);
    }

    public Collection<QuoterService> getDiscoveredServices() {
        return listener.services.values();
    }

    private static class QuoterServiceListener implements ServiceListener {
        Map<String, QuoterService> services = new HashMap<>();

        @Override
        public void serviceAdded(ServiceEvent event) {
            System.out.println("~ Quoter '" + event.getName() + "'");
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            services.remove(event.getName());
            System.out.println("- Quoter '" + event.getName() + "'");
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
            String[] urls = event.getInfo().getURLs("http");

            // Try connecting to the URLs in order until we find one that works.
            for (String url : urls) {
                try {
                    URL wsdlUrl = new URL(url + "?wsdl");
                    QName serviceName = new QName("http://service.quocows.blechschmidt.dev/",
                            "QuoterService");
                    Service service = Service.create(wsdlUrl, serviceName);
                    QName portName = new QName("http://service.quocows.blechschmidt.dev/", "QuoterPort");
                    QuoterService quoterService = service.getPort(portName,
                            QuoterService.class);
                    services.put(event.getName(), quoterService);
                    System.out.println("+ Quoter '" + event.getName() + "'");
                    break;
                } catch (Exception e) {
                    new Exception("Failed to connect to discovered quotation service '" + event.getName() + "' at '"
                            + url + "'", e);
                    continue;
                }
            }
        }
    }
}
