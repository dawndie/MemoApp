import { Routes } from '@angular/router';
import { MemoList } from './components/memo-list/memo-list';
import { MemoForm } from './components/memo-form/memo-form';

export const routes: Routes = [
  { path: '', component: MemoList },
  { path: 'memo/new', component: MemoForm },
  { path: 'memo/edit/:id', component: MemoForm },
  { path: '**', redirectTo: '' }
];
