package com.optimizer.candle.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Getter
@Setter
public class CandleModel {
    private Long id;
    private BigDecimal openValue;
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
