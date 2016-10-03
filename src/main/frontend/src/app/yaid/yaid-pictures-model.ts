import {Injectable}    from '@angular/core';

import {YaidPictureEntity, YaidPictureStatus} from './yaid-picture-entity';
import {YaidPicturesService} from './yaid-pictures-service';
import {YaidErrorModel} from './yaid-error-model';

@Injectable()
export class YaidPicturesModel {
  totalElements:number;
  totalPages:number;
  pageItemSize:number = 36;
  currentPage:number = 0;
  pictures:YaidPictureEntity[];
  currentlySelectedStatuses:string[] = [YaidPictureStatus.CONSIDERING.value];

  constructor(private pictureDataProvider:YaidPicturesService, private errorModel:YaidErrorModel) {
  }

  public applyStatusChange(picture:YaidPictureEntity, newStatus:YaidPictureStatus) {
    let pictureIndex = this.pictures.findIndex(candidate => candidate.id == picture.id);
    if (pictureIndex != -1) {
      console.log(picture, newStatus);
      if (this.needRemove(newStatus))
        this.pictures.splice(pictureIndex, 1);
      else
        this.pictures[pictureIndex].status = newStatus;
    }
  }

  public getCurrentData() {
    this.getData(this.currentlySelectedStatuses, this.currentPage, this.pageItemSize);
  }

  public getCurrentPageData(statuses:string[]) {
    this.getData(statuses, this.currentPage, this.pageItemSize);
  }

  public getCurrentStatusesData(pageNumber:number, pageItemSize:number) {
    this.getData(this.currentlySelectedStatuses, pageNumber, pageItemSize);
  }

  private getData(statuses:string[], pageNumber:number, pageItemSize:number) {
    this.pictureDataProvider.getData(statuses, pageNumber, pageItemSize).subscribe(
      json => {
        this.pictures = json.content.map((rawPicture:any) => this.extractPicture(rawPicture));
        this.totalElements = json.totalElements;
        this.totalPages = json.totalPages;
        this.pageItemSize = json.size;
        this.currentPage = json.number;
        this.currentlySelectedStatuses = statuses;
      },
      error => this.errorModel.handleError(error),
      () => this.errorModel.handleSuccess()
    );
  }

  private extractPicture(rawPicture:any):YaidPictureEntity {
    return new YaidPictureEntity(
      rawPicture.interfaceliftId,
      rawPicture.title,
      rawPicture.status,
      rawPicture.thumbnail.downloadUrl,
      rawPicture.fullPicture.downloadUrl
    );
  }

  private needRemove(checkingStatus:YaidPictureStatus):boolean {
    return (this.currentlySelectedStatuses.find(status => status == checkingStatus.value) == undefined);
  }
}
