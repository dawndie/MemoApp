import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Priority } from '../../models/memo.model';

@Component({
  selector: 'app-priority-selector',
  imports: [CommonModule, FormsModule],
  templateUrl: './priority-selector.html',
  styleUrl: './priority-selector.css'
})
export class PrioritySelector {
  @Input() selectedPriority?: Priority;
  @Input() disabled = false;
  @Input() required = false;
  @Input() placeholder = 'Select Priority';
  @Output() priorityChange = new EventEmitter<Priority | undefined>();

  priorities: { value: Priority; label: string; color: string }[] = [
    { value: 'HIGH', label: 'High', color: '#dc3545' },
    { value: 'MEDIUM', label: 'Medium', color: '#ffc107' },
    { value: 'LOW', label: 'Low', color: '#28a745' }
  ];

  onSelectChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    const value = target.value;
    
    if (value === '') {
      this.priorityChange.emit(undefined);
    } else {
      this.priorityChange.emit(value as Priority);
    }
  }

  getPriorityColor(priority?: Priority): string {
    if (!priority) return '#6c757d';
    const priorityConfig = this.priorities.find(p => p.value === priority);
    return priorityConfig?.color || '#6c757d';
  }

  getPriorityLabel(priority?: Priority): string {
    if (!priority) return 'None';
    const priorityConfig = this.priorities.find(p => p.value === priority);
    return priorityConfig?.label || priority;
  }
}