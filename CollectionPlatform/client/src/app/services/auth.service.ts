import { Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, map, Observable, of, tap } from 'rxjs';
import { ApiClient } from './api-client.service';

export interface Profile {
  id: string;
  tenantName: string;
  email: string;
}
export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  expiresAt: string;
  profile: Profile;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  readonly profile = signal<Profile | null>(null);
  readonly isAuthenticated = signal(Boolean(sessionStorage.getItem('access_token')));

  constructor(
    private readonly router: Router,
    private readonly api: ApiClient,
  ) {}

  register(payload: {
    tenantName: string;
    email: string;
    password: string;
  }): Observable<AuthResponse> {
    return this.api
      .request<
        AuthResponse,
        {
          tenantName: string;
          email: string;
          password: string;
        }
      >({
        method: 'post',
        url: '/auth/register',
        body: payload,
      })
      .pipe(tap((response) => this.accept(response)));
  }
  login(payload: { email: string; password: string }): Observable<AuthResponse> {
    return this.api
      .request<AuthResponse, { email: string; password: string }>({
        method: 'post',
        url: '/auth/login',
        body: payload,
      })
      .pipe(tap((response) => this.accept(response)));
  }

  forgotPassword(email: string): Observable<void> {
    return this.api.request<void, { email: string }>({
      method: 'post',
      url: '/auth/forgot-password',
      body: { email },
    });
  }

  resetPassword(payload: { userId: string; token: string; password: string }): Observable<void> {
    return this.api.request<void, { userId: string; token: string; password: string }>({
      method: 'post',
      url: '/auth/reset-password',
      body: payload,
    });
  }

  getProfile(): Observable<Profile> {
    return this.api
      .request<Profile>({
        method: 'get',
        url: '/profile',
      })
      .pipe(tap((profile) => this.profile.set(profile)));
  }

  updateProfile(payload: { tenantName: string; email: string }): Observable<Profile> {
    return this.api
      .request<Profile, { tenantName: string; email: string }>({
        method: 'put',
        url: '/profile',
        body: payload,
      })
      .pipe(tap((profile) => this.profile.set(profile)));
  }

  logout(): void {
    this.clear();
    void this.router.navigate(['/']);
  }

  refresh(): Observable<boolean> {
    const token = sessionStorage.getItem('refresh_token');

    if (!token) return of(false);

    return this.api
      .request<AuthResponse, { refreshToken: string }>({
        method: 'post',
        url: '/auth/refresh',
        body: { refreshToken: token },
      })
      .pipe(
        tap((response) => this.accept(response)),
        map(() => true),
        catchError(() => {
          this.clear();
          return of(false);
        }),
      );
  }

  private accept(response: AuthResponse): void {
    sessionStorage.setItem('access_token', response.accessToken);
    sessionStorage.setItem('refresh_token', response.refreshToken);
    this.profile.set(response.profile);
    this.isAuthenticated.set(true);
  }
  private clear(): void {
    sessionStorage.clear();
    this.profile.set(null);
    this.isAuthenticated.set(false);
  }
}
