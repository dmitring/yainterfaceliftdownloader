import {Injectable} from "@angular/core";

@Injectable()
export class YaidErrorModel {
  error:any;

  public handleError(error:any) {
    let errMsg = (error.message) ? error.message : error;
    error.status ? `${error.status} - ${error.statusText}` : 'Server error';
    console.error(errMsg);
    this.error = errMsg;
  }

  public handleSuccess() {
    this.error = null;
  }
}
