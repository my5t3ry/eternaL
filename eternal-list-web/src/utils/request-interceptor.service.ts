import {Injectable} from '@angular/core';
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, finalize} from 'rxjs/operators';
import {Router} from '@angular/router';

@Injectable()
export class RequestInterceptorService implements HttpInterceptor {

  constructor(private router: Router) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req)
      .pipe(
        finalize(() => {
        }), catchError((error: HttpErrorResponse) => {
          let errorMessage = '';
          if (error.error.type == 'MINOR') {
          } else {
            if (error.error instanceof Object) {
              errorMessage = 'Error Code: [' + error.status + '], Message: ' + error.error.message;
              this.router.navigate(['/error/500/' + JSON.stringify(error.error)]);
            } else {
              errorMessage = 'Error: ' + error.error;
              this.router.navigate(['/error/500/' + error.error]);
            }
          }
          return throwError(errorMessage);
        })
      );
  }
}
