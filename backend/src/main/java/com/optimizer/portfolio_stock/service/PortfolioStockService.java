package com.optimizer.portfolio_stock.service;

import com.optimizer.portfolio.model.Portfolio;
import com.optimizer.portfolio.repository.PortfolioRepository;
import com.optimizer.portfolio_stock.model.PortfolioStock;
import com.optimizer.portfolio_stock.model.PortfolioStockModel;
import com.optimizer.portfolio_stock.model.converter.PortfolioStockConverter;
import com.optimizer.portfolio_stock.repository.PortfolioStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;
import java.util.Objects;

@Service
@Transactional
public class PortfolioStockService implements IPortfolioStockService {
    private final PortfolioRepository portfolioRepository;
    private final PortfolioStockRepository portfolioStockRepository;
    private final PortfolioStockConverter portfolioStockConverter;

    @Autowired
    public PortfolioStockService(PortfolioRepository portfolioRepository, PortfolioStockRepository portfolioStockRepository, PortfolioStockConverter portfolioStockConverter) {
        this.portfolioRepository = portfolioRepository;
        this.portfolioStockRepository = portfolioStockRepository;
        this.portfolioStockConverter = portfolioStockConverter;
    }

    @Override
    public PortfolioStockModel createPortfolioStock(PortfolioStockModel portfolioStockModel) {
        Objects.requireNonNull(portfolioStockModel, "portfolioStockModel is not valid");
        var portfolio=portfolioRepository.findById(portfolioStockModel.getPortfolioId())
                .orElseThrow(() -> new RuntimeException("Portfolio Not Found"));
        checkStockAlreadyExistsInPortfolio(portfolioStockModel);
        checkTotalPercentage(portfolioStockModel);
        var returned= portfolioStockRepository.
                save(portfolioStockConverter.modelToEntity(portfolioStockModel));
        portfolioRepository.save(portfolio);
        return portfolioStockConverter.entityToModel(returned);
    }

    @Override
    public PortfolioStockModel getPortfolioStock(Long id) {
        var entity=portfolioStockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Portfolio Not Found"));
        return portfolioStockConverter.entityToModel(entity);
    }

    @Override
    public PortfolioStockModel deletePortfolioStock(Long id) {
        var entity=portfolioStockRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Portfolio Not Found"));
        portfolioStockRepository.deleteById(id);
        return portfolioStockConverter.entityToModel(entity);
    }

    @Override
    public PortfolioStockModel updatePortfolioStock(PortfolioStockModel portfolioStockModel) {
        Objects.requireNonNull(portfolioStockModel, "portfolioStockModel is not valid");
        portfolioRepository.findById(portfolioStockModel.getPortfolioId())
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio Not Found"));
        checkTotalPercentage(portfolioStockModel);
        var returned= portfolioStockRepository.
                save(portfolioStockConverter.modelToEntity(portfolioStockModel));
        return portfolioStockConverter.entityToModel(returned);
    }

    @Override
    public Short calculateTotalPortfolioPercentage(Long portfolioId) {
        Short total = portfolioStockRepository.calculateTotalPortfolioPercentage(portfolioId);
        return total != null ? total : 0;

    }

    private void checkTotalPercentage(PortfolioStockModel portfolioStockModel)  {
        if (portfolioStockModel.getPercentage()<0)
            throw new RuntimeException("Stock's percentage is less than 0");
        if (portfolioStockModel.getPercentage()>100)
            throw new RuntimeException("Stock's percentage is greater than 100");
        Portfolio portfolio = portfolioRepository.findById(portfolioStockModel.getPortfolioId())
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio Not Found"));

        int totalWithoutThis = portfolio.getStocks().stream()
                .filter(s -> !s.getId().equals(portfolioStockModel.getId()))
                .mapToInt(PortfolioStock::getPercentage)
                .sum();

        int newTotal = totalWithoutThis + portfolioStockModel.getPercentage();
        if (newTotal > 100) {
            throw new IllegalArgumentException("Total percentage exceeds 100%");
        }
    }
    private void checkStockAlreadyExistsInPortfolio(PortfolioStockModel portfolioStockModel) {
        if(portfolioStockRepository.existsByPortfolioIdAndStockId(portfolioStockModel.getPortfolioId(),portfolioStockModel.getStockId()))
                throw new RuntimeException("Stock already exists in portfolio");

    }
}
