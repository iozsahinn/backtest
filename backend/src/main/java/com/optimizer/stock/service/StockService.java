package com.optimizer.stock.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.optimizer.candle.model.Candle;
import com.optimizer.candle.model.CandleModel;
import com.optimizer.candle.model.converter.CandleConverter;
import com.optimizer.candle.repository.CandleRepository;
import com.optimizer.stock.model.Stock;
import com.optimizer.stock.model.StockModel;
import com.optimizer.stock.model.converter.StockConverter;
import com.optimizer.stock.repository.StockRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StockService {

    private final StockRepository stockRepository;
    private final StockConverter stockConverter;
    private final CandleRepository candleRepository;
    private final CandleConverter candleConverter;
    @Autowired
    public StockService(StockRepository stockRepository,
                        StockConverter stockConverter,
                        CandleRepository candleRepository,
                        CandleConverter candleConverter) {
        this.stockRepository = stockRepository;
        this.stockConverter = stockConverter;
        this.candleRepository = candleRepository;
        this.candleConverter = candleConverter;
    }

    public List<StockModel> getAllStocks() {
        return stockRepository.findAll().stream()
                .map(stockConverter::entityToModel)
                .collect(Collectors.toList());
    }

    public StockModel getStockById(Long id) {
        Optional<Stock> stockOptional = stockRepository.findById(id);
        return stockOptional.map(stockConverter::entityToModel).orElse(null);
    }

    public StockModel createStock(StockModel stockModel) {
        Stock stock = stockConverter.modelToEntity(stockModel);
        Stock savedStock = stockRepository.save(stock);
        return stockConverter.entityToModel(savedStock);
    }

    public StockModel updateStock(Long id, StockModel stockModel) {
        Optional<Stock> existingStockOpt = stockRepository.findById(id);
        if (existingStockOpt.isEmpty()) {
            return null;
        }

        // Convert basic fields without StockLevel
        Stock stock = stockConverter.modelToEntity(stockModel);
        stock.setId(id);


        Stock updatedStock = stockRepository.save(stock);
        return stockConverter.entityToModel(updatedStock);
    }

    public boolean deleteStock(Long id) {
        if (stockRepository.existsById(id)) {
            stockRepository.deleteById(id);
            return true;
        }
        return false;
    }




}
