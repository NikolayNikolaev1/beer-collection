import { Component, signal } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { AuthService } from '../../../services/auth.service';
import { DialogBase } from '../../core/dialog-base/dialog-base';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-forgot-dialog',
  standalone: false,
  styleUrl: './forgot-dialog.scss',
  templateUrl: './forgot-dialog.html',
})
export class ForgotDialog extends DialogBase {
  readonly form;
  sent = signal(false);
  constructor(
    private readonly fb: FormBuilder,
    private readonly dialog: MatDialogRef<ForgotDialog>,
    private readonly auth: AuthService,
    translate: TranslateService,
  ) {
    super(translate);
    this.form = this.fb.group({ email: ['', [Validators.required, Validators.email]] });
  }
  submit(): void {
    if (this.form.invalid) return;
    this.busy.set(true);
    this.auth.forgotPassword(this.form.value.email!).subscribe({
      next: () => {
        this.busy.set(false);
        this.sent.set(true);
      },
      error: (error) => this.fail(error),
    });
  }
}
