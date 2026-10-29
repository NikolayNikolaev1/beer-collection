import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { AuthService } from '../../../services/auth.service';
import { DialogBase } from '../../core/dialog-base/dialog-base';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-login-dialog',
  standalone: false,
  styleUrl: './login-dialog.scss',
  templateUrl: './login-dialog.html',
})
export class LoginDialog extends DialogBase {
  readonly form;

  constructor(
    private readonly fb: FormBuilder,
    private readonly dialog: MatDialogRef<LoginDialog>,
    private readonly authService: AuthService,
    translate: TranslateService,
  ) {
    super(translate);
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
    });
  }

  closeForgot(): void {
    this.dialog.close('forgot');
  }

  submit(): void {
    if (this.form.invalid) return;
    this.busy.set(true);

    this.authService
      .login(this.form.getRawValue() as any)
      .subscribe({ next: () => this.dialog.close(true), error: (error) => this.fail(error) });
  }
}
