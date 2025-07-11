package com.optimizer.metadata;

import com.optimizer.candle.model.CandleModel;
import com.optimizer.portfolio.model.RebalancingFrequency;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.IsoFields;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CandleGrouper {


    public static List<List<CandleModel>> divideByRebalancingFrequency(List<CandleModel> candles, RebalancingFrequency frequency) {
        if (candles == null || candles.isEmpty()) {
            return Collections.emptyList();
        }

        // Choose grouping key function based on frequency
        Function<CandleModel, String> keyFunction = getGroupingKeyFunction(frequency);

        return candles.stream()
                .collect(Collectors.groupingBy(keyFunction)) // Group by frequency
                .values().stream()
                .map(list -> list.stream()
                        .sorted(Comparator.comparing(CandleModel::getStartTime)) // Sort each group by time
                        .collect(Collectors.toList()))
                .sorted(Comparator.comparing(list -> list.getFirst().getStartTime())) // Sort the outer list by first candle in each group
                .collect(Collectors.toList());
    }

    private static Function<CandleModel, String> getGroupingKeyFunction(RebalancingFrequency frequency) {
        return switch (frequency) {
            case DAILY -> c -> c.getStartTime().toString();
            case WEEKLY -> c -> {
                LocalDate date = c.getStartTime();
                int week = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                int year = date.get(IsoFields.WEEK_BASED_YEAR);
                return year + "-W" + String.format("%02d", week);
            };
            case MONTHLY -> c -> {
                YearMonth ym = YearMonth.from(c.getStartTime());
                return ym.toString();
            };
            case QUARTERLY -> c -> {
                LocalDate date = c.getStartTime();
                int quarter = (date.getMonthValue() - 1) / 3 + 1;
                return date.getYear() + "-Q" + quarter;
            };
            case YEARLY -> c -> String.valueOf(c.getStartTime().getYear());
            default ->  throw new IllegalArgumentException("Unsupported frequency: " + frequency);
        };
    }
}
