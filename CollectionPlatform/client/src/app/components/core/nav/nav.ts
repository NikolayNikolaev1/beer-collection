import { Component } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { LoginDialog } from '../../dialogs/login-dialog/login-dialog';
import { ForgotDialog } from '../../dialogs/forgot-dialog/forgot-dialog';
import { Language, LanguageService } from '../../../services/language.service';

@Component({
  selector: 'app-nav',
  standalone: false,
  styleUrl: './nav.scss',
  templateUrl: './nav.html',
})
export class Nav {
  constructor(
    readonly auth: AuthService,
    private readonly dialog: MatDialog,
    private readonly router: Router,
    readonly languageService: LanguageService,
  ) {}
  login(): void {
    const ref = this.dialog.open(LoginDialog);
    ref.afterClosed().subscribe((result) => {
      if (result === 'forgot') this.dialog.open(ForgotDialog);
    });
  }
  profile(): void {
    void this.router.navigate(['/profile']);
  }

  setLanguage(language: string): void {
    if (language === 'en' || language === 'bg') {
      this.languageService.setLanguage(language as Language);
    }
  }
}
