import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Portfolio} from "../models/portfolio.model";

@Injectable({
  providedIn: 'root'
})
export class PortfolioService {
  private apiUrl = 'bapi/api/portfolios';

  constructor(private http: HttpClient) {}

  createPortfolio(portfolio: Portfolio): Observable<Portfolio> {
    return this.http.post<Portfolio>(this.apiUrl, portfolio);
  }
}
