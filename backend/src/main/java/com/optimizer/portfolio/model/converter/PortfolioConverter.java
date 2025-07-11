package com.optimizer.portfolio.model.converter;

import com.optimizer.portfolio.model.RebalancingFrequency;
import com.optimizer.portfolio_stock.model.PortfolioStock;
import com.optimizer.portfolio_stock.model.PortfolioStockModel;
import com.optimizer.portfolio_stock.model.converter.PortfolioStockConverter;
import org.springframework.stereotype.Component;
import com.optimizer.portfolio.model.Portfolio;
import com.optimizer.portfolio.model.PortfolioModel;
import com.optimizer.stock.model.StockModel;
import com.optimizer.stock.model.converter.StockConverter;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PortfolioConverter {

    private final PortfolioStockConverter portfolioStockConverter;
    public PortfolioConverter(StockConverter stockConverter, PortfolioStockConverter portfolioStockConverter) {
        this.portfolioStockConverter = portfolioStockConverter;
    }
    
    public Portfolio portfolioModelToEntity(PortfolioModel portfolioModel) {
        if (portfolioModel == null) {
            return null;
        }
        
        Portfolio portfolio = new Portfolio();
        portfolio.setId(portfolioModel.getId());
        portfolio.setEndDate(portfolioModel.getEndDate());
        portfolio.setStartDate(portfolioModel.getStartDate());
        portfolio.setRebalancingFrequency(RebalancingFrequency.valueOf(portfolioModel.getRebalancingFrequency()));
        portfolio.setInflow(portfolioModel.getInflow());
        portfolio.setInitialInvestment(portfolioModel.getInitialInvestment());
        portfolio.setStocks(
                portfolioModel.getStocks() != null ?
                portfolioModel.getStocks().stream()
                        .map(portfolioStockConverter::modelToEntity)
                        .collect(Collectors.toSet()) :
                        Collections.emptySet()
        );
        return portfolio;
    }
    
    public PortfolioModel entityToPortfolioModel(Portfolio portfolio) {
        if (portfolio == null) {
            return null;
        }
        
        PortfolioModel portfolioModel = new PortfolioModel();
        portfolioModel.setId(portfolio.getId());
        portfolioModel.setRebalancingFrequency(portfolio.getRebalancingFrequency().toString());
        portfolioModel.setEndDate(portfolio.getEndDate());
        portfolioModel.setStartDate(portfolio.getStartDate());
        portfolioModel.setInflow(portfolio.getInflow());
        portfolioModel.setInitialInvestment(portfolio.getInitialInvestment());
        portfolioModel.setStocks(
                portfolio.getStocks() != null ?
                portfolio.getStocks().stream()
                        .map(portfolioStockConverter::entityToModel)
                        .collect(Collectors.toSet()) :
                        Collections.emptySet()
        );
        return portfolioModel;
    }
    public Portfolio portfolioModelToEntityWithoutStocks(PortfolioModel portfolioModel) {
            if (portfolioModel == null) {
                return null;
            }
            Portfolio portfolio = new Portfolio();
            portfolio.setEndDate(portfolioModel.getEndDate());
            portfolio.setStartDate(portfolioModel.getStartDate());
            portfolio.setRebalancingFrequency(RebalancingFrequency.valueOf(portfolioModel.getRebalancingFrequency()));
            portfolio.setInflow(portfolioModel.getInflow());
            portfolio.setInitialInvestment(portfolioModel.getInitialInvestment());
            portfolio.setStocks(Collections.emptySet());
            return portfolio;
        }
    // PortfolioStockModel listesini entity'ye çevirip, kaydedilen portfolio ile ilişkilendir
    public List<PortfolioStock> portfolioStockModelsToEntities(Set<PortfolioStockModel> stockModels, Portfolio savedPortfolio) {
        if (stockModels == null) {
            return Collections.emptyList();
        }
        return stockModels.stream()
                .map(model -> {
                    PortfolioStock entity = portfolioStockConverter.modelToEntity(model);
                    entity.setPortfolio(savedPortfolio);
                    return entity;
                })
                .collect(Collectors.toList());
    }
}