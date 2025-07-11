import {Candle} from "./candle.model";

export interface PortfolioPerformanceResult {
  performance: Candle[];
  correlations: number[];
  yearlyCorrelations: number[];
}
