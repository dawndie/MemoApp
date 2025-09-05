import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PriorityStatsComponent } from './priority-stats';
import { MemoService } from '../../services/memo.service';
import { of, throwError } from 'rxjs';

describe('PriorityStatsComponent', () => {
  let component: PriorityStatsComponent;
  let fixture: ComponentFixture<PriorityStatsComponent>;
  let memoServiceSpy: jasmine.SpyObj<MemoService>;

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('MemoService', ['getPriorityStats'], {
      memoUpdated$: of(void 0)
    });

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
    const mockStats = { 
      priorityCounts: { HIGH: 5, MEDIUM: 10, LOW: 8 },
      totalMemos: 23,
      mostCommonPriority: 'MEDIUM'
    };
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
    component.stats = { 
      priorityCounts: { HIGH: 5, MEDIUM: 10, LOW: 5 },
      totalMemos: 20,
      mostCommonPriority: 'MEDIUM'
    };

    expect(component.getPercentage(5)).toBe(25);
    expect(component.getPercentage(10)).toBe(50);
  });

  it('should return 0 percentage when total is 0', () => {
    component.stats = { 
      priorityCounts: { HIGH: 0, MEDIUM: 0, LOW: 0 },
      totalMemos: 0,
      mostCommonPriority: 'NONE'
    };

    expect(component.getPercentage(0)).toBe(0);
  });
});