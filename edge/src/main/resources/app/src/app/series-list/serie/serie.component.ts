import { Component, Input, ViewContainerRef } from '@angular/core';
import { ToastsManager } from 'ng2-toastr/ng2-toastr';
import { Serie } from './../../models/Serie';
import { SeriesService } from './../../services/series.service';
import { RefresherService } from './../../services/refresher.service';

@Component({
  selector: 'app-serie',
  templateUrl: './serie.component.html',
  styleUrls: ['./serie.component.scss']
})
export class SerieComponent {

  @Input() model: Serie;

  loading: boolean;

  constructor(private toastr: ToastsManager, vRef: ViewContainerRef, private seriesService: SeriesService, private refresherService: RefresherService) {
  }

  delete() {
    if(confirm('Do you want to delete this serie? Are you sure?')) {
      this.loading = true;

      this.seriesService.delete(this.model._id).then(result => {
        this.toastr.success('Serie deleted successfully.');
        this.refresherService.emit('series');
      }, error => {
        this.toastr.error('Error deleting the serie.');
      });
    }
  }

}
