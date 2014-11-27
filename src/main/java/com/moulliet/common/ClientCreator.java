package com.moulliet.common;

import com.sun.jersey.api.client.Client;

/**
 *
 */
public class ClientCreator {
    private static final Client cachedClient = create();

    /**
     * Creating clients is relatively expensive, so you may want to cache these locally.
     * This Client will retry by default.
     */
    public static Client create() {
        int connectTimeoutMillis = 30 * 1000;
        int readTimeoutMillis = 60 * 1000;

        Client client = Client.create();
        client.setConnectTimeout(connectTimeoutMillis);
        client.setReadTimeout(readTimeoutMillis);
        //client.addFilter(new RetryClientFilter());
        return client;
    }

    /**
     * This returns a cached common version of create().
     * You should not make any changes to this client.
     */
    public static Client cached() {
        return cachedClient;
    }

}