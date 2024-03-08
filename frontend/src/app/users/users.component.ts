import {  UserInfoReg, RrgisterInfo } from '../login/services/login-check.service';
// import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatSnackBar, MatTableDataSource, MatPaginator, Sort, MatSort } from '@angular/material';
import { Component, OnInit, ViewChild } from '@angular/core';
 import { UserProfile } from '../models/UserModels';
import { LoginCheckService } from '../login/services/login-check.service';

@Component({
  selector: 'users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class usersComponent implements OnInit {

  public userName = localStorage.getItem("currentUserName");

  public user: UserProfile;

  addButton = true;
  addProduct = false;
  editProductBtn = false;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

  users: any;
  deidine: any;
  length: number;
  pageSize = 5;
  pageSizeOptions: number[] = [2, 5, 10];

  setPageSizeOptions(setPageSizeOptionsInput: string) {
    this.pageSizeOptions = setPageSizeOptionsInput.split(',').map(str => +str);
  }

  sortedData: UserProfile = new UserProfile();
  constructor( 
    private userservice: LoginCheckService,
    private snackBar: MatSnackBar,
    private router: Router,
  ) {

  }

  ngOnInit() {
    // if(localStorage.getItem("currentUserRole")==`["ROLE_CLIENT"]`){

    this.userservice.getUsers().subscribe(
      response => {

        this.users = response;
         
        this.userss = new MatTableDataSource(this.users);
        this.users.sort = this.sort;
        this.users.paginator = this.paginator;
        console.log("deidine " + this.users)
      });
    // }
  }


  public userss: MatTableDataSource<any>;

  ngAfterViewInit() {

  }

  starter() {

  }


  handleSuccessfulResponse(response) {
    this.user = response;
    this.userName = this.user.username;

  }

  onClickProfile() {

    this.router.navigate(['/my_profile']);

  }

  onClickLogout() {
    console.log("Logout Clicked");

    console.log("cleared User Id " + localStorage.getItem("currentUserId"));
    localStorage.clear();

    this.snackBar.open("User Logged Out", "Close", {
      duration: 2000, verticalPosition: 'top', panelClass: ['green-snackbar']
    });

    this.router.navigate(['']);

  }

  handle(response) {

  }
 
  displayedColumns: string[] = [  'id', 'email', 'name','provider','emailVerified','imageUrl','appUserRoles', 'edit', 'delete'];

  editusers(element) {
    let usersId = element.usersId;

    console.log(usersId);

    // this.router.navigate(['/edit-users',element])
    this.router.navigate(['/edit-users'], { queryParams: { usersId: usersId } })
  }

  deleteusers(element) {
    let usersId = element.id;
    this.userservice.deleteUser(usersId).subscribe(data => {
      console.log(data);
      this.router.navigate(['/users']);
      alert("deleted")

      // this.getEmployees();
    },
      err => alert("not deleted"))
    console.log(usersId);
    this.reloadPage()
  }


  handleUpdate(resp) {
    console.log("Updated Product");

    this.snackBar.open("Product Updated", "Close", {
      duration: 4000, verticalPosition: 'top', panelClass: ['green-snackbar']
    });

    // this.userservice.getUserProfiles().subscribe(response => this.handle(response));

  }

  public onClickCancel() {
    this.addButton = !this.addButton;
    this.addProduct = !this.addProduct;
    // this.userservice.getUserProfiles().subscribe(response => this.handle(response));

  }

  onClickAddusers() {

    console.log("Routing to add New_product page");

    this.router.navigate(['/add-dep']);

  }

  sortData(sort: Sort) {
    const data = this.users.slice();
    if (!sort.active || sort.direction === '') {
      this.sortedData = data;
      return;
    }

    this.sortedData = data.sort((a, b) => {
      const isAsc = sort.direction === 'asc';
      switch (sort.active) {
        case 'usersUrl': return compare(a.usersUrl, b.usersUrl, isAsc);
        case 'usersId': return compare(a.usersId, b.usersId, isAsc);
        case 'usersName': return compare(a.usersName, b.usersName, isAsc);
        case 'usersTire': return compare(a.usersTire, b.usersTire, isAsc);
        default: return 0;
      }
    });
  }

  reloadPage(): void {
    window.location.reload();
  }
}

// function compare(productId: any, productId1: any, isAsc: boolean) {
//   throw new Error('Function not implemented.');
// }
function compare(a: number | string, b: number | string, isAsc: boolean) {
  return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
}