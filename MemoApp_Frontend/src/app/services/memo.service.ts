import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Memo, CreateMemoRequest, UpdateMemoRequest, PriorityUpdateRequest, BulkPriorityUpdateRequest, PriorityStats, Priority } from '../models/memo.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MemoService {
  private apiUrl = `${environment.apiUrl}/memos`;
  private memoUpdateSubject = new Subject<void>();

  constructor(private http: HttpClient) { }

  get memoUpdated$(): Observable<void> {
    return this.memoUpdateSubject.asObservable();
  }

  private notifyMemoUpdate(): void {
    this.memoUpdateSubject.next();
  }

  getAllMemos(priority?: string, sort?: string): Observable<Memo[]> {
    let params = new HttpParams();
    
    if (priority) {
      params = params.set('priority', priority);
    }
    
    if (sort) {
      params = params.set('sort', sort);
    }
    
    return this.http.get<Memo[]>(this.apiUrl, { params });
  }

  getMemoById(id: number): Observable<Memo> {
    return this.http.get<Memo>(`${this.apiUrl}/${id}`);
  }

  createMemo(memo: CreateMemoRequest): Observable<Memo> {
    return this.http.post<Memo>(this.apiUrl, memo).pipe(
      tap(() => this.notifyMemoUpdate())
    );
  }

  updateMemo(id: number, memo: UpdateMemoRequest): Observable<Memo> {
    return this.http.put<Memo>(`${this.apiUrl}/${id}`, memo).pipe(
      tap(() => this.notifyMemoUpdate())
    );
  }

  deleteMemo(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap(() => this.notifyMemoUpdate())
    );
  }

  updateMemoPriority(id: number, priorityRequest: PriorityUpdateRequest): Observable<Memo> {
    return this.http.put<Memo>(`${this.apiUrl}/${id}/priority`, priorityRequest).pipe(
      tap(() => this.notifyMemoUpdate())
    );
  }

  bulkUpdatePriority(bulkRequest: BulkPriorityUpdateRequest): Observable<Memo[]> {
    return this.http.post<Memo[]>(`${this.apiUrl}/bulk/priority`, bulkRequest).pipe(
      tap(() => this.notifyMemoUpdate())
    );
  }

  getPriorityStats(): Observable<PriorityStats> {
    return this.http.get<PriorityStats>(`${this.apiUrl}/stats/priority`);
  }
}