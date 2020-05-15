import "@angular/compiler";
import {ApplicationRef, DoBootstrap, Injector, NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {KeycloakBearerInterceptor, KeycloakService} from 'keycloak-angular';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {RequestInterceptorService} from '../utils/request-interceptor.service';
import {ListComponent} from './list/list.component';
import {BrowserModule} from '@angular/platform-browser';
import {ACE_CONFIG, AceConfigInterface} from "ngx-ace-wrapper";
import {FormsModule} from "@angular/forms";
import {CodemirrorModule} from "@ctrl/ngx-codemirror";
import {AceEditorModule} from "ngx-ace-editor-wrapper";
import {MenuComponent} from './menu/menu.component';


const keycloakService = new KeycloakService();

export let DiContainer: Injector;


const DEFAULT_ACE_CONFIG: AceConfigInterface = {};

@NgModule({
  declarations: [
    AppComponent,
    ListComponent,
    MenuComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    CodemirrorModule,
    AceEditorModule,
    HttpClientModule,
    AppRoutingModule
  ],
  providers: [{provide: HTTP_INTERCEPTORS, useClass: RequestInterceptorService, multi: true},
    // { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptorService, multi: true },
    {
      provide: KeycloakService,
      useValue: keycloakService
    }, {
      provide: ACE_CONFIG,
      useValue: DEFAULT_ACE_CONFIG
    }, {
      provide: HTTP_INTERCEPTORS,
      useClass: KeycloakBearerInterceptor,
      multi: true
    }],
  entryComponents: [AppComponent]
})
export class AppModule implements DoBootstrap {
  constructor(private injector: Injector) {
    DiContainer = this.injector;
  }

  ngDoBootstrap(appRef: ApplicationRef) {
    keycloakService.init({
      initOptions: {
        onLoad: 'login-required',
        enableLogging: true
      }
    }).then(() => {
      console.log('[ngDoBootstrap] bootstrap app after authentication');
      appRef.bootstrap(AppComponent);
    }).catch(error => console.error('[ngDoBootstrap] init Keycloak failed', error));
  }
}
