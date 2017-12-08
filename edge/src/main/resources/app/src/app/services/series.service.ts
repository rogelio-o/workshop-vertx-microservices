import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Serie } from '../models/Serie';
import { Page } from '../models/Page';
import { environment } from '../../environments/environment';

@Injectable()
export class SeriesService {

  private http: Http;

  constructor(http: Http) {
    this.http = http;
  }

  create(model: Serie): Promise<Serie> {
    return new Promise<Serie>((resolve, reject) => {
      this.http
        .post(this.getUrl(''), model)
        .toPromise()
        .then((response: Response) => {
          if(response.ok) {
            resolve(response.json() as any as Serie);
          } else {
            reject();
          }
        })
        .catch(err => reject(err));
    });
  }

  update(id: string, model: Serie): Promise<Serie> {
    return new Promise<Serie>((resolve, reject) => {
      this.http
        .put(this.getUrl('/' + id), model)
        .toPromise()
        .then((response: Response) => {
          if(response.ok) {
            resolve(response.json() as any as Serie);
          } else {
            reject();
          }
        })
        .catch(err => reject(err));
    });
  }

  delete(id: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this.http
        .delete(this.getUrl('/' + id))
        .toPromise()
        .then((response: Response) => {
          if(response.ok) {
            resolve();
          } else {
            reject();
          }
        })
        .catch(err => reject(err));
    });
  }

  get(id: string): Promise<Serie> {
    return new Promise<Serie>((resolve, reject) => {
      this.http
        .get(this.getUrl('/' + id))
        .toPromise()
        .then((response: Response) => {
          if(response.ok) {
            resolve(response.json() as any as Serie);
          } else {
            reject();
          }
        })
        .catch(err => reject(err));
    });
  }

  getPage(page: number): Promise<Page<Serie>> {
    return new Promise<Page<Serie>>((resolve, reject) => {
      this.http
        .get(this.getUrl('?page=' + page))
        .toPromise()
        .then((response: Response) => {
          if(response.ok) {
            resolve(response.json() as any as Page<Serie>);
          } else {
            reject();
          }
        })
        .catch(err => reject(err));
    });
  }

  private getUrl(path: string) {
    return environment.apiUrl + '/series' + path;
  }

}
