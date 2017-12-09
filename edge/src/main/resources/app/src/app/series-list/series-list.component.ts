import { Component, ViewContainerRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ToastsManager } from 'ng2-toastr/ng2-toastr';
import * as EventBus from 'vertx3-eventbus-client';
import { Serie } from './../models/Serie';
import { Page } from './../models/Page';
import { SeriesService } from './../services/series.service';
import { RefresherService } from './../services/refresher.service';
import { environment } from './../../environments/environment';

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

    var eb = new EventBus(environment.socketUrl);
    eb.onopen = () => {
      eb.registerHandler('new.comment', {}, (error, message) => {
        const body = message.body;
        this.updateComments(body.id_serie, body.total);
      });

      eb.registerHandler('remove.comment', {}, (error, message) => {
        const body = message.body;
        this.updateComments(body.id_serie, body.total);
      });

      eb.registerHandler('new.rating', {}, (error, message) => {
        const body = message.body;
        this.updateRatings(body.id_serie, body.total, body.average);
      });
    };
  }

  private updateComments(idSerie: string, numComments: number): void {
    const serie: Serie = this.findSerie(idSerie);
    if(serie) {
      serie.num_comments = numComments;
    }
  }

  private updateRatings(idSerie: string, numRatings: number, averageRating: number): void {
    const serie: Serie = this.findSerie(idSerie);
    if(serie) {
      serie.num_ratings = numRatings;
      serie.average_rating = averageRating;
    }
  }

  private findSerie(idSerie: string): Serie {
    let result;

    for(let i = 0; i < this.series.results.length; i++) {
      let serie: Serie = this.series.results[i];
      if(serie._id == idSerie) {
        result = serie;
        break;
      }
    }
    return result;
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
