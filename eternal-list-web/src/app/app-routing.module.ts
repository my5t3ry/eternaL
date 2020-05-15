import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {AppAuthGuard} from '../utils/app.authguard';
import {ListComponent} from './list/list.component';


const routes: Routes = [{
  path: '',
  component: ListComponent,
  canActivate: [AppAuthGuard]
}];

@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [RouterModule],
  providers: [AppAuthGuard]
})
export class AppRoutingModule {
}
