import { NgModule , InjectionToken } from '@angular/core';
import { AuthGaurdService } from './services/auth-gaurd-service.service';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { MyProfileComponent } from './my-profile/my-profile.component';
import { AddDepComponent } from './department/add-dep/add-dep.component';
import { UpdatePasswordComponent } from './my-profile/update-password/update-password.component';
import { departmentComponent } from './department/department.component';
import { RegisterComponent } from './register/register.component';
import { EditDepComponent } from './department/edit-dep/edit-dep.component';
import { SidenavbarComponent } from './sidenavbar/sidenavbar.component';
// import { GetentiteByCategoryComponent } from './Entite/get-entite-by-category/get-entite-by-category.component';
// import { UpdateEntiteComponent } from './Entite/update-entite/update-entite.component';
import { GetAllentitesComponent } from './Entite/get-all-entites/get-all-entites.component';
import { AddEntiteComponent } from './Entite/add-entite/add-entite.component';
import { hover } from './header/hover.component';
import { LoginComponentOauth2 } from './oauth2/login/login.component';
import { RegisterComponent2 } from './oauth2/register/register.component';
import { Oauth2Handler } from './social-login/oauth2handler';
import { NotFoundComponent } from './social-login/not-found.component';
import { usersComponent } from './users/users.component';
import { AdminGuard } from './services/admin-guard.service';
import { LogoutComponent } from './login/LogoutComponent.component';

export const externalUrlProvider = new InjectionToken('externalUrlRedirectResolver');

const routes: Routes = [
  { path: "", component: LoginComponent },
  { path: 'oauth2/redirect', component: Oauth2Handler },
  { path: 'externalRedirect', canActivate:   [externalUrlProvider], component: NotFoundComponent },
  { path: 'logout', component: LogoutComponent },
  { path: "logiOauth2", component: LoginComponentOauth2 },
  { path: "regOauth2", component: RegisterComponent2 },
  { path: 'login', component: LoginComponent },
  { path: 'sidnavbar', component: SidenavbarComponent },
  { path: 'update-password', component: UpdatePasswordComponent, canActivate: [AuthGaurdService] },
  { path: 'department', component: departmentComponent, canActivate: [AuthGaurdService] },
  // {path: 'distributor' , component: DistributerComponent,canActivate:[AuthGaurdService]},
  // {path: 'retailer' , component: RetailerComponent,canActivate:[AuthGaurdService]},
  { path: 'my_profile', component: MyProfileComponent, canActivate: [AuthGaurdService] },
  { path: 'add-dep', component: AddDepComponent, canActivate: [AuthGaurdService] },
  //  {path: 'add-retailer' , component: AddNewRetailerComponent },
  { path: 'edit-department', component: EditDepComponent, canActivate: [AuthGaurdService] },
  { path: 'page-not-found', component: PageNotFoundComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'getallentite', component: GetAllentitesComponent, canActivate: [AuthGaurdService] },
  { path: 'addentite', component: AddEntiteComponent, canActivate: [AuthGaurdService] },
  { path: 'hover', component: hover },
{path:'users',component:usersComponent  ,canActivate: [AdminGuard] },
  { path: '**', redirectTo: ('/page-not-found') },


];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

export const RoutingComponents = [LoginComponent, RegisterComponent];