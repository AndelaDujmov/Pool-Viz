package com.poolviz.pool_viz.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poolviz.pool_viz.service.MempoolService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import reactor.core.publisher.Flux;
import wf.bitcoin.javabitcoindrpcclient.BitcoinRPCException;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Controller
@AllArgsConstructor
public class MempoolController {

    private MempoolService mempoolService;

    @GetMapping("/")
    public ModelAndView index() {

        ModelAndView mv = new ModelAndView();

        List<String> mempool = mempoolService.getRawMempool();

        mv.addObject("mempool", mempool);
        mv.addObject("data", "Welcome home man");

        mv.setViewName("index");

        return mv;
    }

    @GetMapping("/transactions/{txid}")
    @ResponseBody
    public ResponseEntity<?> getTransactionDetails(@PathVariable String txid) {

        BitcoindRpcClient.RawTransaction transaction = mempoolService.getMempoolTransaction(txid);

        Map<String, Object> transactionDetails = new HashMap<>();
        transactionDetails.put("txid", txid);
        transactionDetails.put("size", transaction.size());
        transactionDetails.put("fee", mempoolService.calculateFee(transaction.txId()));
        transactionDetails.put("confirmed", transaction.confirmations() != null && transaction.confirmations() > 0);
        transactionDetails.put("confirmations", Optional.ofNullable(transaction.confirmations()).orElse(0));
        transactionDetails.put("inputs", transaction.vIn().size());
        transactionDetails.put("outputs", transaction.vOut().size());

        return ResponseEntity.ok(transactionDetails);
    }

    @GetMapping(value = "/realTime")
    public ModelAndView visualiseMempoolRealTime() {

        ModelAndView mv = new ModelAndView();

        mv.setViewName("realTimeMempool");

        return mv;
    }

    @GetMapping(value = "/visualise", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
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

    @GetMapping("/about")
    public ModelAndView getTotalMempoolSize() {

        ModelAndView mv = new ModelAndView();

        long size = mempoolService.getTotalMempoolSize();

        mv.addObject("size", size);
        mv.setViewName("about");

        return mv;
    }

    @GetMapping(value = "/fluxStats", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String, Object>> getMempoolStatisticsRealtime() {
        final AtomicReference<Integer> previousTransactionCount = new AtomicReference<>(0);

        return Flux.interval(Duration.ofSeconds(1))
                .onBackpressureBuffer()
                .map(tick -> {
                    Map<String, Object> stats = mempoolService.getMempoolStatistics();
                    int currentTransactionCount = (int) stats.get("totalTransactions");

                    int transactionsPerInterval = currentTransactionCount - previousTransactionCount.getAndSet(currentTransactionCount);

                    List<BigDecimal> feeDistribution = mempoolService.getFeeDistribution();

                    long timestamp = System.currentTimeMillis();

                    return Map.of(
                            "totalTransactions", stats.get("totalTransactions"),
                            "totalSize", stats.get("totalSize"),
                            "transactionsPerInterval", transactionsPerInterval,
                            "feeDistribution", feeDistribution,
                            "timestamp", timestamp
                    );
                })
                .doOnError(error -> {
                    System.err.println("Error fetching mempool stats: " + error.getMessage());
                });
    }


    @GetMapping(value = "/statistics")
    public ModelAndView getStatistics() {

        ModelAndView mv = new ModelAndView();

        mv.setViewName("statistics");

        return mv;
    }

    // TODO: osto
    @GetMapping("/histogram")
    public ModelAndView histogram() {

        ModelAndView mv = new ModelAndView();

        try{

            Map<String, List<Long>> data = mempoolService.getFeeHistogramData();

            mv.addObject("data", data);
            mv.setViewName("histogram");
        } catch (BitcoinRPCException e) {

            mv.setViewName("index");
        }

        return mv;
    }
}
