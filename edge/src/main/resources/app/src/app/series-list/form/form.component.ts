import { Component, ViewContainerRef } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { NgForm } from '@angular/forms';
import { ToastsManager } from 'ng2-toastr/ng2-toastr';
import { Serie } from './../../models/Serie';
import { RefresherService } from './../../services/refresher.service';
import { SeriesService } from './../../services/series.service';

@Component({
  templateUrl: './form.component.html'
})
export class FormComponent {

  id: string;
  modalTitle: string;
  submitLabel: string;
  model: Serie = new Serie();

  private paramsSub: any;

  constructor(private router: Router, private route: ActivatedRoute, private toastr: ToastsManager, vRef: ViewContainerRef, private seriesService: SeriesService, private refresherService: RefresherService) {
    this.toastr.setRootViewContainerRef(vRef);
  }

  ngOnInit() {
    this.paramsSub = this.route.params.subscribe(params => {
      this.id = params['id'];
      if(this.id) {
        this.modalTitle = 'Update Serie';
        this.submitLabel = 'Update';

        this.seriesService
          .get(this.id)
          .then(findModel => {
            this.model = findModel;
          }, error => {
            this.close();
            this.toastr.error('Error loading the serie.', 'Oops!', {positionClass: 'toast-bottom-right'});
          });
      } else {
        this.modalTitle = 'Create Serie';
        this.submitLabel = 'Create';
        this.model = new Serie();
      }
    });
  }

  ngOnDestroy() {
    this.paramsSub.unsubscribe();
  }

  close() {
    this.router.navigate(['series']);
  }

  onSubmit() {
    if(this.id) {
      this.update();
    } else {
      this.create();
    }
  }

  private create() {
    this.seriesService
      .create(this.model)
      .then(result => {
        this.toastr.success('Serie created successfully.', 'Yeahh!', {positionClass: 'toast-bottom-right'});
        this.close();
        this.refresherService.emit('series');
      }, err => {
        this.toastr.error('Error creating the serie.', 'Oops!', {positionClass: 'toast-bottom-right'})
      });
  }

  private update() {
    this.seriesService
      .update(this.id, this.model)
      .then(result => {
        this.toastr.success('Serie updated successfully.', 'Yeahh!', {positionClass: 'toast-bottom-right'});
        this.close();
        this.refresherService.emit('series');
      }, err => this.toastr.error('Error updating the serie.', 'Oops!', {positionClass: 'toast-bottom-right'}));
  }

}
