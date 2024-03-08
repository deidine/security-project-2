import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TokenStorageService } from './services/token-storage.service';

@Component({
  selector: 'app-logout',
  template: `
    <p>Logging out...</p>
  `,
  styles: []
})
export class LogoutComponent implements OnInit {

  constructor( private tokenStorage: TokenStorageService, private router: Router) { }

  ngOnInit(): void { 
    this.tokenStorage.signOut(); 
        localStorage.clear( );
        
    
    this.router.navigate(['']);
  }

}
