import { Component, EventEmitter, Input, Output, OnInit, OnDestroy, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { trigger, state, style, transition, animate } from '@angular/animations';

export interface ModalConfig {
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  type?: 'danger' | 'warning' | 'info';
  data?: any;
}

export interface ModalResult {
  confirmed: boolean;
  data?: any;
}

@Component({
  selector: 'app-confirmation-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './confirmation-modal.html',
  styleUrls: ['./confirmation-modal.css'],
  animations: [
    trigger('modalAnimation', [
      state('in', style({ opacity: 1, transform: 'translateY(0)' })),
      transition('void => *', [
        style({ opacity: 0, transform: 'translateY(-50px)' }),
        animate('200ms ease-in')
      ]),
      transition('* => void', [
        animate('200ms ease-out', style({ opacity: 0, transform: 'translateY(-50px)' }))
      ])
    ]),
    trigger('backdropAnimation', [
      state('in', style({ opacity: 1 })),
      transition('void => *', [
        style({ opacity: 0 }),
        animate('300ms ease-in')
      ]),
      transition('* => void', [
        animate('300ms ease-out', style({ opacity: 0 }))
      ])
    ])
  ]
})
export class ConfirmationModalComponent implements OnInit, OnDestroy {
  @Input() config: ModalConfig = {
    title: 'Confirm Action',
    message: 'Are you sure you want to proceed?',
    confirmText: 'Confirm',
    cancelText: 'Cancel',
    type: 'info'
  };
  
  @Output() result = new EventEmitter<ModalResult>();
  @Output() closed = new EventEmitter<void>();

  showModal = false;

  ngOnInit(): void {
    this.showModal = true;
    
    // Set default values if not provided
    this.config.confirmText = this.config.confirmText || 'Confirm';
    this.config.cancelText = this.config.cancelText || 'Cancel';
    this.config.type = this.config.type || 'info';
    
    // Focus on cancel button after modal opens
    setTimeout(() => {
      const cancelButton = document.querySelector('.btn-cancel') as HTMLButtonElement;
      if (cancelButton) {
        cancelButton.focus();
      }
    }, 250);
  }

  ngOnDestroy(): void {
    document.body.classList.remove('modal-open');
  }

  @HostListener('document:keydown', ['$event'])
  handleKeydown(event: KeyboardEvent): void {
    if (event.key === 'Escape') {
      this.onCancel();
    }
  }

  onBackdropClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('modal-backdrop')) {
      this.onCancel();
    }
  }

  onConfirm(): void {
    this.result.emit({
      confirmed: true,
      data: this.config.data
    });
    this.closeModal();
  }

  onCancel(): void {
    this.result.emit({
      confirmed: false,
      data: this.config.data
    });
    this.closeModal();
  }

  private closeModal(): void {
    this.showModal = false;
    this.closed.emit();
  }

  getModalIcon(): string {
    switch (this.config.type) {
      case 'danger':
        return 'fas fa-exclamation-triangle';
      case 'warning':
        return 'fas fa-exclamation-circle';
      default:
        return 'fas fa-info-circle';
    }
  }

  getModalTypeClass(): string {
    return `modal-${this.config.type}`;
  }
}