package com.optimizer.stock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.optimizer.candle.model.CandleModel;
import com.optimizer.stock.model.StockModel;
import com.optimizer.stock.service.StockService;

import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin("localhost")
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public ResponseEntity<List<StockModel>> getAllStocks() {
        List<StockModel> stocks = stockService.getAllStocks();
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockModel> getStockById(@PathVariable Long id) {
        StockModel stock = stockService.getStockById(id);
        /*if (stock == null) {
            return ResponseEntity.notFound().build();
        }*/
        return ResponseEntity.ok(stock);
    }

    @PostMapping
    public ResponseEntity<StockModel> createStock(@RequestBody StockModel stockModel) {
        StockModel createdStock = stockService.createStock(stockModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStock);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockModel> updateStock(@PathVariable Long id, @RequestBody StockModel stockModel) {
        StockModel updatedStock = stockService.updateStock(id, stockModel);
        if (updatedStock == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedStock);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable Long id) {
        boolean deleted = stockService.deleteStock(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/candles")
    public ResponseEntity<List<CandleModel>> getCandlesOfStock(@PathVariable Long id) {
        var stock = stockService.getStockById(id);
        if (stock == null) {
            return ResponseEntity.notFound().build();
        }

        var candles = stock.getCandles();
        // It's better to return an empty list instead of null for no candles
        if (candles == null || candles.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(candles);
    }



}
