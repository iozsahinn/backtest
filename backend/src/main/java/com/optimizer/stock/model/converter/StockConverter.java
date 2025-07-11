package com.optimizer.stock.model.converter;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import com.optimizer.stock.model.Stock;
import com.optimizer.stock.model.StockModel;
import com.optimizer.candle.model.Candle;
import com.optimizer.candle.model.CandleModel;
import com.optimizer.candle.model.converter.CandleConverter;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StockConverter {

    private final CandleConverter candleConverter;

    @Autowired
    public StockConverter(CandleConverter candleConverter) {
        this.candleConverter = candleConverter;
    }

    public Stock modelToEntity(StockModel stockModel) {
        if (stockModel == null) return null;

        Stock stock = new Stock();
        stock.setId(stockModel.getId());
        stock.setName(stockModel.getName());
        stock.setSymbol(stockModel.getSymbol());

        if (stockModel.getCandles() != null && !stockModel.getCandles().isEmpty()) {
            List<Candle> candles = stockModel.getCandles().stream()
                    .map(candleConverter::candleModelToEntity)
                    .collect(Collectors.toList());
            stock.setCandles(candles);
        }

        return stock;
    }

    public StockModel entityToModel(Stock stock) {
        if (stock == null) return null;

        StockModel stockModel = new StockModel();
        stockModel.setId(stock.getId());
        stockModel.setName(stock.getName());
        stockModel.setSymbol(stock.getSymbol()); // Fixed


        if (stock.getCandles() != null && !stock.getCandles().isEmpty()) {
            stockModel.setCandles(
                    stock.getCandles().stream()
                            .map(candleConverter::entityToCandleModel)
                            .collect(Collectors.toList())
            );
        }

        return stockModel;
    }
    public List<StockModel> entitiesToModels(List<Stock> stocks) {
        return stocks.stream()
                .map(this::entityToModel)
                .collect(Collectors.toList());
    }
    public List<Stock> modelsToEntities(List<StockModel> stockModels) {
        return stockModels.stream()
                .map(this::modelToEntity)
                .collect(Collectors.toList());
    }
}
