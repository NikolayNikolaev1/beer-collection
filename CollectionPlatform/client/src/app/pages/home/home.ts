import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { LoginDialog } from '../../components/dialogs/login-dialog/login-dialog';
import { ForgotDialog } from '../../components/dialogs/forgot-dialog/forgot-dialog';
import { RegisterDialog } from '../../components/dialogs/register-dialog/register-dialog';
import { ResetDialog } from '../../components/dialogs/reset-dialog/reset-dialog';

@Component({
  selector: 'app-home',
  standalone: false,
  styleUrl: './home.scss',
  templateUrl: './home.html',
})
export class Home {
  constructor(
    readonly auth: AuthService,
    private readonly dialog: MatDialog,
    private readonly router: Router,
  ) {
    this.resetFromUrl();
  }
  login(): void {
    const ref = this.dialog.open(LoginDialog);
    ref.afterClosed().subscribe((result) => {
      if (result === 'forgot') this.dialog.open(ForgotDialog);
    });
  }
  register(): void {
    this.dialog.open(RegisterDialog);
  }
  forgot(): void {
    this.dialog.open(ForgotDialog);
  }
  profile(): void {
    void this.router.navigate(['/profile']);
  }
  resetFromUrl(): void {
    const params = new URLSearchParams(location.search);
    const userId = params.get('userId');
    const token = params.get('token');
    if (userId && token) this.dialog.open(ResetDialog, { data: { userId, token } });
  }
}
