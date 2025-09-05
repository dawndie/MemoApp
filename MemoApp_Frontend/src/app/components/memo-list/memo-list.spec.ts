import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { of, throwError } from 'rxjs';

import { MemoList } from './memo-list';
import { MemoService } from '../../services/memo.service';
import { ModalService } from '../../services/modal.service';
import { Memo } from '../../models/memo.model';
import { ModalResult } from '../confirmation-modal/confirmation-modal';

describe('MemoList', () => {
  let component: MemoList;
  let fixture: ComponentFixture<MemoList>;
  let memoService: jasmine.SpyObj<MemoService>;
  let modalService: jasmine.SpyObj<ModalService>;

  const mockMemos: Memo[] = [
    {
      id: 1,
      title: 'Test Memo 1',
      content: 'Content 1',
      createdAt: '2025-01-01T10:00:00Z',
      updatedAt: '2025-01-01T10:00:00Z',
      priority: 'HIGH'
    },
    {
      id: 2,
      title: 'Test Memo 2',
      content: 'Content 2',
      createdAt: '2025-01-01T11:00:00Z',
      updatedAt: '2025-01-01T11:00:00Z',
      priority: 'MEDIUM'
    }
  ];

  beforeEach(async () => {
    const memoServiceSpy = jasmine.createSpyObj('MemoService', [
      'getAllMemos', 'deleteMemo', 'updateMemoPriority', 'bulkUpdatePriority'
    ]);
    const modalServiceSpy = jasmine.createSpyObj('ModalService', ['confirm']);

    await TestBed.configureTestingModule({
      imports: [MemoList, HttpClientTestingModule, BrowserAnimationsModule],
      providers: [
        { provide: MemoService, useValue: memoServiceSpy },
        { provide: ModalService, useValue: modalServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(MemoList);
    component = fixture.componentInstance;
    memoService = TestBed.inject(MemoService) as jasmine.SpyObj<MemoService>;
    modalService = TestBed.inject(ModalService) as jasmine.SpyObj<ModalService>;

    // Setup default service responses
    memoService.getAllMemos.and.returnValue(of(mockMemos));
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load memos on init', () => {
    component.ngOnInit();
    
    expect(memoService.getAllMemos).toHaveBeenCalled();
    expect(component.memos).toEqual(mockMemos);
    expect(component.loading).toBe(false);
  });

  it('should handle loading state', () => {
    expect(component.loading).toBe(false);
    
    component.loadMemos();
    expect(component.loading).toBe(true);
    
    fixture.detectChanges();
    expect(component.loading).toBe(false);
  });

  it('should handle error when loading memos fails', () => {
    memoService.getAllMemos.and.returnValue(throwError('Load error'));
    
    component.loadMemos();
    
    expect(component.error).toBe('Failed to load memos');
    expect(component.loading).toBe(false);
  });

  it('should delete memo when confirmed', async () => {
    component.memos = [...mockMemos];
    
    const confirmResult: ModalResult = { confirmed: true, data: { memoId: 1, memoTitle: 'Test Memo 1' } };
    modalService.confirm.and.returnValue(Promise.resolve(confirmResult));
    memoService.deleteMemo.and.returnValue(of(void 0));

    await component.deleteMemo(1);

    expect(modalService.confirm).toHaveBeenCalledWith({
      title: 'Delete Memo',
      message: 'Are you sure you want to delete this memo? This action cannot be undone.',
      confirmText: 'Delete Memo',
      cancelText: 'Cancel',
      type: 'danger',
      data: { memoId: 1, memoTitle: 'Test Memo 1' }
    });
    expect(memoService.deleteMemo).toHaveBeenCalledWith(1);
    expect(component.memos).toEqual([mockMemos[1]]);
  });

  it('should not delete memo when cancelled', async () => {
    component.memos = [...mockMemos];
    
    const confirmResult: ModalResult = { confirmed: false, data: { memoId: 1, memoTitle: 'Test Memo 1' } };
    modalService.confirm.and.returnValue(Promise.resolve(confirmResult));

    await component.deleteMemo(1);

    expect(modalService.confirm).toHaveBeenCalled();
    expect(memoService.deleteMemo).not.toHaveBeenCalled();
    expect(component.memos).toEqual(mockMemos);
  });

  it('should handle error when delete fails', async () => {
    component.memos = [...mockMemos];
    
    const confirmResult: ModalResult = { confirmed: true, data: { memoId: 1, memoTitle: 'Test Memo 1' } };
    modalService.confirm.and.returnValue(Promise.resolve(confirmResult));
    memoService.deleteMemo.and.returnValue(throwError('Delete error'));

    await component.deleteMemo(1);

    expect(component.error).toBe('Failed to delete memo');
  });

  it('should handle missing memo gracefully', async () => {
    component.memos = [];
    
    await component.deleteMemo(999);
    
    expect(modalService.confirm).not.toHaveBeenCalled();
  });

  it('should format dates correctly', () => {
    const dateString = '2025-01-01T10:00:00Z';
    const formattedDate = component.formatDate(dateString);
    
    expect(formattedDate).toBe(new Date(dateString).toLocaleDateString());
  });

  it('should toggle memo selection', () => {
    const event = { target: { checked: true } } as any;
    
    component.toggleMemoSelection(1, event);
    expect(component.selectedMemos.has(1)).toBe(true);
    
    event.target.checked = false;
    component.toggleMemoSelection(1, event);
    expect(component.selectedMemos.has(1)).toBe(false);
  });

  it('should clear all selections', () => {
    component.selectedMemos.add(1);
    component.selectedMemos.add(2);
    
    component.clearSelection();
    
    expect(component.selectedMemos.size).toBe(0);
  });

  it('should update memo priority', () => {
    component.memos = [...mockMemos];
    const updatedMemo: Memo = { ...mockMemos[0], priority: 'LOW' };
    memoService.updateMemoPriority.and.returnValue(of(updatedMemo));

    component.updateMemoPriority(1, 'LOW');

    expect(memoService.updateMemoPriority).toHaveBeenCalledWith(1, { priority: 'LOW' });
    expect(component.memos[0].priority).toBe('LOW');
  });

  it('should handle bulk priority update', () => {
    component.memos = [...mockMemos];
    component.selectedMemos.add(1);
    component.selectedMemos.add(2);
    
    const updatedMemos: Memo[] = mockMemos.map(memo => ({ ...memo, priority: 'HIGH' as const }));
    memoService.bulkUpdatePriority.and.returnValue(of(updatedMemos));

    component.onBulkPriorityChange('HIGH');

    expect(memoService.bulkUpdatePriority).toHaveBeenCalledWith({
      memoIds: [1, 2],
      priority: 'HIGH'
    });
    expect(component.selectedMemos.size).toBe(0);
  });

  it('should get priority color', () => {
    expect(component.getPriorityColor('HIGH')).toBe('#dc3545');
    expect(component.getPriorityColor('MEDIUM')).toBe('#ffc107');
    expect(component.getPriorityColor('LOW')).toBe('#28a745');
    expect(component.getPriorityColor()).toBe('#6c757d');
  });

  it('should get priority label', () => {
    expect(component.getPriorityLabel('HIGH')).toBe('High');
    expect(component.getPriorityLabel('MEDIUM')).toBe('Medium');
    expect(component.getPriorityLabel('LOW')).toBe('Low');
    expect(component.getPriorityLabel()).toBe('None');
  });
});