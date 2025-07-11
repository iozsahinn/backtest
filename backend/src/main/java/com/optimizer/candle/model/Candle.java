package com.optimizer.candle.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.optimizer.stock.model.Stock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Candle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "candle_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @Column(precision = 38, scale = 10)
    private BigDecimal openValue;

   @Column(precision = 38, scale = 10)
   private BigDecimal closeValue;

    private LocalDate startTime;

    public void setOpenValue(BigDecimal openValue) {
        if (openValue != null) {
            // Validate openValue is not negative
            if (openValue.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Open value cannot be less than 0");
            }
            this.openValue = openValue.setScale(10, RoundingMode.HALF_UP);
        } else {
            this.openValue = null;
        }
    }

    public void setCloseValue(BigDecimal closeValue) {
        if (closeValue != null) {
            // Validate closeValue is not negative
            if (closeValue.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Close value cannot be less than 0");
            }
            this.closeValue = closeValue.setScale(10, RoundingMode.HALF_UP);
        } else {
            this.closeValue = null;
        }
    }


}
