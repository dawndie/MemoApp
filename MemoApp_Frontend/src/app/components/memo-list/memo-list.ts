import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Memo } from '../../models/memo.model';
import { MemoService } from '../../services/memo.service';

@Component({
  selector: 'app-memo-list',
  imports: [CommonModule, RouterModule],
  templateUrl: './memo-list.html',
  styleUrl: './memo-list.css'
})
export class MemoList implements OnInit {
  memos: Memo[] = [];
  loading = false;
  error: string | null = null;

  constructor(private memoService: MemoService) {}

  ngOnInit(): void {
    this.loadMemos();
  }

  loadMemos(): void {
    this.loading = true;
    this.error = null;
    
    this.memoService.getAllMemos().subscribe({
      next: (memos) => {
        this.memos = memos;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load memos';
        this.loading = false;
        console.error('Error loading memos:', err);
      }
    });
  }

  deleteMemo(id: number): void {
    if (confirm('Are you sure you want to delete this memo?')) {
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
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString();
  }
}
