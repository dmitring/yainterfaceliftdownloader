import {Component, Input} from '@angular/core';
import {Button} from 'primeng/primeng';

import {YaidPictureEntity} from '../yaid-picture-entity';
import {YaidPictureConsiderService} from '../yaid-picture-consider-service';

@Component({
  moduleId: module.id,
  selector: 'yaid-picture',
  templateUrl: 'yaid-picture.component.html',
  directives: [Button]
})
export class YaidPictureComponent {
  @Input() picture:YaidPictureEntity;

  constructor(private pictureConsiderService:YaidPictureConsiderService) {

  }

  private isAcceptable():boolean {
    return this.picture.isAcceptable();
  }

  private isCosiderable():boolean {
    return this.picture.isCosiderable();
  }

  private isRejectable():boolean {
    return this.picture.isRejectable();
  }

  private accept() {
    this.pictureConsiderService.acceptPicture(this.picture);
  }

  private returnToConsider() {
    this.pictureConsiderService.returnPictureToConsider(this.picture);
  }

  private reject() {
    this.pictureConsiderService.rejectPicture(this.picture);
  }
}
