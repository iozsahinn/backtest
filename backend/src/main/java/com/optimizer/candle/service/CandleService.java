package com.optimizer.candle.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.optimizer.candle.model.Candle;
import com.optimizer.candle.model.CandleModel;
import com.optimizer.candle.model.converter.CandleConverter;
import com.optimizer.candle.repository.CandleRepository;
import com.optimizer.stock.model.Stock;
import com.optimizer.stock.repository.StockRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CandleService {

    private final CandleRepository candleRepository;
    private final CandleConverter candleConverter;

    @Autowired
    public CandleService(CandleRepository candleRepository,
                         CandleConverter candleConverter) {
        this.candleRepository = candleRepository;
        this.candleConverter = candleConverter;
    }

    public List<CandleModel> getAllCandles() {
        return candleRepository.findAll().stream()
                .map(candleConverter::entityToCandleModel)
                .collect(Collectors.toList());
    }

    public CandleModel getCandleById(Long id) {
        Optional<Candle> candleOptional = candleRepository.findById(id);
        return candleOptional.map(candleConverter::entityToCandleModel).orElse(null);
    }


    public CandleModel createCandle(CandleModel candleModel) {
        Candle candle = candleConverter.candleModelToEntity(candleModel);
        Candle savedCandle = candleRepository.save(candle);
        return candleConverter.entityToCandleModel(savedCandle);
    }

    public CandleModel updateCandle(Long id, CandleModel candleModel) {
        if (candleRepository.existsById(id)) {
            Candle candle = candleConverter.candleModelToEntity(candleModel);
            candle.setId(id);
            Candle updatedCandle = candleRepository.save(candle);
            return candleConverter.entityToCandleModel(updatedCandle);
        }
        return null;
    }

    public boolean deleteCandle(Long id) {
        if (candleRepository.existsById(id)) {
            candleRepository.deleteById(id);
            return true;
        }
        return false;
    }
}