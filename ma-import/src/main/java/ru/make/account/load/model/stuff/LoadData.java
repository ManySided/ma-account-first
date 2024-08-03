package ru.make.account.load.model.stuff;

import java.net.URI;

public class LoadData {
    private final String host;
    private final String port;
    private final String accountName;

    public LoadData(String host, String port, String accountName) {
        this.host = host;
        this.port = port;
        this.accountName = accountName;
    }

    public URI getUrl(String apiUrl) throws Exception {
        return new URI(String.format("http://%s:%s/api/service/%s", host, port, apiUrl));
    }

    public String getAccountName() {
        return accountName;
    }
}
