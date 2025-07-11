package com.optimizer.candle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.optimizer.candle.model.CandleModel;
import com.optimizer.candle.service.CandleService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/candles")
public class CandleController {

    private final CandleService candleService;

    @Autowired
    public CandleController(CandleService candleService) {
        this.candleService = candleService;
    }

    @GetMapping
    public ResponseEntity<List<CandleModel>> getAllCandles() {
        List<CandleModel> candles = candleService.getAllCandles();
        return new ResponseEntity<>(candles, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandleModel> getCandleById(@PathVariable Long id) {
        CandleModel candle = candleService.getCandleById(id);
        if (candle != null) {
            return new ResponseEntity<>(candle, HttpStatus.OK);
        }
        return new ResponseEntity<>(candle,HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CandleModel> createCandle(@RequestBody CandleModel candleModel) {
        CandleModel createdCandle = candleService.createCandle(candleModel);
        return new ResponseEntity<>(createdCandle, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CandleModel> updateCandle(@PathVariable Long id, @RequestBody CandleModel candleModel) {
        CandleModel updatedCandle = candleService.updateCandle(id, candleModel);
        if (updatedCandle != null) {
            return new ResponseEntity<>(updatedCandle, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandle(@PathVariable Long id) {
        boolean deleted = candleService.deleteCandle(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
