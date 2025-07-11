import {ChangeDetectorRef, Component} from '@angular/core';
import { CommonModule } from '@angular/common';
import {Chart, ChartConfiguration} from "chart.js";
import {NgChartsModule} from "ng2-charts";
import {PortfolioPerformanceResult} from "../models/portfolio-performance-result";
import {StockService} from "../services/stock.service";
import {PortfolioService} from "../services/portfolio.service";
import {PerformanceService} from "../services/performance.service";
import {Portfolio} from "../models/portfolio.model";

@Component({
        selector: 'app-m2-portfolio-correlation',
        standalone: true,
        imports: [CommonModule, NgChartsModule],
        templateUrl: './m2-portfolio-correlation.component.html',
        styleUrls: ['./m2-portfolio-correlation.component.scss']
      })
      export class M2PortfolioCorrelationComponent {
        constructor(private cdr: ChangeDetectorRef) {}
        public barChartData: ChartConfiguration<'bar'>['data'] = {
          labels: Array.from({length: 24}, (_, i) => i),
          datasets: [{
            data: Array.from({length: 12}, (_, i) => i),
            label: 'Korelasyonlar',
            backgroundColor: 'red'
          }]
        };
        public barChartDataYearlyM2Correlation: ChartConfiguration<'bar'>['data']={
          labels: Array.from({length: 1}, (_, i) => i),
          datasets: [{
            data: Array.from({length: 1}, (_, i) => i),
            label: 'Yearly Correlations',
            backgroundColor: 'red'
          }]
        }
        public barChartOptions: ChartConfiguration<'bar'>['options'] = {
          responsive: true,
          plugins: {
            title: {
              display: true,
              text: 'Portföy Korelasyonları'
            },
            tooltip: {
              enabled: true
            }
          },
          scales: {
            x: {
              title: {
                display: true,
                text: 'Monthly Shift'
              }
            },
            y: {
              title: {
                display: true,
                text: 'Korelasyon Değeri'
              },
              beginAtZero: false
            }
          }
        };

        public barChartType: ChartConfiguration<'bar'>['type'] = 'bar';

        onCreateM2PortfolioCorrelation(correlations: number[]) {
          this.barChartData = {
            labels: this.barChartData.labels,
            datasets: [{
              data: [...correlations],
              label: 'Korelasyonlar',
              backgroundColor: 'rgba(0, 123, 255, 0.5)'
            }]
          };
          console.log("Korelasyonlar:", correlations);
          this.cdr.detectChanges();
        }
      }
