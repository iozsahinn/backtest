import { ComponentFixture, TestBed } from '@angular/core/testing';

import { M2PortfolioCorrelationComponent } from './m2-portfolio-correlation.component';

describe('M2PortfolioCorrelationComponent', () => {
  let component: M2PortfolioCorrelationComponent;
  let fixture: ComponentFixture<M2PortfolioCorrelationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [M2PortfolioCorrelationComponent]
    });
    fixture = TestBed.createComponent(M2PortfolioCorrelationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
