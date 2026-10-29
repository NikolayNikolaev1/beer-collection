import { Component, Inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AuthService } from '../../../services/auth.service';
import { DialogBase } from '../../core/dialog-base/dialog-base';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-reset-dialog',
  standalone: false,
  styleUrl: './reset-dialog.scss',
  templateUrl: './reset-dialog.html',
})
export class ResetDialog extends DialogBase {
  readonly form;
  constructor(
    private readonly fb: FormBuilder,
    private readonly dialog: MatDialogRef<ResetDialog>,
    private readonly auth: AuthService,
    @Inject(MAT_DIALOG_DATA) private readonly data: { userId: string; token: string },
    translate: TranslateService,
  ) {
    super(translate);
    this.form = this.fb.group({ password: ['', [Validators.required, Validators.minLength(8)]] });
  }
  submit(): void {
    if (this.form.invalid) return;
    this.busy.set(true);
    this.auth
      .resetPassword({ ...this.data, password: this.form.value.password! })
      .subscribe({ next: () => this.dialog.close(true), error: (error) => this.fail(error) });
  }
}
