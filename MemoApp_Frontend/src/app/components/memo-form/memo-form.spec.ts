import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MemoForm } from './memo-form';

describe('MemoForm', () => {
  let component: MemoForm;
  let fixture: ComponentFixture<MemoForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MemoForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MemoForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
