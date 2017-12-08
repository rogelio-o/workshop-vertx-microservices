import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
})
export class PaginationComponent {
  @Input() currentPage: number;
  @Input() totalPages: number;
  @Input() path: string;

  getPages() {
    const result: number[] = [];
    for(let index: number = 1; index <= this.totalPages; index++) {
      result.push(index);
    }
    return result;
  }
}
