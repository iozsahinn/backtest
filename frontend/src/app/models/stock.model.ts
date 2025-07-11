import {Candle} from "./candle.model";

export interface Stock {
  id?: number;
  name: string;
  symbol: string;
  candles?: Candle[];
}
