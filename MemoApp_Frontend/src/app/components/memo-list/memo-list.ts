import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Memo, Priority } from '../../models/memo.model';
import { MemoService } from '../../services/memo.service';
import { ModalService } from '../../services/modal.service';
import { PrioritySelector } from '../priority-selector/priority-selector';
import { PriorityStatsComponent } from '../priority-stats/priority-stats';
@Component({
  selector: 'app-memo-list',
  imports: [
    CommonModule, RouterModule, FormsModule, PrioritySelector, PriorityStatsComponent
  ],
  templateUrl: './memo-list.html',
  styleUrl: './memo-list.css'
})
export class MemoList implements OnInit {
  memos: Memo[] = [];
  loading = false;
  error: string | null = null;
  selectedPriorityFilter = '';
  selectedSort = '';
  selectedMemos = new Set<number>();

  priorities: { value: Priority; label: string; color: string }[] = [
    { value: 'HIGH', label: 'High', color: '#dc3545' },
    { value: 'MEDIUM', label: 'Medium', color: '#ffc107' },
    { value: 'LOW', label: 'Low', color: '#28a745' }
  ];

  constructor(
    private memoService: MemoService,
    private modalService: ModalService
  ) {}

  ngOnInit(): void {
    this.loadMemos();
  }

  loadMemos(): void {
    this.loading = true;
    this.error = null;
    
    this.memoService.getAllMemos(this.selectedPriorityFilter, this.selectedSort).subscribe({
      next: (memos) => {
        this.memos = memos;
        this.loading = false;
        this.selectedMemos.clear();
      },
      error: (err) => {
        this.error = 'Failed to load memos';
        this.loading = false;
        console.error('Error loading memos:', err);
      }
    });
  }

  async deleteMemo(id: number): Promise<void> {
    const memo = this.memos.find(m => m.id === id);
    if (!memo) return;

    try {
      const result = await this.modalService.confirm({
        title: 'Delete Memo',
        message: 'Are you sure you want to delete this memo? This action cannot be undone.',
        confirmText: 'Delete Memo',
        cancelText: 'Cancel',
        type: 'danger',
        data: { memoId: id, memoTitle: memo.title }
      });

      if (result.confirmed) {
        this.memoService.deleteMemo(id).subscribe({
          next: () => {
            this.memos = this.memos.filter(memo => memo.id !== id);
          },
          error: (err) => {
            this.error = 'Failed to delete memo';
            console.error('Error deleting memo:', err);
          }
        });
      }
    } catch (error) {
      console.error('Error showing confirmation modal:', error);
    }
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString();
  }

  onFilterChange(): void {
    this.loadMemos();
  }

  onSortChange(): void {
    this.loadMemos();
  }

  toggleMemoSelection(id: number, event: Event): void {
    const target = event.target as HTMLInputElement;
    if (target.checked) {
      this.selectedMemos.add(id);
    } else {
      this.selectedMemos.delete(id);
    }
  }

  clearSelection(): void {
    this.selectedMemos.clear();
  }

  updateMemoPriority(id: number, priority: Priority | undefined): void {
    if (!priority) return;

    this.memoService.updateMemoPriority(id, { priority }).subscribe({
      next: (updatedMemo) => {
        const index = this.memos.findIndex(memo => memo.id === id);
        if (index !== -1) {
          this.memos[index] = updatedMemo;
        }
      },
      error: (err) => {
        this.error = 'Failed to update memo priority';
        console.error('Error updating memo priority:', err);
      }
    });
  }

  onBulkPriorityChange(priority: Priority | undefined): void {
    if (!priority || this.selectedMemos.size === 0) return;

    const memoIds = Array.from(this.selectedMemos);
    this.memoService.bulkUpdatePriority({ memoIds, priority }).subscribe({
      next: (updatedMemos) => {
        updatedMemos.forEach(updatedMemo => {
          const index = this.memos.findIndex(memo => memo.id === updatedMemo.id);
          if (index !== -1) {
            this.memos[index] = updatedMemo;
          }
        });
        this.selectedMemos.clear();
      },
      error: (err) => {
        this.error = 'Failed to update memo priorities';
        console.error('Error updating memo priorities:', err);
      }
    });
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
