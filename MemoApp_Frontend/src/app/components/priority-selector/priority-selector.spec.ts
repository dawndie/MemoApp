import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PrioritySelector } from './priority-selector';

describe('PrioritySelector', () => {
  let component: PrioritySelector;
  let fixture: ComponentFixture<PrioritySelector>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PrioritySelector]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PrioritySelector);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit priority change event', () => {
    spyOn(component.priorityChange, 'emit');
    
    component.onPriorityChange('HIGH');
    
    expect(component.priorityChange.emit).toHaveBeenCalledWith('HIGH');
  });

  it('should emit undefined when empty string is selected', () => {
    spyOn(component.priorityChange, 'emit');
    
    component.onPriorityChange('');
    
    expect(component.priorityChange.emit).toHaveBeenCalledWith(undefined);
  });

  it('should return correct priority color', () => {
    expect(component.getPriorityColor('HIGH')).toBe('#dc3545');
    expect(component.getPriorityColor('MEDIUM')).toBe('#ffc107');
    expect(component.getPriorityColor('LOW')).toBe('#28a745');
    expect(component.getPriorityColor()).toBe('#6c757d');
  });

  it('should return correct priority label', () => {
    expect(component.getPriorityLabel('HIGH')).toBe('High');
    expect(component.getPriorityLabel('MEDIUM')).toBe('Medium');
    expect(component.getPriorityLabel('LOW')).toBe('Low');
    expect(component.getPriorityLabel()).toBe('None');
  });
});