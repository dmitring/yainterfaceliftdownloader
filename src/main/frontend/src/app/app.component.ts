import { Component } from '@angular/core';

import { YaidPicturesComponent } from "./yaid/yaid-pictures.component/yaid-pictures.component"
import { YaidPicturesService } from "./yaid/yaid-pictures-service"
import { YaidPictureConsiderService } from "./yaid/yaid-picture-consider-service"
import { YaidPicturesModel } from "./yaid/yaid-pictures-model"
import { YaidErrorModel } from "./yaid/yaid-error-model"

@Component({
    moduleId: module.id,
    selector: 'app-root',
    templateUrl: 'app.component.html',
    directives: [YaidPicturesComponent],
    providers: [YaidPicturesService, YaidPictureConsiderService, YaidPicturesModel, YaidErrorModel]
})
export class AppComponent {
    title:string = 'Yet another interfacelift.com picture pictureStreams';
}
