import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PriorityStatsComponent } from './priority-stats';
import { MemoService } from '../../services/memo.service';
import { of, throwError } from 'rxjs';

describe('PriorityStatsComponent', () => {
  let component: PriorityStatsComponent;
  let fixture: ComponentFixture<PriorityStatsComponent>;
  let memoServiceSpy: jasmine.SpyObj<MemoService>;

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('MemoService', ['getPriorityStats']);

    await TestBed.configureTestingModule({
      imports: [PriorityStatsComponent],
      providers: [
        { provide: MemoService, useValue: spy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PriorityStatsComponent);
    component = fixture.componentInstance;
    memoServiceSpy = TestBed.inject(MemoService) as jasmine.SpyObj<MemoService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load stats on init', () => {
    const mockStats = { high: 5, medium: 10, low: 8, total: 23 };
    memoServiceSpy.getPriorityStats.and.returnValue(of(mockStats));

    fixture.detectChanges();

    expect(component.stats).toEqual(mockStats);
    expect(component.loading).toBeFalse();
  });

  it('should handle error when loading stats', () => {
    memoServiceSpy.getPriorityStats.and.returnValue(throwError(() => new Error('Test error')));

    fixture.detectChanges();

    expect(component.error).toBe('Failed to load priority statistics');
    expect(component.loading).toBeFalse();
  });

  it('should calculate percentage correctly', () => {
    component.stats = { high: 5, medium: 10, low: 5, total: 20 };

    expect(component.getPercentage(5)).toBe(25);
    expect(component.getPercentage(10)).toBe(50);
  });

  it('should return 0 percentage when total is 0', () => {
    component.stats = { high: 0, medium: 0, low: 0, total: 0 };

    expect(component.getPercentage(0)).toBe(0);
  });
});