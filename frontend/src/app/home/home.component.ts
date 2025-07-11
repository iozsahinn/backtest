import {ChangeDetectorRef, Component, OnInit, ViewChild} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HttpClientModule} from "@angular/common/http";
import {Stock} from "../models/stock.model";
import {StockService} from "../services/stock.service";
import {Portfolio} from "../models/portfolio.model";
import {PortfolioService} from "../services/portfolio.service";
import {FormsModule} from "@angular/forms";
import {PerformanceService} from "../services/performance.service";
import {
  Chart,
  ChartData,
  ChartOptions,
  Legend,
  LinearScale,
  LineController,
  LineElement,
  TimeScale,
  Title,
  Tooltip
} from "chart.js";
import {NgChartsModule} from "ng2-charts";
import {M2PortfolioCorrelationComponent} from "../m2-portfolio-correlation/m2-portfolio-correlation.component";
import {
  M2PortfolioCorrelationYearlyComponent
} from "../m2-portfolio-correlation-yearly/m2-portfolio-correlation-yearly.component";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FormsModule, NgChartsModule, M2PortfolioCorrelationComponent, M2PortfolioCorrelationYearlyComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit{
  stocks: Stock[] = [];
  port_stocks: { stockId: number; leverage: number; percentage: number; }[] = [
    {stockId: 0, leverage: 1.0, percentage: 0}
  ];
  rebalanceFrequency: 'MONTHLY' | 'QUARTERLY' | 'YEARLY' | 'DAILY' | 'WEEKLY' = 'YEARLY'; // Varsayılan rebalans sıklığı
  inflow: number = 0; // Varsayılan para girişi
  initialInvestment: number = 1; // Varsayılan başlangıç yatırımı
  startDate: string = '01.01.2000'; // Varsayılan başlangıç tarihi
  endDate: string = '01.01.2025'; // Varsayılan bitiş tarihi
  constructor(private stockService: StockService,
              private portfolioService: PortfolioService,
              private performanceService: PerformanceService,
              private cdr: ChangeDetectorRef) {
  }
  @ViewChild(M2PortfolioCorrelationComponent, { static: false }) m2PortfolioCorrelationComponent!: M2PortfolioCorrelationComponent;
  @ViewChild(M2PortfolioCorrelationYearlyComponent, { static: false }) m2PortfolioCorrelationYearlyComponent!: M2PortfolioCorrelationYearlyComponent;
  options: { stockId: number; leverage: number; percentage: number }[] = [];
  performance: any[] = [];
  correlation: number[] = [];
  yearlyCorrelation: number[] = [];

  addStock() {
    this.options.push({
      stockId: 0,
      leverage: 1,
      percentage: 0
    });
  }

  ngOnInit() {
    Chart.register(
      LineController,
      LineElement, // <-- Ensure this is registered
      LinearScale,
      TimeScale,
      Title,
      Tooltip,
      Legend
    );
    this.stockService.getStocks().subscribe(data => this.stocks = data);
  }



  onPercentageChange(index: number) {
    const totalPercentage = this.port_stocks.reduce((sum, stock) => sum + stock.percentage, 0);
    if (totalPercentage > 100) {
      alert('Percentage cannot exceed 100%');
      this.port_stocks[index].percentage = 100 - (totalPercentage - this.port_stocks[index].percentage);
    } else if (totalPercentage < 0) {
      this.port_stocks[index].percentage = 0;
    }
  }

  removeStock(i: number) {
    this.options.splice(i, 1);
  }

  trackByFn(index: number, item: any): number {
    return index;
  }

  trackByStockId(index: number, stock: any): any {
    return stock.id;
  }
  isGraphVisible : boolean = false;
  onCreatePortfolio() {
    let newPortfolio: Portfolio = {
      stocks: this.options,
      rebalancingFrequency: this.rebalanceFrequency,
      inflow: this.inflow,
      initialInvestment: this.initialInvestment,
      startDate: new Date(this.startDate.split('.').reverse().join('-')),
      endDate: new Date(this.endDate.split('.').reverse().join('-')),
    };
    let portId:number | undefined=0;
    this.portfolioService.createPortfolio(newPortfolio).subscribe({
      next: (response) => {
        console.log('Portföy başarıyla oluşturuldu:', response);
        console.log('Gönderilen JSON:', JSON.stringify(newPortfolio, null, 2));
        portId = response.id;
        this.performanceService.calclatePerformance(portId).subscribe({
          next: (response) => {
            console.log("Performance successfully calculated.", response);
            this.performance = response.performance;
            console.log('this.performance içeriği:', this.performance);
            this.correlation=response.correlations;
            console.log('this.correlation içeriği:', this.correlation);
            this.yearlyCorrelation= response.yearlyCorrelations;
            this.updateChart();
          },
          error: (err) => {
            console.error('Error when calculating the performance.', err);
            console.log(err);
            console.error('Error when calculating the performance.');
          }
        })
      },
      error: (err) => {
        console.error('Portföy oluşturulurken hata:', err);
        console.log('Gönderilen JSON:', JSON.stringify(newPortfolio, null, 2));
        console.error('Portföy oluşturulamadı!');
      }
    });
  }

  public lineChartType: 'line' = 'line';
  public lineChartLabels: string[] = [];
  public lineChartData: ChartData= {
    labels: [],
    datasets: []
  };
  public lineChartOptions: ChartOptions = {
    responsive: true,
    maintainAspectRatio: false, // You already have this, good for flexible sizing
    // Plugins for general chart features like legend, tooltips, and title
    plugins: {
      title: {
        display: true,
        text: 'Portfolio Value Over Time',
      },
    },

    // Axis configurations
    scales: {
      x: {
        type: 'timeseries', // Essential for date-based data
        title: {
          display: false,
          text: 'Date', // X-axis title
        },
      },
      'y-axis-price': { // Your custom y-axis ID
        position: 'left', // Position on the left
        title: {
          display: true,
          text: 'Portfolio Value (Dolar)', // Y-axis title
        },
        ticks: {
          callback: function(value) {
            return `${value} Dolar`; // Add currency symbol to Y-axis labels
          }
        },
      }
    }
  };
  updateChart(): void {
    const labels: string[] = [];
    const values: number[] = [];

    for (const candle of this.performance) {
      const date = new Date(candle.startTime);
      if (!isNaN(date.getTime())) {
        labels.push(date.toISOString().split('T')[0]);
        values.push(candle.closeValue);
      }
    }


    this.lineChartData = {
      labels: labels,
      datasets: [
        {
          data: values,
          label: 'Close Price',
        }
      ],
    };
    this.lineChartType= 'line';
    if (this.m2PortfolioCorrelationComponent) {
        setTimeout(() => {
                this.m2PortfolioCorrelationComponent.onCreateM2PortfolioCorrelation(this.correlation);
                this.cdr.markForCheck();
                this.cdr.detectChanges();
        });
    }

    if (this.m2PortfolioCorrelationYearlyComponent) {
      setTimeout(() => {
        this.m2PortfolioCorrelationYearlyComponent.onCreateM2YearlyCorrelation(this.yearlyCorrelation);
        this.cdr.markForCheck();
        this.cdr.detectChanges();
      });
    }
  }
  formatDate(event: any) {
    let input = event.target;
    let value = input.value.replace(/\D/g, '');
    if (value.length > 0) {
      if (value.length > 4) {
        value = value.slice(0,2) + '.' + value.slice(2,4) + '.' + value.slice(4);
      } else if (value.length > 2) {
        value = value.slice(0,2) + '.' + value.slice(2);
      }
    }
    input.value = value;
  }
}
