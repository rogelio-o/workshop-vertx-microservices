import { NgModule }              from '@angular/core';
import { RouterModule, Routes }  from '@angular/router';

import { SeriesListComponent }   from './series-list/series-list.component';
import { FormComponent as SeriesFormComponent }   from './series-list/form/form.component';
import { PageNotFoundComponent } from './core/page-not-found.component';

const appRoutes: Routes = [
  {
    path: 'series',
    component: SeriesListComponent,
    children: [
      { path: 'create', component: SeriesFormComponent, outlet: 'seriesForm' },
      { path: 'update/:id', component: SeriesFormComponent, outlet: 'seriesForm' }
    ]
  },
  { path: '',   redirectTo: '/series', pathMatch: 'full' },
  { path: '**', component: PageNotFoundComponent }
];

@NgModule({
  imports: [
    RouterModule.forRoot(
      appRoutes
    )
  ],
  exports: [
    RouterModule
  ]
})
export class AppRoutingModule {}
