import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { HttpModule } from '@angular/http';
import { FormsModule }   from '@angular/forms';
import { ToastModule, ToastOptions } from 'ng2-toastr/ng2-toastr';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';

import { PaginationComponent }   from './shared/pagination.component';
import { HeaderComponent }   from './header/header.component';
import { SeriesListComponent }   from './series-list/series-list.component';
import { FormComponent as SeriesFormComponent }   from './series-list/form/form.component';
import { SerieComponent }   from './series-list/serie/serie.component';
import { PageNotFoundComponent } from './core/page-not-found.component';

import { CustomToastOptions } from './core/configuration';
import { SeriesService } from './services/series.service';
import { RefresherService } from './services/refresher.service';


@NgModule({
  declarations: [
    AppComponent,
    PaginationComponent,
    HeaderComponent,
    SeriesListComponent,
    SeriesFormComponent,
    SerieComponent,
    PageNotFoundComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    HttpModule,
    ToastModule.forRoot(),
    FormsModule
  ],
  providers: [
    {provide: ToastOptions, useClass: CustomToastOptions},
    SeriesService,
    RefresherService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
