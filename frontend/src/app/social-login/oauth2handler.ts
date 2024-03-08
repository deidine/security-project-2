 
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
    import { OauthLoginService } from '../services/oauth-login.service';
    import { UserInfo } from '../login/services/login-check.service';
    import { TokenStorageService } from '../login/services/token-storage.service';
    // import { AngularBootstrapToastsService } from 'angular-bootstrap-toasts';
    @Component({
        selector: 'auth-handler',
        template: '<h1>Done!</h1>'
    }) 
    
    @Component({
        selector: 'auth-handler',
        template: '<h1>Done!</h1>'
    })
    export class Oauth2Handler implements OnInit {
    
        constructor(private route: ActivatedRoute,private tokenStorage: TokenStorageService,
                    private router: Router,
                    private loginService: OauthLoginService,
                    // private toastService: AngularBootstrapToastsService
                    )
        {}
      user: UserInfo = new UserInfo();
      userStorage: any;
      handleSuccessfulResponse(response) {
        this.user = response;
         this.userStorage = this.tokenStorage.getUser()
        localStorage.setItem("currentUserId", JSON.stringify(this.userStorage.id));
        localStorage.setItem("currentUserEmail", JSON.stringify(this.userStorage.email));
        localStorage.setItem("currentUserRole", JSON.stringify(this.userStorage.roles));
        localStorage.setItem("currentUserName", (this.userStorage.username));
        localStorage.setItem("UserToken", (this.tokenStorage.getToken()));
      }
    ngOnInit() {
        let token: string = null;
        let error: string = null;
        this.route.queryParams.subscribe(params => {
            token = params['token'];
            error = params['error']
            if(error) {
                // this.createToasts("Error", error);
                console.log(error)
                this.router.navigate(['login']);
            } else {
                this.handleSuccessfulResponse(params);
                this.loginService.setAuthToken('Bearer ' + token);
                // this.createToasts("Login Success", "You are logged in successfully");
                this.router.navigate(['home']);
            } 
        });
    }

    // createToasts(title, message) {
    //     this.toastService.changeDefaultTitle(title);
    //     this.toastService.changeDefaultText(message);
    //     this.toastService.changeDefaultDuration(5000);
    //     this.toastService.showSimpleToast({ showProgressLine: true });
    // }
}