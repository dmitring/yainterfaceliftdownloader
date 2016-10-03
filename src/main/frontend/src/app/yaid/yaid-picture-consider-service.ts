import {Injectable}    from '@angular/core';
import {Headers, Http} from '@angular/http';

import {endpoints} from '../endpoints'

import {YaidPictureEntity, YaidPictureStatus} from './yaid-picture-entity';
import {YaidErrorModel} from './yaid-error-model';
import {YaidPicturesModel} from './yaid-pictures-model';

class YaidPictureConsiderRequestBody {
  private accepted:string[];
  private returnedToConsider:string[];
  private rejected:string[];

  constructor(accepted:string[], returnedToConsider:string[], rejected:string[]) {
    this.accepted = accepted;
    this.returnedToConsider = returnedToConsider;
    this.rejected = rejected;
  }
}

@Injectable()
export class YaidPictureConsiderService {
  private considerUrl:string = endpoints.getConsiderUrl();

  constructor(private http:Http, private picturesModel:YaidPicturesModel, private errorModel:YaidErrorModel) {
  }

  public acceptPicture(picture:YaidPictureEntity) {
    return this.postConsiderResults([picture], [], []);
  }

  public returnPictureToConsider(picture:YaidPictureEntity) {
    return this.postConsiderResults([], [picture], []);
  }

  public rejectPicture(picture:YaidPictureEntity) {
    return this.postConsiderResults([], [], [picture]);
  }

  private prepareConsiderRequestBody(pictures:YaidPictureEntity[]):string[] {
    return pictures.map((picture:YaidPictureEntity) => picture.id);
  }

  private handleSuccess(pictures:YaidPictureEntity[], newStatus:YaidPictureStatus) {
    pictures.forEach(picture => this.picturesModel.applyStatusChange(picture, newStatus));
  }

  private postConsiderResults(
    accepted:YaidPictureEntity[],
    returnedToConsider:YaidPictureEntity[],
    rejected:YaidPictureEntity[]) {
    let body = JSON.stringify(new YaidPictureConsiderRequestBody(
      this.prepareConsiderRequestBody(accepted),
      this.prepareConsiderRequestBody(returnedToConsider),
      this.prepareConsiderRequestBody(rejected)
    ));
    let headers = new Headers({'Content-Type': 'application/json'});

    this.http.post(this.considerUrl, body, {headers: headers}).subscribe(
      data => {
      },
      err => this.errorModel.handleError(err),
      () => {
        this.errorModel.handleSuccess();
        this.handleSuccess(accepted, YaidPictureStatus.ACCEPTED);
        this.handleSuccess(returnedToConsider, YaidPictureStatus.CONSIDERING);
        this.handleSuccess(rejected, YaidPictureStatus.REJECTED);
      }
    );
  }
}
