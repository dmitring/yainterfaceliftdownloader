export class YaidPictureStatus {
  public value: string;
  public label: string;

  public static CONSIDERING: YaidPictureStatus = {value: "CONSIDERING", label: "Considering"};
  public static ACCEPTED: YaidPictureStatus = {value: "ACCEPTED", label: "Accepted"};
  public static REJECTED: YaidPictureStatus = {value: "REJECTED", label: "Rejected"};
  public static DOWNLOADED: YaidPictureStatus = {value: "DOWNLOADED", label: "Downloaded"};

  public static getStatusByValue(statusValue: string): YaidPictureStatus {
    return YaidPictureStatus.statuses.find(pictureStatus => pictureStatus.value == statusValue);
  }

  public static statuses = [
    YaidPictureStatus.CONSIDERING,
    YaidPictureStatus.ACCEPTED,
    YaidPictureStatus.REJECTED,
    YaidPictureStatus.DOWNLOADED
  ];
}

export class YaidPictureEntity {
  id:string;
  title:string;
  status:YaidPictureStatus;
  thumbnailDownloadUrl:string;
  fullPictureDownloadUrl:string;

  constructor(id:string, title:string, status:string, thumbnailDownloadUrl:string, fullPictureDownloadUrl:string) {
    this.id = id;
    this.title = title;
    this.status = YaidPictureStatus.getStatusByValue(status);
    this.thumbnailDownloadUrl = thumbnailDownloadUrl;
    this.fullPictureDownloadUrl = fullPictureDownloadUrl;
  }

  public isAcceptable() : boolean {
    return (this.status == YaidPictureStatus.CONSIDERING || this.status == YaidPictureStatus.REJECTED);
  }

  public isCosiderable() : boolean {
    return this.status != YaidPictureStatus.CONSIDERING;
  }

  public isRejectable() : boolean {
    return this.status != YaidPictureStatus.REJECTED;
  }
}
