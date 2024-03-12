import { Injectable } from '@angular/core';
export const TOKEN = 'token';

const TOKEN_KEY = 'auth-token';
const USER_KEY = 'auth-user';
const IMAGE_QRCODE = 'image-qrcode';

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {
  constructor() { }

  signOut(): void {
    window.sessionStorage.clear();
  }

  public saveToken(token: string): void {
    window.sessionStorage.removeItem(TOKEN_KEY);
    window.sessionStorage.setItem(TOKEN_KEY, token);
  }
  setAuthToken(token) {
    
    localStorage.setItem(TOKEN, token);
  }
  public getToken(): string | null {
    return window.sessionStorage.getItem(TOKEN_KEY);
  }

  public saveUser(user: any): void {
    window.sessionStorage.removeItem(USER_KEY);
    window.sessionStorage.setItem(USER_KEY, JSON.stringify(user));
  }

  public saveUserQrImage(data: any): void {
    window.sessionStorage.removeItem(IMAGE_QRCODE);
    window.sessionStorage.setItem(IMAGE_QRCODE, JSON.stringify(data));
  }
  public getUser(): any {
    const user = window.sessionStorage.getItem(USER_KEY);
    if (user) {
      return JSON.parse(user);
    }

    return {};
  }
  public getUserFromQRCode(): any {
    const data = window.sessionStorage.getItem(IMAGE_QRCODE);
    if (data) {
      return JSON.parse(data);
    }

    return {};
  }

}
