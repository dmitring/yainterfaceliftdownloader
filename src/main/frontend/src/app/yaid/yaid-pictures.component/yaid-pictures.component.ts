import {Component, OnInit} from '@angular/core';

import {YaidPicturesModel} from "../yaid-pictures-model";
import {YaidPictureComponent} from "../yaid-picture.component/yaid-picture.component";
import {YaidPaginatorComponent} from "../yaid-paginator.component/yaid-paginator.component";
import {YaidStatusFilterComponent} from "../yaid-status-filter.component/yaid-status-filter.component";
import {YaidErrorComponent} from "../yaid-error.component/yaid-error.component"

@Component({
  moduleId: module.id,
  selector: 'yaid-pictures',
  templateUrl: 'yaid-pictures.component.html',
  directives: [YaidErrorComponent, YaidPaginatorComponent, YaidPictureComponent, YaidStatusFilterComponent]
})
export class YaidPicturesComponent implements OnInit {
  constructor(private picturesModel:YaidPicturesModel) {
  }

  ngOnInit() {
    this.picturesModel.getCurrentData();
  }
}
