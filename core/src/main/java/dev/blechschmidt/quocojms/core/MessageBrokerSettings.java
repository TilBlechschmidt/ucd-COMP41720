package dev.blechschmidt.quocojms.core;

public class MessageBrokerSettings {
    public String id;
    public String url;

    public MessageBrokerSettings(String[] args) throws Exception {
        String url = System.getenv("MQ");
        String id = System.getenv("ID");

        // Prefer CLI args over environment variables
        if (args.length == 2) {
            id = args[0];
            url = args[1];
        }

        // Require both url and ID to be set
        if (url == null || id == null) {
            throw new Exception(
                    "Expected two CLI arguments (id, brokerUrl) or the environment variables (MQ, ID) to be set");
        }

        this.id = id;
        this.url = url;
    }
}
