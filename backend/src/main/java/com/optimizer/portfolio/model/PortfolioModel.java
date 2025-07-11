package com.optimizer.portfolio.model;

import com.optimizer.portfolio_stock.model.PortfolioStock;
import com.optimizer.portfolio_stock.model.PortfolioStockModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.optimizer.stock.model.StockModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class PortfolioModel {
    private Long id;


    private String rebalancingFrequency;


    private Set<PortfolioStockModel> stocks;

    private BigDecimal inflow;
    private BigDecimal initialInvestment;
    private LocalDate startDate;

    private LocalDate endDate;
}
