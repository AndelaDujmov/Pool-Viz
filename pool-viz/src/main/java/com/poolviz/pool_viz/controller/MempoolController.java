package com.poolviz.pool_viz.controller;

import com.poolviz.pool_viz.service.MempoolService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;


import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class MempoolController {

    private MempoolService mempoolService;

    @GetMapping("/")
    public String home() {

        return "Hello World!";
    }

    @GetMapping("/mempool")
    public List<String> getMempool() {

        return mempoolService.getRawMempool();
    }

    @GetMapping("/mempool/{txid}")
    public BitcoindRpcClient.RawTransaction getRawTransaction(@PathVariable String txid) {

        return mempoolService.getMempoolTransaction(txid);
    }

    @GetMapping(value = "/mempool/visualise", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<BitcoindRpcClient.RawTransaction> streamMempool() {

        return Flux.interval(Duration.ofSeconds(1))
                .flatMap(element -> Flux.fromIterable(mempoolService.getRawMempool()))
                .map(txId -> {
                    try{

                        return mempoolService.getMempoolTransaction(txId);
                    } catch (RuntimeException e) {

                        throw new RuntimeException();
                    }
                });
    }

    @GetMapping("/mempool/{txid}/fees")
    public BigDecimal getTotalFee(@PathVariable String txid) {

        return mempoolService.calculateFee(txid);
    }

    @GetMapping("/mempool/size")
    public long getTotalMempoolSize() {

        return mempoolService.getTotalMempoolSize();
    }

    @GetMapping("/mempool/statistics")
    public Map<String, Object> getStatistics() {

        return mempoolService.getMempoolStatistics();
    }

    @GetMapping("/mempool/histogram")
    public Map<String, List<Long>> getHistogram() {

        return mempoolService.getFeeHistogramData();
    }
}
