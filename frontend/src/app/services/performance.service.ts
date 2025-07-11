import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Portfolio} from "../models/portfolio.model";
import {Candle} from "../models/candle.model";
import {PortfolioPerformanceResult} from "../models/portfolio-performance-result";

@Injectable({
  providedIn: 'root'
})
export class PerformanceService {
  private apiUrl = 'bapi/api/portfolios';


  constructor(private http: HttpClient) {}

  calclatePerformance(portId: number | undefined): Observable<PortfolioPerformanceResult> {
    return this.http.get<PortfolioPerformanceResult>(this.apiUrl +"/"+portId +'/performance');
  }
}
