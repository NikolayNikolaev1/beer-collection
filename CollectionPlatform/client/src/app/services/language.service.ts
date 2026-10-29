import { DOCUMENT } from '@angular/common';
import { Injectable, Inject, signal } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

export const SUPPORTED_LANGUAGES = ['en', 'bg'] as const;
export type Language = (typeof SUPPORTED_LANGUAGES)[number];

@Injectable({ providedIn: 'root' })
export class LanguageService {
  readonly language = signal<Language>('en');
  private readonly storageKey = 'collection-platform-language';

  constructor(
    private readonly translate: TranslateService,
    @Inject(DOCUMENT) private readonly document: Document,
  ) {
    this.translate.addLangs([...SUPPORTED_LANGUAGES]);
    this.translate.setFallbackLang('en');
    const saved = localStorage.getItem(this.storageKey);
    this.setLanguage(this.isSupported(saved) ? saved : 'en');
  }

  setLanguage(language: Language): void {
    if (!this.isSupported(language)) return;
    this.language.set(language);
    localStorage.setItem(this.storageKey, language);
    this.document.documentElement.lang = language;
    this.translate.use(language).subscribe(() => this.updateTitle());
  }

  private updateTitle(): void {
    this.translate.get('app.title').subscribe((title) => (this.document.title = title));
  }

  private isSupported(value: string | null): value is Language {
    return value !== null && SUPPORTED_LANGUAGES.includes(value as Language);
  }
}
