import { Component, signal } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-dialog-base',
  standalone: false,
  styleUrl: './dialog-base.scss',
  templateUrl: './dialog-base.html',
})
export class DialogBase {
  readonly busy = signal(false);
  error = '';

  constructor(private readonly translate: TranslateService) {}

  protected fail(error: any): void {
    this.busy.set(false);
    this.translate
      .get(error?.translationKey ?? error?.error?.translationKey ?? 'errors.generic')
      .subscribe((message) => (this.error = message));
  }
}
