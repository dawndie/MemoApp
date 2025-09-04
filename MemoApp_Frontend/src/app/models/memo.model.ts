export type Priority = 'LOW' | 'MEDIUM' | 'HIGH';

export interface Memo {
  id?: number;
  title: string;
  content: string;
  priority?: Priority;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateMemoRequest {
  title: string;
  content: string;
  priority?: Priority;
}

export interface UpdateMemoRequest {
  title: string;
  content: string;
  priority?: Priority;
}

export interface PriorityUpdateRequest {
  priority: Priority;
}

export interface BulkPriorityUpdateRequest {
  memoIds: number[];
  priority: Priority;
}

export interface PriorityStats {
  priorityCounts: {
    HIGH: number;
    MEDIUM: number;
    LOW: number;
    NONE?: number;
  };
  totalMemos: number;
  mostCommonPriority: string;
}