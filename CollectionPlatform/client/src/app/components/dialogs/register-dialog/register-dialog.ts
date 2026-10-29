import { Component } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { DialogBase } from '../../core/dialog-base/dialog-base';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-register-dialog',
  standalone: false,
  styleUrl: './register-dialog.scss',
  templateUrl: './register-dialog.html',
})
export class RegisterDialog extends DialogBase {
  readonly form;
  constructor(
    private readonly fb: FormBuilder,
    private readonly dialog: MatDialogRef<RegisterDialog>,
    private readonly auth: AuthService,
    translate: TranslateService,
  ) {
    super(translate);
    this.form = this.fb.group({
      tenantName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(3)]],
    });
  }
  submit(): void {
    if (this.form.invalid) return;
    this.busy.set(true);
    this.auth
      .register(this.form.getRawValue() as any)
      .subscribe({ next: () => this.dialog.close(true), error: (error) => this.fail(error) });
  }
}
