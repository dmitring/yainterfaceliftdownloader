import {Component,} from '@angular/core';
import {SelectButton, Button} from 'primeng/primeng';

import {YaidPictureStatus} from "../yaid-picture-entity";
import {YaidPicturesModel} from "../yaid-pictures-model";

@Component({
  moduleId: module.id,
  selector: 'yaid-status-filter',
  templateUrl: 'yaid-status-filter.component.html',
  directives: [Button, SelectButton],
})
export class YaidStatusFilterComponent {
  private statuses = YaidPictureStatus.statuses;
  private userSelectedStatuses:string[];

  constructor(private picturesModel:YaidPicturesModel) {
    this.userSelectedStatuses = picturesModel.currentlySelectedStatuses;
  }

  private applyStatusFilter() {
    this.picturesModel.getCurrentPageData(this.userSelectedStatuses);
  }

  private isDisabled():boolean {
    return this.userSelectedStatuses.length < 1;
  }

  private getCurrentlySelected():string {
    return this.picturesModel.currentlySelectedStatuses
      .map(status => this.statuses.find(selectItem => selectItem.value == status).label)
      .join(', ');
  }
}
