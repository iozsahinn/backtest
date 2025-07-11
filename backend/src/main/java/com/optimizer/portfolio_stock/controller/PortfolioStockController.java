package com.optimizer.portfolio_stock.controller;

import com.optimizer.portfolio_stock.model.PortfolioStockModel;
import com.optimizer.portfolio_stock.service.IPortfolioStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.web.servlet.function.ServerResponse.ok;

@RestController
@RequestMapping("/api/portfolio-stocks")
public class PortfolioStockController implements IPortfolioStockController {

    private final IPortfolioStockService portfolioStockService;

    @Autowired
    public PortfolioStockController(IPortfolioStockService portfolioStockService) {
        this.portfolioStockService = portfolioStockService;
    }
    @Override
    @PostMapping
    public ResponseEntity<PortfolioStockModel> createPortfolioStock(@RequestBody PortfolioStockModel model) {
        PortfolioStockModel created = portfolioStockService.createPortfolioStock(model);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<PortfolioStockModel>> getPortfolioStock() {
        return ResponseEntity.ok(null);
    }


    @Override
    @PutMapping("/{id}")
    public ResponseEntity<PortfolioStockModel> updatePortfolioStock(@RequestBody PortfolioStockModel model, @PathVariable Long id) {
        PortfolioStockModel updated = portfolioStockService.updatePortfolioStock(model);
        return ResponseEntity.ok(updated);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePortfolioStock(@PathVariable Long id) {
        portfolioStockService.deletePortfolioStock(id);
        return ResponseEntity.ok(null);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<PortfolioStockModel> getPortfolioStockById(@PathVariable Long id) {
        return ResponseEntity.ok().body(portfolioStockService.getPortfolioStock(id));
    }


}
