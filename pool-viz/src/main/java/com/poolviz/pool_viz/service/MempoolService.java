package com.poolviz.pool_viz.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MempoolService {

    private BitcoinJSONRPCClient client;

    public List<String> getRawMempool() {

        return client.getRawMemPool();
    }

    public BitcoindRpcClient.RawTransaction getMempoolTransaction(String txId) {

        return client.getRawTransaction(txId);
    }

    public BigDecimal calculateFee(String txid) {

        BigDecimal total = BigDecimal.ZERO;

        BitcoindRpcClient.RawTransaction transaction = client.getRawTransaction(txid);

        total = calculateTotalInputs(transaction).subtract(calculateTotalOutputs(transaction));

        return total;
    }

    private BigDecimal calculateTotalInputs(BitcoindRpcClient.RawTransaction transaction) {

        BigDecimal totalInputs = BigDecimal.ZERO;

        List<BitcoindRpcClient.RawTransaction.In> inputs = transaction.vIn();

        for (BitcoindRpcClient.RawTransaction.In input : inputs) {
            BitcoindRpcClient.RawTransaction rawTransaction = client.getRawTransaction(input.txid());

            BitcoindRpcClient.RawTransaction.Out output = rawTransaction.vOut().get(input.vout());
            totalInputs = totalInputs.add(output.value());
        }

        return totalInputs;
    }

    private BigDecimal calculateTotalOutputs(BitcoindRpcClient.RawTransaction transaction) {

        BigDecimal totalOutputs = BigDecimal.ZERO;

        List<BitcoindRpcClient.RawTransaction.Out> outputs = transaction.vOut();

        for (BitcoindRpcClient.RawTransaction.Out output : outputs) {
            totalOutputs = totalOutputs.add(output.value());
        }

        return totalOutputs;
    }

    public long calculateTransactionSize(String txid) {

        BitcoindRpcClient.RawTransaction transaction = client.getRawTransaction(txid);
        return transaction.hex().length() / 2;
    }

    public BigDecimal calculateFeeRate(String txid) {

        BigDecimal fee = calculateFee(txid);
        long size = calculateTransactionSize(txid);
        return fee.divide(BigDecimal.valueOf(size), BigDecimal.ROUND_HALF_UP); // Fee per byte
    }

    public long getTotalMempoolSize() {

        return client.getRawMemPool().stream()
                .mapToLong(this::calculateTransactionSize)
                .sum();
    }

    public Map<String, Object> getMempoolStatistics() {
        List<String> transactions = client.getRawMemPool();

        long totalSize = transactions.parallelStream()
                .mapToLong(this::calculateTransactionSize)
                .sum();

        BigDecimal totalFee = transactions.parallelStream()
                .map(this::calculateFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averageFee = totalFee.divide(BigDecimal.valueOf(transactions.size()), BigDecimal.ROUND_HALF_UP);
        long averageSize = totalSize / transactions.size();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTransactions", transactions.size());
        stats.put("totalSize", totalSize);
        stats.put("averageFee", averageFee);
        stats.put("averageSize", averageSize);

        return stats;
    }

    public List<BigDecimal> getFeeDistribution() {
        List<String> transactions = client.getRawMemPool();

        // Collect fees for fee distribution
        return transactions.stream()
                .map(this::calculateFee)
                .collect(Collectors.toList());
    }

    public Map<String, List<Long>> getFeeHistogramData() {

        // Get all transactions once.
        List<String> transactions = client.getRawMemPool();

        // Initialize a map to hold the histogram data.
        Map<String, List<Long>> histogramData = new HashMap<>();

        transactions.parallelStream().forEach(txid -> {
            BigDecimal feeRate = calculateFeeRate(txid);
            Long size = calculateTransactionSize(txid);

            if (feeRate.compareTo(BigDecimal.valueOf(50)) >= 0) {
                histogramData.computeIfAbsent("High Fee", k -> new ArrayList<>()).add(size);
            }
        });

        System.out.println(histogramData);

        return histogramData;
    }

}
