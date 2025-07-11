package com.optimizer.portfolio.model;

import com.optimizer.candle.model.CandleModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class PortfolioPerformanceResult {
    List<CandleModel> performance;
    List<Double> correlations;
    List<Double> yearlyCorrelations;
    public PortfolioPerformanceResult(List<CandleModel> performance, List<Double> correlations, List<Double> yearlyCorrelations) {
        this.performance = performance;
        this.correlations = correlations;
        this.yearlyCorrelations = yearlyCorrelations;
    }
}
