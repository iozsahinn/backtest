package com.optimizer.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.optimizer.portfolio.model.Portfolio;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

}