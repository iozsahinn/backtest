package com.optimizer.portfolio_stock.repository;

import com.optimizer.portfolio_stock.model.PortfolioStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioStockRepository extends JpaRepository<PortfolioStock, Long> {
    @Query("""
                SELECT COUNT(ps) > 0
                FROM PortfolioStock ps
                WHERE ps.portfolio.id = :portfolioId AND ps.stock.id = :stockId
""")
    boolean existsByPortfolioIdAndStockId(@Param("portfolioId")Long portfolioId,
                                          @Param("stockId")Long stockId);

    @Query("""
            SELECT SUM(ps.percentage)
            FROM Portfolio p, PortfolioStock ps
            WHERE p.id = :portfolioId AND ps.portfolio.id = :portfolioId
""")
    Short calculateTotalPortfolioPercentage(@Param("portfolioId") Long portfolioId);
}
