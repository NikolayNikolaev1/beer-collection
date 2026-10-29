import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, provideHttpClient, withInterceptors } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatToolbarModule } from '@angular/material/toolbar';
import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { authInterceptor } from './auth.interceptor';
import { ProfileComponent } from './components/profile/profile';
import { LoginDialog } from './components/dialogs/login-dialog/login-dialog';
import { RegisterDialog } from './components/dialogs/register-dialog/register-dialog';
import { ForgotDialog } from './components/dialogs/forgot-dialog/forgot-dialog';
import { ResetDialog } from './components/dialogs/reset-dialog/reset-dialog';
import { DialogBase } from './components/core/dialog-base/dialog-base';
import { Home } from './pages/home/home';
import { Nav } from './components/core/nav/nav';
import { TranslateDirective, TranslatePipe, provideTranslateService } from '@ngx-translate/core';
import { provideTranslateHttpLoader } from '@ngx-translate/http-loader';

@NgModule({
  declarations: [
    App,
    LoginDialog,
    RegisterDialog,
    ForgotDialog,
    ResetDialog,
    ProfileComponent,
    DialogBase,
    Home,
    Nav,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatToolbarModule,
    TranslatePipe,
    TranslateDirective,
  ],
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideHttpClient(withInterceptors([authInterceptor])),
    provideTranslateService({ fallbackLang: 'en' }),
    provideTranslateHttpLoader({ prefix: './i18n/', suffix: '.json' }),
  ],
  bootstrap: [App],
})
export class AppModule {}
