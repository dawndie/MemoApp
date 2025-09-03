import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Memo, CreateMemoRequest, UpdateMemoRequest } from '../models/memo.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MemoService {
  private apiUrl = `${environment.apiUrl}/memos`;

  constructor(private http: HttpClient) { }

  getAllMemos(): Observable<Memo[]> {
    return this.http.get<Memo[]>(this.apiUrl);
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
}