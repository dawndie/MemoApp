import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Memo, CreateMemoRequest, UpdateMemoRequest, PriorityUpdateRequest, BulkPriorityUpdateRequest, PriorityStats, Priority } from '../models/memo.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MemoService {
  private apiUrl = `${environment.apiUrl}/memos`;

  constructor(private http: HttpClient) { }

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
    return this.http.post<Memo>(this.apiUrl, memo);
  }

  updateMemo(id: number, memo: UpdateMemoRequest): Observable<Memo> {
    return this.http.put<Memo>(`${this.apiUrl}/${id}`, memo);
  }

  deleteMemo(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  updateMemoPriority(id: number, priorityRequest: PriorityUpdateRequest): Observable<Memo> {
    return this.http.put<Memo>(`${this.apiUrl}/${id}/priority`, priorityRequest);
  }

  bulkUpdatePriority(bulkRequest: BulkPriorityUpdateRequest): Observable<Memo[]> {
    return this.http.post<Memo[]>(`${this.apiUrl}/bulk/priority`, bulkRequest);
  }

  getPriorityStats(): Observable<PriorityStats> {
    return this.http.get<PriorityStats>(`${this.apiUrl}/stats/priority`);
  }
}