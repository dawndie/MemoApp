import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MemoList } from './memo-list';

describe('MemoList', () => {
  let component: MemoList;
  let fixture: ComponentFixture<MemoList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MemoList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MemoList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
