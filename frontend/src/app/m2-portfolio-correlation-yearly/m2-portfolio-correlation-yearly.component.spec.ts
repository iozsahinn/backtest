import { ComponentFixture, TestBed } from '@angular/core/testing';

import { M2PortfolioCorrelationYearlyComponent } from './m2-portfolio-correlation-yearly.component';

describe('M2PortfolioCorrelationYearlyComponent', () => {
  let component: M2PortfolioCorrelationYearlyComponent;
  let fixture: ComponentFixture<M2PortfolioCorrelationYearlyComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [M2PortfolioCorrelationYearlyComponent]
    });
    fixture = TestBed.createComponent(M2PortfolioCorrelationYearlyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
