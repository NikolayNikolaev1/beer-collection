import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, throwError } from 'rxjs';

export interface ApiClientError {
  status: number;
  translationKey: string;
  error?: unknown;
}

interface ApiRequestData<TBody> {
  url: string;
  method: 'get' | 'post' | 'put' | 'delete';
  body?: TBody;
}

@Injectable({ providedIn: 'root' })
export class ApiClient {
  private readonly baseUrl = 'https://localhost:7226/api/v1';

  constructor(private readonly http: HttpClient) {}

  request<TResponse, TRquestBody = undefined>({ url, method, body }: ApiRequestData<TRquestBody>) {
    let requestUrl = `${this.baseUrl}${url}`;

    return this.http
      .request<TResponse>(method, requestUrl, {
        body,
      })
      .pipe(catchError(this.handleError));
  }

  private handleError(error: HttpErrorResponse) {
    if (error.status === 0) {
      // A client-side or network error occurred. Handle it accordingly.
      return throwError(() => ({ status: 0, translationKey: 'errors.network', error: error.error } satisfies ApiClientError));
    }

    if (error.status === 200) {
      return throwError(() => ({ status: error.status, translationKey: 'errors.generic' } satisfies ApiClientError));
    }

    console.error(`Backend returned code ${error.status}, body was: `, error.error);

    // The backend returned an unsuccessful response code.
    // The response body may contain clues as to what went wrong.
    const translationKey = error.status === 401 ? 'errors.unauthorized' : 'errors.generic';
    return throwError(() => ({ status: error.status, translationKey, error: error.error } satisfies ApiClientError));
  }
}
