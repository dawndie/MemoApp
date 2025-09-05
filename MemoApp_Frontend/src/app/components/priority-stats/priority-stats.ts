import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { PriorityStats, Priority } from '../../models/memo.model';
import { MemoService } from '../../services/memo.service';

@Component({
  selector: 'app-priority-stats',
  imports: [CommonModule],
  templateUrl: './priority-stats.html',
  styleUrl: './priority-stats.css'
})
export class PriorityStatsComponent implements OnInit, OnDestroy {
  stats: PriorityStats | null = null;
  loading = false;
  error: string | null = null;
  private memoUpdateSubscription?: Subscription;

  priorityConfig = [
    { key: 'high', label: 'High Priority', color: '#dc3545', icon: 'fas fa-exclamation-circle' },
    { key: 'medium', label: 'Medium Priority', color: '#ffc107', icon: 'fas fa-exclamation-triangle' },
    { key: 'low', label: 'Low Priority', color: '#28a745', icon: 'fas fa-check-circle' }
  ];

  constructor(private memoService: MemoService) {}

  ngOnInit(): void {
    this.loadStats();
    
    this.memoUpdateSubscription = this.memoService.memoUpdated$.subscribe(() => {
      this.loadStats();
    });
  }

  ngOnDestroy(): void {
    if (this.memoUpdateSubscription) {
      this.memoUpdateSubscription.unsubscribe();
    }
  }

  loadStats(): void {
    this.loading = true;
    this.error = null;

    this.memoService.getPriorityStats().subscribe({
      next: (stats) => {
        this.stats = stats;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load priority statistics';
        this.loading = false;
        console.error('Error loading priority stats:', err);
      }
    });
  }

  getPercentage(value: number): number {
    if (!this.stats || this.stats.totalMemos === 0) return 0;
    return Math.round((value / this.stats.totalMemos) * 100);
  }

  refresh(): void {
    this.loadStats();
  }

  getStatValue(key: string): number {
    if (!this.stats) return 0;
    const priorityKey = key.toUpperCase() as keyof typeof this.stats.priorityCounts;
    return this.stats.priorityCounts[priorityKey] || 0;
  }
}