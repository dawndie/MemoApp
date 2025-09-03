export interface Memo {
  id?: number;
  title: string;
  content: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateMemoRequest {
  title: string;
  content: string;
}

export interface UpdateMemoRequest {
  title: string;
  content: string;
}