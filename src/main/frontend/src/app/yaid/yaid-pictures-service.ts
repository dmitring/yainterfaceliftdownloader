import {Injectable} from '@angular/core';
import {Http, Response, URLSearchParams} from '@angular/http';
import {Observable} from "rxjs/Rx";

import {endpoints} from '../endpoints'

import {YaidPictureEntity} from './yaid-picture-entity';

@Injectable()
export class YaidPicturesService {
  private pictureUrl:string = endpoints.getPicturesUrl();

  constructor(private http:Http) {
  }

  public getData(statuses:string[], pageNumber:number, pageItemSize:number):Observable<any> {
    let params:URLSearchParams = new URLSearchParams();
    params.set('page', pageNumber.toString());
    params.set('size', pageItemSize.toString());
    let statusParam = statuses
      .map(status => "status=" + status)
      .join('&');
    params.appendAll(new URLSearchParams(statusParam));

    return this.http.get(this.pictureUrl, {search: params})
      .map((res:Response) => res.json())
      .catch(this.handleError);
  }

  private handleError(error:any) {
    let errMsg = (error.message) ? error.message :
      error.status ? `${error.status} - ${error.statusText}` : 'Server error';
    console.error(errMsg);
    return Observable.throw(errMsg);
  }
}
