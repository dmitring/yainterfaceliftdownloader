
import { Component } from '@angular/core';

import {Messages, Message} from 'primeng/primeng';

import {YaidErrorModel} from "../yaid-error-model";

@Component({
  moduleId: module.id,
  selector: 'yaid-error',
  templateUrl: 'yaid-error.component.html',
  directives: [Messages]
})
export class YaidErrorComponent{
  constructor(private errorModel:YaidErrorModel) {
  }

  private getMessages() : Message[] {
    if (this.errorModel.error)
      return [{severity:'error', summary:'Connection error', detail:this.errorModel.error}]
    else
      return [];
  }
}
