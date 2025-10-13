import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-delete',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="delete-confirm" style="display: inline-flex; gap: 8px; align-items: center;">
      <span>Confirm delete {{ item }}</span>
      <button type="button" (click)="onDelete.emit(item)">Confirm</button>
      <button type="button" (click)="onCancel.emit()">Cancel</button>
    </div>
  `
})
export class DeleteComponent {
  @Input() item: any;
  @Output() onDelete = new EventEmitter<any>();
  @Output() onCancel = new EventEmitter<void>();
}
