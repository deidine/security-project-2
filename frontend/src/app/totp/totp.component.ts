import { Component, OnInit } from '@angular/core';
import { TokenStorageService } from '../login/services/token-storage.service';
import { LoginCheckService } from '../login/services/login-check.service';
 

@Component({
  selector: 'app-totp',
  templateUrl: './totp.component.html',
  styleUrls: ['./totp.component.css']
})
export class TotpComponent implements OnInit {

  form: any = {};
  isLoggedIn = false;
  isLoginFailed = false;
  errorMessage = '';
  currentUser: any;
  qrCodeImage = '';
  email='';
  isUsing2FA=false;
  message;
data:any;
  constructor( private loginService: LoginCheckService, private tokenStorage: TokenStorageService) {}

  ngOnInit(): void {
  	if (this.tokenStorage.getUserFromQRCode()) {
        this.data=this.tokenStorage.getUserFromQRCode();
        this.qrCodeImage=this.data.qrCodeImage;
        this.email=this.data.email;
        this.isUsing2FA=this.data.using2FA;
    }
  }

  onSubmit(): void {
    this.loginService.verify(this.form.code,this.email).subscribe(
      data => {
        this.isLoggedIn=true 
        this.message=data.message
      },
      err => {
       
        this.errorMessage = err.error.message;
        this.isLoginFailed = true;
      }
    );
  }

  login(user): void {
	// this.tokenStorage.saveUser(user);
	// this.isLoginFailed = false;
	// this.isLoggedIn = true;
	// this.currentUser = this.tokenStorage.getUser();
    // window.location.reload();
  }
}
