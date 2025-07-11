package com.optimizer.portfolio_stock.controller;

import com.optimizer.portfolio_stock.model.PortfolioStockModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface IPortfolioStockController {
    ResponseEntity<PortfolioStockModel> createPortfolioStock(PortfolioStockModel model);

    ResponseEntity<PortfolioStockModel> updatePortfolioStock(PortfolioStockModel model, Long id);

    ResponseEntity<Void> deletePortfolioStock(Long portfolioStockId);

    ResponseEntity<PortfolioStockModel> getPortfolioStockById(Long id);
}
