import {Candle} from "./candle.model";

export interface Portfolio {
  id?: number;
  stocks?: { stockId: number; leverage: number ;percentage:number }[];
  rebalancingFrequency: 'MONTHLY' | 'QUARTERLY' | 'YEARLY'| 'DAILY' | 'WEEKLY';
  inflow: number;
  initialInvestment:number;
  startDate: Date;
  endDate:Date;
}
