
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
// it take the information from the link 
// https://localhost:4200/oauth2/redirect?user=com.example.springsocial.model.User@1ae32efa&token=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI1NyIsImlhdCI6MTcwOTk0MDAzMywiZXhwIjoxNzEwODA3NjMzfQ.Sqei8QheNLAn_kQDSz4f6SKd1oG201DJ-HzJSN1QKH3KLpJoD7_P6HyTVXnH_-3q-l5bmyBxQKFyphTiof8bsg
export class Oauth2Handler implements OnInit {

    constructor(private route: ActivatedRoute, private tokenStorage: TokenStorageService,
        private router: Router,
        private loginService: OauthLoginService,
        // private toastService: AngularBootstrapToastsService
    ) { }
    user: UserInfo = new UserInfo();
    userStorage: any;
    
    ngOnInit() {
        let token: string = null;
        let user: any;
        let error: string = null;
        this.route.queryParams.subscribe(params => {
            token = params['token'];
            user = params['user']
            error = params['error']
            if (error) {
                // this.createToasts("Error", error);
                console.log(error)
                this.router.navigate(['login']);
            } else { 
                localStorage.setItem("currentUserId", JSON.stringify(''));
                localStorage.setItem("currentUserEmail", JSON.stringify(params['email']));
                localStorage.setItem("currentUserRole", JSON.stringify(params['roles']));
                localStorage.setItem("currentUserName", (params['username'])); 
                this.loginService.setAuthToken('Bearer ' + token);
        this.tokenStorage.saveToken('Bearer ' + token);

                // this.createToasts("Login Success", "You are logged in successfully");
                this.router.navigate(['getallentite']);
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