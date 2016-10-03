import {Component, OnInit} from '@angular/core';
import {SelectButton, SelectItem, Button, Paginator} from 'primeng/primeng';

import {YaidPicturesModel} from "../yaid-pictures-model";
import {YaidPictureComponent} from "../yaid-picture.component/yaid-picture.component";

@Component({
  moduleId: module.id,
  selector: 'yaid-paginator',
  templateUrl: 'yaid-paginator.component.html',
  directives: [Paginator],
})
export class YaidPaginatorComponent {
  constructor(private picturesModel:YaidPicturesModel) {
  }

  private pageChanged(event:any) {
    this.picturesModel.getCurrentStatusesData(event.page, event.rows);
  }
}
