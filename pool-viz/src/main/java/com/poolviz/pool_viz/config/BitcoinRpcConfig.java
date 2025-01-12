package com.poolviz.pool_viz.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@Configuration
public class BitcoinRpcConfig {

    @Bean
    public BitcoinJSONRPCClient bitcoinClient() throws  MalformedURLException {
        Dotenv dotenv = Dotenv.configure().load();

        String rpcUrl = dotenv.get("BITCOIN_RPC_URL");
        String rpcUser = dotenv.get("BITCOIN_RPC_USER");
        String rpcPassword = dotenv.get("BITCOIN_RPC_PASSWORD");
        String rpcPort = dotenv.get("PORT");

        System.out.println(rpcUrl);
        System.out.println(rpcUser);
        System.out.println(rpcPort);

        if (rpcUrl == null || rpcUser == null || rpcPassword == null || rpcPort == null) {

            throw new IllegalArgumentException("Missing required environment variables for Bitcoin RPC configuration.");
        }

        @SuppressWarnings("deprication")
        URL url = new URL("http://"   + rpcUser + ':' + rpcPassword + "@" + rpcUrl + ":" + rpcPort + "/");

        return new BitcoinJSONRPCClient(url);
    }
}
