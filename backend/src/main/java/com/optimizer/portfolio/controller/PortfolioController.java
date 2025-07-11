package com.optimizer.portfolio.controller;

import com.optimizer.candle.model.CandleModel;
import com.optimizer.portfolio.model.PortfolioPerformanceResult;
import com.optimizer.portfolio.model.RebalancingFrequency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.optimizer.portfolio.model.PortfolioModel;
import com.optimizer.portfolio.service.PortfolioService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    @Autowired
    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public ResponseEntity<List<PortfolioModel>> getAllPortfolios() {
        List<PortfolioModel> portfolios = portfolioService.getAllPortfolios();
        return ResponseEntity.ok(portfolios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PortfolioModel> getPortfolioById(@PathVariable Long id) {
        PortfolioModel portfolio = portfolioService.getPortfolioById(id);
        if (portfolio == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(portfolio);
    }

    @PostMapping
    public ResponseEntity<PortfolioModel> createPortfolio(@RequestBody PortfolioModel portfolioModel) {
        PortfolioModel createdPortfolio = portfolioService.createPortfolio(portfolioModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPortfolio);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PortfolioModel> updatePortfolio(@PathVariable Long id, @RequestBody PortfolioModel portfolioModel) {
        PortfolioModel updatedPortfolio = portfolioService.updatePortfolio(id, portfolioModel);
        if (updatedPortfolio == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedPortfolio);
    }
    @GetMapping(value = "/{id}/performance", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PortfolioPerformanceResult> calculatePortfolioPerformance(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(portfolioService.calculateDailyPortfolioPerformance(id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable Long id) {
        boolean deleted = portfolioService.deletePortfolio(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/rebalance-frequency")
    public ResponseEntity<RebalancingFrequency> getRebalancingFrequency(@PathVariable Long id) {
        if(portfolioService.getRebalancingFrequency(id) == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(portfolioService.getRebalancingFrequency(id));
    }

    @PutMapping("/{id}/rebalance-frequency")
    public ResponseEntity<Void> updateRebalancingFrequency(@PathVariable Long id, @RequestBody RebalancingFrequency frequency) {
        boolean updated = portfolioService.updateRebalancingFrequency(id, frequency);
        if (!updated) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}