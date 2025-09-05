import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';

import { ConfirmationModalComponent, ModalConfig } from './confirmation-modal';

describe('ConfirmationModalComponent', () => {
  let component: ConfirmationModalComponent;
  let fixture: ComponentFixture<ConfirmationModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfirmationModalComponent, BrowserAnimationsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(ConfirmationModalComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display default configuration', () => {
    fixture.detectChanges();
    
    const titleElement = fixture.debugElement.query(By.css('.modal-title'));
    const messageElement = fixture.debugElement.query(By.css('.modal-message'));
    
    expect(titleElement.nativeElement.textContent.trim()).toBe('Confirm Action');
    expect(messageElement.nativeElement.textContent.trim()).toBe('Are you sure you want to proceed?');
  });

  it('should display custom configuration', () => {
    const config: ModalConfig = {
      title: 'Delete Memo',
      message: 'Are you sure you want to delete this memo?',
      confirmText: 'Delete',
      cancelText: 'Cancel',
      type: 'danger',
      data: { memoTitle: 'Test Memo' }
    };
    
    component.config = config;
    fixture.detectChanges();
    
    const titleElement = fixture.debugElement.query(By.css('.modal-title'));
    const messageElement = fixture.debugElement.query(By.css('.modal-message'));
    const confirmButton = fixture.debugElement.query(By.css('.btn-confirm'));
    const cancelButton = fixture.debugElement.query(By.css('.btn-cancel'));
    
    expect(titleElement.nativeElement.textContent.trim()).toBe('Delete Memo');
    expect(messageElement.nativeElement.textContent.trim()).toBe('Are you sure you want to delete this memo?');
    expect(confirmButton.nativeElement.textContent.trim()).toBe('Delete');
    expect(cancelButton.nativeElement.textContent.trim()).toBe('Cancel');
  });

  it('should display memo title when provided in data', () => {
    component.config = {
      title: 'Delete Memo',
      message: 'Are you sure you want to delete this memo?',
      data: { memoTitle: 'My Important Memo' }
    };
    
    fixture.detectChanges();
    
    const memoPreview = fixture.debugElement.query(By.css('.memo-preview'));
    expect(memoPreview).toBeTruthy();
    expect(memoPreview.nativeElement.textContent.trim()).toContain('My Important Memo');
  });

  it('should apply correct CSS class for danger type', () => {
    component.config = { title: 'Test', message: 'Test', type: 'danger' };
    fixture.detectChanges();
    
    const modalContainer = fixture.debugElement.query(By.css('.modal-container'));
    expect(modalContainer.nativeElement.classList).toContain('modal-danger');
    
    const confirmButton = fixture.debugElement.query(By.css('.btn-confirm'));
    expect(confirmButton.nativeElement.classList).toContain('btn-danger');
  });

  it('should emit result with confirmed true when confirm button is clicked', () => {
    spyOn(component.result, 'emit');
    spyOn(component.closed, 'emit');
    
    fixture.detectChanges();
    
    const confirmButton = fixture.debugElement.query(By.css('.btn-confirm'));
    confirmButton.nativeElement.click();
    
    expect(component.result.emit).toHaveBeenCalledWith({
      confirmed: true,
      data: component.config.data
    });
    expect(component.closed.emit).toHaveBeenCalled();
  });

  it('should emit result with confirmed false when cancel button is clicked', () => {
    spyOn(component.result, 'emit');
    spyOn(component.closed, 'emit');
    
    fixture.detectChanges();
    
    const cancelButton = fixture.debugElement.query(By.css('.btn-cancel'));
    cancelButton.nativeElement.click();
    
    expect(component.result.emit).toHaveBeenCalledWith({
      confirmed: false,
      data: component.config.data
    });
    expect(component.closed.emit).toHaveBeenCalled();
  });

  it('should close modal when close button is clicked', () => {
    spyOn(component.result, 'emit');
    spyOn(component.closed, 'emit');
    
    fixture.detectChanges();
    
    const closeButton = fixture.debugElement.query(By.css('.modal-close-btn'));
    closeButton.nativeElement.click();
    
    expect(component.result.emit).toHaveBeenCalledWith({
      confirmed: false,
      data: component.config.data
    });
    expect(component.closed.emit).toHaveBeenCalled();
  });

  it('should close modal when backdrop is clicked', () => {
    spyOn(component.result, 'emit');
    spyOn(component.closed, 'emit');
    
    fixture.detectChanges();
    
    const backdrop = fixture.debugElement.query(By.css('.modal-backdrop'));
    backdrop.nativeElement.click();
    
    expect(component.result.emit).toHaveBeenCalledWith({
      confirmed: false,
      data: component.config.data
    });
    expect(component.closed.emit).toHaveBeenCalled();
  });

  it('should handle ESC key press', () => {
    spyOn(component.result, 'emit');
    spyOn(component.closed, 'emit');
    
    fixture.detectChanges();
    
    const event = new KeyboardEvent('keydown', { key: 'Escape' });
    component.handleKeydown(event);
    
    expect(component.result.emit).toHaveBeenCalledWith({
      confirmed: false,
      data: component.config.data
    });
    expect(component.closed.emit).toHaveBeenCalled();
  });

  it('should return correct icon class for different types', () => {
    component.config.type = 'danger';
    expect(component.getModalIcon()).toBe('fas fa-exclamation-triangle');
    
    component.config.type = 'warning';
    expect(component.getModalIcon()).toBe('fas fa-exclamation-circle');
    
    component.config.type = 'info';
    expect(component.getModalIcon()).toBe('fas fa-info-circle');
  });

  it('should return correct modal type class', () => {
    component.config.type = 'danger';
    expect(component.getModalTypeClass()).toBe('modal-danger');
    
    component.config.type = 'warning';
    expect(component.getModalTypeClass()).toBe('modal-warning');
    
    component.config.type = 'info';
    expect(component.getModalTypeClass()).toBe('modal-info');
  });

  it('should set showModal to true on init', () => {
    expect(component.showModal).toBe(false);
    component.ngOnInit();
    expect(component.showModal).toBe(true);
  });

  it('should set default values for config properties', () => {
    component.config = { title: 'Test', message: 'Test' };
    component.ngOnInit();
    
    expect(component.config.confirmText).toBe('Confirm');
    expect(component.config.cancelText).toBe('Cancel');
    expect(component.config.type).toBe('info');
  });
});