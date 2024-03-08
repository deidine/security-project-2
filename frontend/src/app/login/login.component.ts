import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormControl, Validators, FormGroup, FormBuilder } from '@angular/forms';
 import { TokenStorageService } from './services/token-storage.service';

import { LoginCheckService, UserInfo, LoginInfo } from './services/login-check.service';
import { AuthenticatorService } from './services/authenticator.service';
import { MatSnackBar } from '@angular/material';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})

export class LoginComponent implements OnInit {

  //  loginDetails = new LoginDetails('','');

  errorl=''
  loading = false;
  submitted = false;
  returnUrl: string;
   loginFormError = false;

  loginInfo: LoginInfo = new LoginInfo();
  user: UserInfo = new UserInfo();
  userStorage: any;
  roles: string[] = [];


  constructor(private fb: FormBuilder,
    private router: Router,
    private loginService: LoginCheckService,
    private authenticateService: AuthenticatorService,
    private snackBar: MatSnackBar, private tokenStorage: TokenStorageService
  ) { }
  isLoggedIn = false;
  isLoginFailed = false;

  ngOnInit() {

    this.authenticateService.checkLogin();
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
      this.roles = this.tokenStorage.getUser().roles;
    }
  }

  loginForm: FormGroup = this.fb.group({
    ipusername: ['', [Validators.required]],
     ipPassword: ['', [Validators.required, Validators.minLength(5)]]

  });

  get f() { return this.loginForm.controls; }

  handleSuccessfulResponse(response) {
    this.user = response;
     this.userStorage = this.tokenStorage.getUser()
    localStorage.setItem("currentUserId", JSON.stringify(this.userStorage.id));
    localStorage.setItem("currentUserEmail", JSON.stringify(this.userStorage.email));
    localStorage.setItem("currentUserRole", JSON.stringify(this.userStorage.roles));
    localStorage.setItem("currentUserName", (this.userStorage.username));
    localStorage.setItem("UserToken", (this.tokenStorage.getToken()));
  }

  validateSubmit() {
    console.log("Inside validate Login method");
    this.loginInfo.email = this.f.ipusername.value;
    this.loginInfo.password = this.f.ipPassword.value;


    this.loginService.login(this.loginInfo.email, this.loginInfo.password).subscribe(
      (response: any) => {
        // If the response is successful
        // Save token and user information
        this.tokenStorage.saveToken(`Bearer ${response.accessToken}`);
        this.tokenStorage.setAuthToken('Bearer ' + response.accessToken);
        this.tokenStorage.saveUser(response);
        console.log(response);
        this.handleSuccessfulResponse(response);
        this.isLoginFailed = false;
        this.isLoggedIn = true;
    
        // Check user role and navigate accordingly
        if (response.roles.includes("ROLE_CLIENT")) {
          this.forward("/getallentite");
        } else if (response.roles.includes("ROLE_ADMIN")) {
          this.forward("/users");
        } else {
          // Handle other roles or unexpected responses
          alert("Unknown role received: " + response.roles);
        }
      },
      (error: any) => {
        // If there's an error in the response
        // this.handleErrorResponse(error);
        this.loginFormError=true
        this.errorl=error
        console.log("Error:", error);
        // this.forward('');
      }
    );
    
  }
  forward(url): void {
    window.location.href=url;
  }
  onSubmit() {

    this.validateSubmit();

    if (this.user.result === false) {
      this.loginFormError = true;
    }

  }

}


