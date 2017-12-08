import { Injectable } from '@angular/core';

@Injectable()
export class RefresherService {

  private subscriptions: {[name: string]: () => void} = {};

  subscribe(key: string, func: () => void): void {
    this.subscriptions[key] = func;
  }

  emit(key: string) {
    const func: () => void = this.subscriptions[key];
    if(func) {
      func();
    }
  }

}
