
import {environment} from './environment'

export class endpoints {
  private static picturesUrl:string = 'pictures';
  private static considerUrl:string = 'pictures/user_consider';

  private static getPrefix():string {
    if (environment.production == true) {
      return '';
    } else {
      return 'http://localhost:8080/';
    }
  }

  public static getPicturesUrl():string {
    return endpoints.getPrefix()+endpoints.picturesUrl;
  }

  public static getConsiderUrl():string {
    return endpoints.getPrefix()+endpoints.considerUrl;
  }
}
