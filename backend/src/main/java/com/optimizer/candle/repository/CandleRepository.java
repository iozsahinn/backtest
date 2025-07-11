package com.optimizer.candle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.optimizer.candle.model.Candle;
import com.optimizer.stock.model.Stock;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CandleRepository extends JpaRepository<Candle, Long> {
    List<Candle> findByStockIdOrderByStartTime(Long stockId);
    List<Candle> findByStockIdAndStartTimeBetweenOrderByStartTime(Long stockId, LocalDate startDate, LocalDate endDate);

}