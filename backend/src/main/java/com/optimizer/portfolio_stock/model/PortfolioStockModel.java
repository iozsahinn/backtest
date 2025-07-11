package com.optimizer.portfolio_stock.model;

import com.optimizer.portfolio.model.Portfolio;
import com.optimizer.portfolio.model.PortfolioModel;
import com.optimizer.stock.model.Stock;
import com.optimizer.stock.model.StockModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PortfolioStockModel {
    private Long id;

    private Double leverage;
    private Long stockId;
    private Long portfolioId;
    private Short percentage;
}
