package com.optimizer.stock.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.optimizer.candle.model.Candle;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long id;

    @NotNull
    private String name;

    @NotNull
    @Column(unique = true)
    private String symbol;
    // Removed price field

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Candle> candles = new ArrayList<>();


    // Add a method to add a candle to the stock
    public boolean addCandle(Candle candle) {
        var flag=candles.add(candle);
        return false;
    }

    // Add a method to remove a candle from the stock
    public boolean removeCandle(Candle candle) {
        var flag=candles.remove(candle);
        return false;
    }

    // Add a method to get the latest candle (most recent)
    public Candle getLatestCandle() {
        if (candles == null || candles.isEmpty()) {
            return null;
        }
        return candles.stream()
                .max((c1, c2) -> c1.getStartTime().compareTo(c2.getStartTime()))
                .orElse(null);
    }
}
