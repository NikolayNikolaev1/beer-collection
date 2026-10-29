import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-profile',
  standalone: false,
  templateUrl: './profile.html',
  styleUrl: './profile.scss',
})
export class ProfileComponent implements OnInit {
  readonly form;
  message = '';
  error = '';
  busy = false;
  constructor(
    private readonly fb: FormBuilder,
    private readonly auth: AuthService,
    private readonly translate: TranslateService,
  ) {
    this.form = this.fb.group({
      tenantName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
    });
  }
  ngOnInit(): void {
    this.auth.getProfile().subscribe({
      next: (profile) => this.form.patchValue(profile),
      error: () => this.translate.get('errors.unauthorized').subscribe((message) => (this.error = message)),
    });
  }
  save(): void {
    if (this.form.invalid) return;
    this.busy = true;
    this.auth
      .updateProfile(this.form.getRawValue() as { tenantName: string; email: string })
      .subscribe({
        next: () => {
          this.busy = false;
          this.translate.get('profile.updated').subscribe((message) => (this.message = message));
        },
        error: (error) => {
          this.busy = false;
          this.translate.get(error?.translationKey ?? 'errors.profileUpdate').subscribe((message) => (this.error = message));
        },
      });
  }
}
