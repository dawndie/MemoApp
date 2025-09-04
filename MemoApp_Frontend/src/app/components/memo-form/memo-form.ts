import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MemoService } from '../../services/memo.service';
import { Memo, Priority } from '../../models/memo.model';
import { PrioritySelector } from '../priority-selector/priority-selector';

@Component({
  selector: 'app-memo-form',
  imports: [CommonModule, ReactiveFormsModule, PrioritySelector],
  templateUrl: './memo-form.html',
  styleUrl: './memo-form.css'
})
export class MemoForm implements OnInit {
  memoForm: FormGroup;
  isEditMode = false;
  memoId: number | null = null;
  loading = false;
  error: string | null = null;
  selectedPriority?: Priority;

  constructor(
    private fb: FormBuilder,
    private memoService: MemoService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.memoForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(1)]],
      content: ['']
    });
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.memoId = +params['id'];
        this.loadMemo();
      }
    });
  }

  loadMemo(): void {
    if (this.memoId) {
      this.loading = true;
      this.memoService.getMemoById(this.memoId).subscribe({
        next: (memo) => {
          this.memoForm.patchValue({
            title: memo.title,
            content: memo.content
          });
          this.selectedPriority = memo.priority;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Failed to load memo';
          this.loading = false;
          console.error('Error loading memo:', err);
        }
      });
    }
  }

  onSubmit(): void {
    if (this.memoForm.valid) {
      this.loading = true;
      this.error = null;

      const memoData = {
        ...this.memoForm.value,
        priority: this.selectedPriority
      };

      if (this.isEditMode && this.memoId) {
        this.memoService.updateMemo(this.memoId, memoData).subscribe({
          next: () => {
            this.router.navigate(['/']);
          },
          error: (err) => {
            this.error = 'Failed to update memo';
            this.loading = false;
            console.error('Error updating memo:', err);
          }
        });
      } else {
        this.memoService.createMemo(memoData).subscribe({
          next: () => {
            this.router.navigate(['/']);
          },
          error: (err) => {
            this.error = 'Failed to create memo';
            this.loading = false;
            console.error('Error creating memo:', err);
          }
        });
      }
    }
  }

  onCancel(): void {
    this.router.navigate(['/']);
  }

  onPriorityChange(priority: Priority | undefined): void {
    this.selectedPriority = priority;
  }
}
