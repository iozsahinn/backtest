package com.optimizer.portfolio_stock.model.converter;

import com.optimizer.portfolio.model.converter.PortfolioConverter;
import com.optimizer.portfolio.repository.PortfolioRepository;
import com.optimizer.portfolio_stock.model.PortfolioStock;
import com.optimizer.portfolio_stock.model.PortfolioStockModel;
import com.optimizer.stock.model.converter.StockConverter;
import com.optimizer.stock.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PortfolioStockConverter {
    private final StockRepository stockRepository;
    private final PortfolioRepository portfolioRepository;

    @Autowired
    public PortfolioStockConverter( StockRepository stockRepository, PortfolioRepository portfolioRepository) {
        this.stockRepository = stockRepository;
        this.portfolioRepository = portfolioRepository;
    }

    /**
     * Convert PortfolioStock entity to PortfolioStockModel
     */
    public PortfolioStockModel entityToModel(PortfolioStock entity) {
        if (entity == null) {
            return null;
        }

        PortfolioStockModel model = new PortfolioStockModel();
        model.setId(entity.getId());
        model.setPercentage(entity.getPercentage());
        model.setLeverage(entity.getLeverage());

        if (entity.getStock() != null) {
            model.setStockId(entity.getStock().getId());
        }
        if (entity.getPortfolio() != null) {
            model.setPortfolioId(entity.getPortfolio().getId());
        }

        return model;
    }

    /**
     * Convert PortfolioStockModel to PortfolioStock entity
     * Note: This method requires Portfolio and Stock entities to be fetched separately
     */
    public PortfolioStock modelToEntity(PortfolioStockModel model) {
        if (model == null) {
            return null;
        }

        PortfolioStock entity = new PortfolioStock();
        if(model.getId()!=null) entity.setId(model.getId());
        entity.setLeverage(model.getLeverage());
        entity.setPercentage(model.getPercentage() != null ? model.getPercentage() : 0);
        if (model.getPortfolioId() == null) {
            throw new IllegalArgumentException("Portfolio ID is required to create/update PortfolioStock.");
        }
        if (model.getStockId() == null) {
            throw new IllegalArgumentException("Stock ID is required to create/update PortfolioStock.");
        }
        entity.setStock(stockRepository.findById
                (model.getStockId()).
                        orElseThrow(()-> new IllegalArgumentException("The stock is not found with ID "+model.getStockId())));
        entity.setPortfolio(portfolioRepository.findById
                (model.getPortfolioId()).
                orElseThrow(()->
                        new IllegalArgumentException("The portfolio is not found with ID "+model.getPortfolioId())));

        return entity;
    }


    public Set<PortfolioStockModel> toModelList(Set<PortfolioStock> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::entityToModel)
                .collect(Collectors.toSet());
    }

    /**
     * Convert list of models to entities (without setting relationships)
     */
    public Set<PortfolioStock> toEntityList(Set<PortfolioStockModel> models) {
        if (models == null) {
            return null;
        }

        return models.stream()
                .map(this::modelToEntity)
                .collect(Collectors.toSet());
    }
}