package com.optimizer.portfolio.model;

import com.optimizer.portfolio_stock.model.PortfolioStock;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.optimizer.stock.model.Stock;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Entity
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private RebalancingFrequency rebalancingFrequency;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL)
    private Set<PortfolioStock> stocks = new HashSet<>(); // Initialize collection

    private BigDecimal inflow;

    private BigDecimal initialInvestment;

    private LocalDate startDate;

    private LocalDate endDate;
}
