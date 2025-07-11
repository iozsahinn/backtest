package com.optimizer.portfolio.service;

import com.optimizer.candle.model.Candle;
import com.optimizer.candle.model.CandleModel;
import com.optimizer.candle.model.converter.CandleConverter;
import com.optimizer.candle.repository.CandleRepository;
import com.optimizer.portfolio.model.PortfolioPerformanceResult;
import com.optimizer.portfolio.model.RebalancingFrequency;
import com.optimizer.portfolio_stock.model.PortfolioStockModel;
import com.optimizer.portfolio_stock.model.converter.PortfolioStockConverter;
import com.optimizer.portfolio_stock.repository.PortfolioStockRepository;
import com.optimizer.stock.model.Stock;
import com.optimizer.stock.model.converter.StockConverter;
import com.optimizer.stock.repository.StockRepository;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import com.optimizer.portfolio.model.Portfolio;
import com.optimizer.portfolio.model.PortfolioModel;
import com.optimizer.portfolio.model.converter.PortfolioConverter;
import com.optimizer.portfolio.repository.PortfolioRepository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.InvalidParameterException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalField;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioConverter portfolioConverter;
    private final CandleConverter candleConverter;
    private final PortfolioStockRepository portfolioStockRepository;
    private final CandleRepository candleRepository;
    private final PortfolioStockConverter portfolioStockConverter;
    private final StockRepository stockRepository;

    @Autowired
    public PortfolioService(PortfolioRepository portfolioRepository, PortfolioConverter portfolioConverter, CandleConverter candleConverter, StockRepository stockRepository, PortfolioStockRepository stockRepository1, CandleRepository candleRepository, PortfolioStockConverter portfolioStockConverter, StockRepository stockRepository2) {
        this.portfolioRepository = portfolioRepository;
        this.portfolioConverter = portfolioConverter;
        this.candleConverter = candleConverter;
        this.portfolioStockRepository = stockRepository1;
        this.candleRepository = candleRepository;
        this.portfolioStockConverter = portfolioStockConverter;
        this.stockRepository = stockRepository2;
    }

    public List<PortfolioModel> getAllPortfolios() {
        return portfolioRepository.findAll().stream()
                .map(portfolioConverter::entityToPortfolioModel)
                .collect(Collectors.toList());
    }

    public PortfolioModel getPortfolioById(Long id) {
        Optional<Portfolio> portfolioOptional = portfolioRepository.findById(id);
        return portfolioOptional.map(portfolioConverter::entityToPortfolioModel).orElse(null);
    }

    @Transactional
   public PortfolioModel createPortfolio(PortfolioModel portfolioModel) {
       Portfolio portfolio = portfolioConverter.portfolioModelToEntityWithoutStocks(portfolioModel);
       Portfolio savedPortfolio = portfolioRepository.save(portfolio);
       portfolioModel.setId(savedPortfolio.getId());
       LocalDate startDate = portfolioModel.getStartDate();
       LocalDate endDate = portfolioModel.getEndDate();
       for (PortfolioStockModel stockModel : portfolioModel.getStocks()) {
           stockModel.setPortfolioId(savedPortfolio.getId());
       }
       return updatePortfolio(savedPortfolio.getId(), portfolioModel);
   }


    public PortfolioModel updatePortfolio(Long id, PortfolioModel portfolioModel) {
        return portfolioRepository.findById(id)
                .map(existingPortfolio -> {

                    // For example:
                    existingPortfolio.setEndDate(portfolioModel.getEndDate());
                    existingPortfolio.setStartDate(portfolioModel.getStartDate());
                    existingPortfolio.setRebalancingFrequency(RebalancingFrequency.valueOf(portfolioModel.getRebalancingFrequency()));
                    existingPortfolio.setInflow(portfolioModel.getInflow());
                    existingPortfolio.setInitialInvestment(portfolioModel.getInitialInvestment());
                    existingPortfolio.setStocks(portfolioStockConverter.toEntityList(portfolioModel.getStocks()));
                    // ... set other fields as needed

                    Portfolio updatedPortfolio = portfolioRepository.save(existingPortfolio);
                    return portfolioConverter.entityToPortfolioModel(updatedPortfolio);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with id " + id));
    }

    public boolean deletePortfolio(Long id) {
        if (portfolioRepository.existsById(id)) {
            portfolioRepository.deleteById(id);
            return true;
        }
        return false;
    }
    public RebalancingFrequency  getRebalancingFrequency(Long id) {
        return portfolioRepository.findById(id)
                .map(Portfolio::getRebalancingFrequency)
                .orElse(null);
    }
    public boolean updateRebalancingFrequency(Long id, RebalancingFrequency frequency) {
        Optional<Portfolio> portfolioOpt = portfolioRepository.findById(id);
        if (portfolioOpt.isPresent()) {
            Portfolio portfolio = portfolioOpt.get();
            portfolio.setRebalancingFrequency(frequency);
            portfolioRepository.save(portfolio);
            return true;
        }
        return false;
    }

    public PortfolioPerformanceResult calculateDailyPortfolioPerformance(Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new InvalidParameterException("Portfolio not found"));

        PortfolioModel portModel = portfolioConverter.entityToPortfolioModel(portfolio);
        BigDecimal inflow = portModel.getInflow();
        BigDecimal totalCapital = portModel.getInitialInvestment();
        YearMonth lastInflowMonth = null;
        var frequency= portModel.getRebalancingFrequency();
        // Load all stock data in bulk
        Map<Long, PortfolioStockModel> stockMap = portModel.getStocks().stream()
                .collect(Collectors.toMap(PortfolioStockModel::getStockId, Function.identity()));

        Map<Long, List<CandleModel>> stockCandles = new HashMap<>();
        for (Long stockId : stockMap.keySet()) {
            List<Candle> candles = candleRepository.findByStockIdAndStartTimeBetweenOrderByStartTime(stockId, portfolio.getStartDate(), portfolio.getEndDate());
            List<CandleModel> models = candleConverter.entityToModelList(candles);
            stockCandles.put(stockId, models);
        }

        // Prepare per-stock capital
        Map<Long, BigDecimal> stockCapital = new HashMap<>();
        BigDecimal equalAlloc = totalCapital.divide(BigDecimal.valueOf(stockMap.size()), 10, RoundingMode.HALF_UP);
        stockMap.keySet().forEach(id -> stockCapital.put(id, equalAlloc));

        // Daily timeline based on all candles
        TreeSet<LocalDate> allDates = stockCandles.values().stream()
                .flatMap(List::stream)
                .map(CandleModel::getStartTime)
                .collect(Collectors.toCollection(TreeSet::new));

        List<CandleModel> dailyPortfolio = new ArrayList<>();
        int dayCounter = 0;

        for (LocalDate date : allDates) {
            YearMonth currentMonth = YearMonth.from(date);

            // Apply monthly inflow
            if (lastInflowMonth == null || currentMonth.isAfter(lastInflowMonth)) {
                totalCapital = totalCapital.add(inflow);
                BigDecimal inflowPerStock = inflow.divide(BigDecimal.valueOf(stockMap.size()), 10, RoundingMode.HALF_UP);
                for (Long id : stockCapital.keySet()) {
                    stockCapital.put(id, stockCapital.get(id).add(inflowPerStock));
                }
                lastInflowMonth = currentMonth;
            }

            // Rebalance if it's a rebalance day
            if (shouldRebalance(date, RebalancingFrequency.valueOf(frequency), dayCounter)) {
                BigDecimal perStock = totalCapital.divide(BigDecimal.valueOf(stockMap.size()), 10, RoundingMode.HALF_UP);
                stockCapital.keySet().forEach(id -> stockCapital.put(id, perStock));
            }

            // Apply daily return with leverage
            for (Map.Entry<Long, List<CandleModel>> entry : stockCandles.entrySet()) {
                Long stockId = entry.getKey();
                List<CandleModel> candles = entry.getValue();
                Optional<CandleModel> todayCandleOpt = candles.stream()
                        .filter(c -> c.getStartTime().equals(date))
                        .findFirst();

                if (todayCandleOpt.isPresent()) {
                    CandleModel candle = todayCandleOpt.get();
                    BigDecimal open = candle.getOpenValue();
                    BigDecimal close = candle.getCloseValue();

                    if (open.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal dailyReturn = close.subtract(open)
                                .divide(open, 10, RoundingMode.HALF_UP);

                        BigDecimal leverage = stockMap.get(stockId).getLeverage() != null
                                ? BigDecimal.valueOf(stockMap.get(stockId).getLeverage())
                                : BigDecimal.ONE;

                        BigDecimal leveragedReturn = dailyReturn.multiply(leverage);
                        BigDecimal multiplier = BigDecimal.ONE.add(leveragedReturn);

                        if (multiplier.compareTo(BigDecimal.ZERO) < 0) {
                            multiplier = BigDecimal.ZERO;
                        }

                        BigDecimal capital = stockCapital.get(stockId);
                        stockCapital.put(stockId, capital.multiply(multiplier));
                    }
                }
            }

            // Sum total capital
            totalCapital = stockCapital.values().stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);

            CandleModel snapshot = new CandleModel();
            snapshot.setStartTime(date);
            snapshot.setOpenValue(null); // optional
            snapshot.setCloseValue(totalCapital);
            dailyPortfolio.add(snapshot);

            dayCounter++;
        }

        PortfolioPerformanceResult result = new PortfolioPerformanceResult(dailyPortfolio
                , calculateCorelationVsM2Shift(dailyPortfolio,6L)
                ,calculateYearlyCorrelation(dailyPortfolio, 6L,dailyPortfolio.getFirst().getStartTime().getYear(),dailyPortfolio.getLast().getStartTime().getYear()) );
        return result;
    }

    public List<Double> calculateCorelationVsM2Shift(List<CandleModel> portfolioPerformanceDaily, Long m2StockId) {
        if (portfolioPerformanceDaily.isEmpty()) {
            throw new InvalidParameterException("Portfolio performance data is empty or has no daily entries");
        }

        var m2Stock = stockRepository.findById(m2StockId);
        List<CandleModel> m2StockModel = m2Stock.map(Stock::getCandles).map(candleConverter::entityToModelList)
                .orElseThrow(() -> new InvalidParameterException("M2 Stock not found"));
        if (portfolioPerformanceDaily.isEmpty()) {
            throw new InvalidParameterException("Portfolio performance data is empty or has no monthly entries");
        }

        ArrayList<Double> correlections = new ArrayList<>(24);
        for (int i = 0; i < 24; i++) {
           final int shift = i;
                    var shiftedM2StockModel = m2StockModel.stream()
                            .map(x -> {
                                CandleModel newCandle = new CandleModel();
                                newCandle.setStartTime(x.getStartTime().plusDays(30*shift));
                                newCandle.setCloseValue(x.getCloseValue());
                                newCandle.setOpenValue(x.getOpenValue());
                                return newCandle;
                            })
                            .filter(x -> x.getStartTime().isAfter(portfolioPerformanceDaily.getFirst().getStartTime()))
                            .filter(x -> x.getStartTime().isBefore(portfolioPerformanceDaily.getLast().getStartTime()))
                            .collect(Collectors.toList());

            correlections.add(calculateCorrelation(portfolioPerformanceDaily,shiftedM2StockModel));
        }
        // This method is a placeholder for future implementation
        return correlections;

    }
    private Double calculateCorrelation(List<CandleModel> portfolioPerformance, List<CandleModel> m2StockPerformance) {
        // Tarihleri eşleştir ve aynı tarihlere sahip değerleri al
        Map<LocalDate, BigDecimal> portfolioMap = portfolioPerformance.stream()
                .collect(Collectors.toMap(CandleModel::getStartTime, CandleModel::getCloseValue));
        Map<LocalDate, BigDecimal> m2Map = m2StockPerformance.stream()
                .collect(Collectors.toMap(CandleModel::getStartTime, CandleModel::getCloseValue));
        // Sadece her iki listede de bulunan tarihleri al
        List<LocalDate> commonDates = m2StockPerformance.stream()
                .map(CandleModel::getStartTime)
                .filter(portfolioMap::containsKey)
                .sorted()
                .toList();
        // Eşleşen tarihler için dizileri oluştur
        double[] portfolioValues = new double[commonDates.size()];
        double[] m2Values = new double[commonDates.size()];

        for (int i = 0; i < commonDates.size(); i++) {
            LocalDate date = commonDates.get(i);
            portfolioValues[i] = portfolioMap.get(date).doubleValue();
            m2Values[i] = m2Map.get(date).doubleValue();
        }

        // Apache Commons Math PearsonsCorrelation kullanarak korelasyonu hesapla
        try {
            PearsonsCorrelation correlation = new PearsonsCorrelation();
            return correlation.correlation(portfolioValues, m2Values);
        } catch (Exception e) {
            return Double.NaN;
        }
    }
    private List<Double> calculateYearlyCorrelation(List<CandleModel> p1, Long p2, int startYear, int endYear) {
        List<Double> correlections = new ArrayList<>();
        List<CandleModel> p2Candles = stockRepository.findById(p2)
                .map(Stock::getCandles)
                .map(candleConverter::entityToModelList)
                .orElseThrow(() -> new InvalidParameterException("Stock with ID " + p2 + " not found"));
        for (int i = startYear; i <= endYear; i++) {
            Integer finalI1 = i;
            List<CandleModel> p1Yearly = p1.stream()
                    .filter(candle -> candle.getStartTime().getYear() == finalI1)
                    .sorted(Comparator.comparing(CandleModel::getStartTime))
                    .collect(Collectors.toList());
            List<CandleModel> p2Yearly = p2Candles.stream()
                    .filter(candle -> candle.getStartTime().getYear() == finalI1)
                    .sorted(Comparator.comparing(CandleModel::getStartTime))
                    .collect(Collectors.toList());
            correlections.add(calculateCorrelation(p1Yearly,p2Yearly));
        }
        return correlections;
    }
    private LocalDate parsePeriodKeyToDate(String periodKey, RebalancingFrequency frequency) {
        return switch (frequency) {
            case DAILY -> LocalDate.parse(periodKey); // YYYY-MM-DD
            case WEEKLY -> {
                // periodKey is "YYYY-Www" e.g. "2024-W26"
                String[] parts = periodKey.split("-W");
                int year = Integer.parseInt(parts[0]);
                int week = Integer.parseInt(parts[1]);
                TemporalField woy = IsoFields.WEEK_OF_WEEK_BASED_YEAR;
                TemporalField wby = IsoFields.WEEK_BASED_YEAR;
                yield LocalDate.now()
                        .with(wby, year)
                        .with(woy, week)
                        .with(DayOfWeek.MONDAY);

            }
            case MONTHLY -> {
                YearMonth ym = YearMonth.parse(periodKey); // YYYY-MM
                yield ym.atDay(1); // first day of month
            }
            case QUARTERLY -> {
                // periodKey example: "2024-Q2"
                String[] parts = periodKey.split("-Q");
                int year = Integer.parseInt(parts[0]);
                int quarter = Integer.parseInt(parts[1]);
                int month = (quarter - 1) * 3 + 1;
                yield LocalDate.of(year, month, 1);
            }
            case YEARLY -> LocalDate.of(Integer.parseInt(periodKey), 1, 1);
        };
    }


    private String generatePeriodKey(LocalDate date, RebalancingFrequency frequency) {
        return switch (frequency) {
            case DAILY -> date.toString();
            case WEEKLY -> {
                int week = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                int year = date.get(IsoFields.WEEK_BASED_YEAR);
                yield year + "-W" + week;
            }
            case MONTHLY -> YearMonth.from(date).toString();
            case QUARTERLY -> {
                int quarter = (date.getMonthValue() - 1) / 3 + 1;
                yield date.getYear() + "-Q" + quarter;
            }
            case YEARLY -> String.valueOf(date.getYear());
        };
    }
    private boolean shouldRebalance(LocalDate date, RebalancingFrequency freq, int dayCount) {
        return switch (freq) {
            case DAILY -> true;
            case WEEKLY -> date.getDayOfWeek() == DayOfWeek.MONDAY;
            case MONTHLY -> date.getDayOfMonth() == 1;
            case QUARTERLY -> date.getDayOfMonth() == 1 && List.of(1, 4, 7, 10).contains(date.getMonthValue());
            case YEARLY -> date.getDayOfYear() == 1;
        };
    }


}