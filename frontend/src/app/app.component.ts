import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { HomeComponent } from './home/home.component';
import {M2PortfolioCorrelationComponent} from "./m2-portfolio-correlation/m2-portfolio-correlation.component";
import {RouterOutlet} from "@angular/router";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, HttpClientModule, M2PortfolioCorrelationComponent, RouterOutlet],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'optimizer-fe';
}
