import { Injectable, ComponentRef, ApplicationRef, Injector, EmbeddedViewRef, createComponent, EnvironmentInjector } from '@angular/core';
import { ConfirmationModalComponent, ModalConfig, ModalResult } from '../components/confirmation-modal/confirmation-modal';

@Injectable({
  providedIn: 'root'
})
export class ModalService {
  private modals: ComponentRef<ConfirmationModalComponent>[] = [];

  constructor(
    private appRef: ApplicationRef,
    private injector: Injector
  ) {}

  /**
   * Opens a confirmation modal and returns a promise with the result
   */
  async confirm(config: ModalConfig): Promise<ModalResult> {
    return new Promise<ModalResult>((resolve) => {
      const modalRef = this.createModal(config);
      
      // Subscribe to modal result
      modalRef.instance.result.subscribe((result: ModalResult) => {
        resolve(result);
        this.destroyModal(modalRef);
      });

      // Subscribe to modal closed event
      modalRef.instance.closed.subscribe(() => {
        this.destroyModal(modalRef);
      });

      // Add to active modals array
      this.modals.push(modalRef);
    });
  }

  /**
   * Closes all open modals
   */
  closeAll(): void {
    this.modals.forEach(modal => {
      this.destroyModal(modal);
    });
    this.modals = [];
  }

  /**
   * Returns the number of currently open modals
   */
  getOpenModalCount(): number {
    return this.modals.length;
  }

  private createModal(config: ModalConfig): ComponentRef<ConfirmationModalComponent> {
    // Create component instance using modern approach
    const modalRef = createComponent(ConfirmationModalComponent, {
      environmentInjector: this.appRef.injector
    });
    
    // Set the config input
    modalRef.instance.config = config;
    
    // Attach to the view
    this.appRef.attachView(modalRef.hostView);
    
    // Append to body
    const domElem = (modalRef.hostView as EmbeddedViewRef<any>).rootNodes[0] as HTMLElement;
    document.body.appendChild(domElem);
    
    // Prevent body scroll
    document.body.classList.add('modal-open');
    
    return modalRef;
  }

  private destroyModal(modalRef: ComponentRef<ConfirmationModalComponent>): void {
    // Remove from modals array
    const index = this.modals.indexOf(modalRef);
    if (index > -1) {
      this.modals.splice(index, 1);
    }

    // Remove body scroll prevention if no more modals
    if (this.modals.length === 0) {
      document.body.classList.remove('modal-open');
    }

    // Detach and destroy
    this.appRef.detachView(modalRef.hostView);
    modalRef.destroy();
  }
}