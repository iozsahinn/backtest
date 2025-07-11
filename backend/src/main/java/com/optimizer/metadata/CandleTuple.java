package com.optimizer.metadata;

import com.optimizer.candle.model.Candle;
import com.optimizer.candle.model.CandleModel;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CandleTuple {
    private final CandleModel currentCandle;
    private final BigDecimal money_in_stock;
    @Getter
    private BigDecimal win=BigDecimal.ZERO;
    public CandleTuple(CandleModel currentCandle,BigDecimal money_in_stock){
        this.currentCandle = currentCandle;
        this.money_in_stock = money_in_stock;
        calculateWin();
    }
    private void calculateWin() {
        BigDecimal open = currentCandle.getOpenValue();
        this.win = (currentCandle.getCloseValue()
                .subtract(open))
                .divide(open, 5, RoundingMode.HALF_UP)
                .multiply(money_in_stock)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
