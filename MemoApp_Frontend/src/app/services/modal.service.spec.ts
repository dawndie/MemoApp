import { TestBed } from '@angular/core/testing';
import { ApplicationRef, Injector } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { ModalService } from './modal.service';
import { ConfirmationModalComponent, ModalConfig } from '../components/confirmation-modal/confirmation-modal';

describe('ModalService', () => {
  let service: ModalService;
  let appRef: ApplicationRef;
  let injector: Injector;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [BrowserAnimationsModule, ConfirmationModalComponent]
    });
    
    service = TestBed.inject(ModalService);
    appRef = TestBed.inject(ApplicationRef);
    injector = TestBed.inject(Injector);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return zero open modals initially', () => {
    expect(service.getOpenModalCount()).toBe(0);
  });

  it('should create and resolve modal with confirmed result', async () => {
    const config: ModalConfig = {
      title: 'Test Modal',
      message: 'Test message',
      type: 'info'
    };

    // Start the modal confirmation
    const confirmPromise = service.confirm(config);
    
    // Check that modal count increased
    expect(service.getOpenModalCount()).toBe(1);

    // Find the modal component in the DOM
    const modalElement = document.querySelector('app-confirmation-modal');
    expect(modalElement).toBeTruthy();

    // Simulate clicking confirm button
    const confirmButton = document.querySelector('.btn-confirm') as HTMLButtonElement;
    if (confirmButton) {
      confirmButton.click();
    }

    // Wait for promise to resolve
    const result = await confirmPromise;
    
    expect(result.confirmed).toBe(true);
    expect(service.getOpenModalCount()).toBe(0);
  });

  it('should create and resolve modal with cancelled result', async () => {
    const config: ModalConfig = {
      title: 'Test Modal',
      message: 'Test message',
      type: 'danger'
    };

    // Start the modal confirmation
    const confirmPromise = service.confirm(config);
    
    // Check that modal count increased
    expect(service.getOpenModalCount()).toBe(1);

    // Simulate clicking cancel button
    const cancelButton = document.querySelector('.btn-cancel') as HTMLButtonElement;
    if (cancelButton) {
      cancelButton.click();
    }

    // Wait for promise to resolve
    const result = await confirmPromise;
    
    expect(result.confirmed).toBe(false);
    expect(service.getOpenModalCount()).toBe(0);
  });

  it('should handle ESC key to cancel modal', async () => {
    const config: ModalConfig = {
      title: 'Test Modal',
      message: 'Test message'
    };

    // Start the modal confirmation
    const confirmPromise = service.confirm(config);
    
    // Simulate ESC key press
    const escEvent = new KeyboardEvent('keydown', { key: 'Escape' });
    document.dispatchEvent(escEvent);

    // Wait for promise to resolve
    const result = await confirmPromise;
    
    expect(result.confirmed).toBe(false);
    expect(service.getOpenModalCount()).toBe(0);
  });

  it('should support multiple modals', () => {
    const config1: ModalConfig = { title: 'Modal 1', message: 'Message 1' };
    const config2: ModalConfig = { title: 'Modal 2', message: 'Message 2' };

    service.confirm(config1);
    service.confirm(config2);

    expect(service.getOpenModalCount()).toBe(2);
  });

  it('should close all modals', () => {
    const config1: ModalConfig = { title: 'Modal 1', message: 'Message 1' };
    const config2: ModalConfig = { title: 'Modal 2', message: 'Message 2' };

    service.confirm(config1);
    service.confirm(config2);
    
    expect(service.getOpenModalCount()).toBe(2);
    
    service.closeAll();
    
    expect(service.getOpenModalCount()).toBe(0);
  });

  it('should add and remove modal-open class from body', () => {
    const config: ModalConfig = { title: 'Test', message: 'Test' };
    
    // Initially no modal-open class
    expect(document.body.classList.contains('modal-open')).toBe(false);
    
    // Open modal
    service.confirm(config);
    expect(document.body.classList.contains('modal-open')).toBe(true);
    
    // Close all modals
    service.closeAll();
    expect(document.body.classList.contains('modal-open')).toBe(false);
  });

  it('should pass data through modal config', async () => {
    const testData = { memoId: 123, memoTitle: 'Test Memo' };
    const config: ModalConfig = {
      title: 'Delete Memo',
      message: 'Are you sure?',
      data: testData
    };

    const confirmPromise = service.confirm(config);
    
    // Simulate confirm
    const confirmButton = document.querySelector('.btn-confirm') as HTMLButtonElement;
    if (confirmButton) {
      confirmButton.click();
    }

    const result = await confirmPromise;
    
    expect(result.data).toEqual(testData);
  });

  afterEach(() => {
    // Clean up any remaining modals
    service.closeAll();
    document.body.classList.remove('modal-open');
    
    // Remove any modal elements from DOM
    const modals = document.querySelectorAll('app-confirmation-modal');
    modals.forEach(modal => modal.remove());
  });
});