package com.optimizer.stock.model;

import lombok.Getter;
import lombok.Setter;
import com.optimizer.candle.model.CandleModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
public class StockModel {

    private String name;
    private List<CandleModel> candles = new ArrayList<>();
    private Long id;
    private String symbol;

    // Add a method to get the latest candle (most recent)
    public CandleModel getLatestCandle() {
        return candles.stream()
                .max(Comparator.comparing(CandleModel::getStartTime))
                .orElse(null);
    }
}
