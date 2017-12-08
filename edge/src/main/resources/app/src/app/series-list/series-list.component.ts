
import { Component, ViewContainerRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ToastsManager } from 'ng2-toastr/ng2-toastr';
import { Serie } from './../models/Serie';
import { Page } from './../models/Page';
import { SeriesService } from './../services/series.service';
import { RefresherService } from './../services/refresher.service';

@Component({
  templateUrl: './series-list.component.html',
  styleUrls: ['./series-list.component.scss']
})
export class SeriesListComponent {
  loading: boolean;
  series: Page<Serie>;
  page: number;

  private paramsSub: any;

  constructor(private route: ActivatedRoute, private toastr: ToastsManager, vRef: ViewContainerRef, private seriesService: SeriesService, private refresherService: RefresherService) {
    this.toastr.setRootViewContainerRef(vRef);
  }

  ngOnInit() {
    this.paramsSub = this.route.queryParams.subscribe(params => {
      const page: number = params['page'] || 1;
      this.loadPage(page);
    });

    this.refresherService.subscribe('series', () => {
      this.loadPage(this.page);
    });
  }

  ngOnDestroy() {
    this.paramsSub.unsubscribe();
  }

  loadPage(page: number) {
    this.loading = true;
    this.page = page;
    this.seriesService
      .getPage(this.page)
      .then(series => {
        this.series = series;
        this.loading = false;
      }, error => {
        this.loading = false;
        this.toastr.error('Error loading the series.', 'Oops!', {positionClass: 'toast-bottom-right'});
      });
  }

}
