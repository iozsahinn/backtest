package com.optimizer.portfolio_stock.model;

import com.optimizer.portfolio.model.Portfolio;
import com.optimizer.stock.model.Stock;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Getter
public class PortfolioStock {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "portfolio_stock_id")
    @Setter()
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false,targetEntity =  Stock.class)
    @Setter()
    @JoinColumn(name= "stock_id")
    private Stock stock;

    @Min(-5)
    @Max(5)
    private Double leverage=1d;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id",nullable = false)
    @Setter()
    private Portfolio portfolio;

    private Short percentage=0;

    public void setPercentage(short percentage) {
        if(percentage<0){
            throw new IllegalArgumentException("percentage cannot be negative");
        }
        if(percentage > 100){
            throw new IllegalArgumentException("percentage cannot be greater than 100");
        }

        this.percentage=percentage;

    }

    public Set<PortfolioStock> getPortfoliosOtherStocks(){
        return this.portfolio.getStocks();
    }
    public void setLeverage(Double leverage) {
        if(leverage== null) throw new IllegalArgumentException("leverage cannot be null");
        if (leverage < -5) throw new IllegalArgumentException("leverage cannot be lower than -5");
        if (leverage > 5) throw new IllegalArgumentException("leverage cannot be higher than 5");
        this.leverage = leverage;
    }
}
