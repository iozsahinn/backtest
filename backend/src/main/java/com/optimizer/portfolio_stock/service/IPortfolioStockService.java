package com.optimizer.portfolio_stock.service;

import com.optimizer.portfolio.model.PortfolioModel;
import com.optimizer.portfolio_stock.model.PortfolioStockModel;

public interface IPortfolioStockService {
    PortfolioStockModel createPortfolioStock(PortfolioStockModel portfolioStockModel) ;
    PortfolioStockModel getPortfolioStock(Long id);
    PortfolioStockModel deletePortfolioStock(Long id);
    PortfolioStockModel updatePortfolioStock(PortfolioStockModel portfolioStockModel) ;
    Short calculateTotalPortfolioPercentage(Long portfolioId);
}
