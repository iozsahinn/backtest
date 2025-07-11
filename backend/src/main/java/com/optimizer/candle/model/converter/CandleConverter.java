package com.optimizer.candle.model.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.optimizer.candle.model.Candle;
import com.optimizer.candle.model.CandleModel;
import com.optimizer.stock.repository.StockRepository;

import java.util.List;

@Component
public class CandleConverter {

    private final StockRepository stockRepository;

    @Autowired
    public CandleConverter(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public Candle candleModelToEntity(CandleModel candleModel) {
        Candle candle = new Candle();
        candle.setId(candleModel.getId());
        candle.setOpenValue(candleModel.getOpenValue());
        candle.setCloseValue(candleModel.getCloseValue());
        candle.setStartTime(candleModel.getStartTime());

        return candle;
    }

    public CandleModel entityToCandleModel(Candle candle) {
        CandleModel candleModel = new CandleModel();
        candleModel.setId(candle.getId());
        candleModel.setOpenValue(candle.getOpenValue());
        candleModel.setCloseValue(candle.getCloseValue());
        candleModel.setStartTime(candle.getStartTime());

        
        return candleModel;
    }
    public List<CandleModel> entityToModelList(List<Candle> candles) {
        if (candles == null || candles.isEmpty()) {
            return List.of();
        }
        return candles.stream()
                .map(this::entityToCandleModel)
                .toList();
    }

    public List<Candle> modelToEntityList(List<CandleModel> candleModels) {
        if (candleModels == null || candleModels.isEmpty()) {
            return List.of();
        }
        return candleModels.stream()
                .map(this::candleModelToEntity)
                .toList();
    }
}