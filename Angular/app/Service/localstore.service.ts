import { Inject, Injectable } from '@angular/core';
import { SESSION_STORAGE, StorageService } from 'angular-webstorage-service';

const STORAGE_KEY = 'local_favorite';
@Injectable({
  providedIn: 'root'
})
export class LocalstoreService {

  constructor(@Inject(SESSION_STORAGE) public storage: StorageService) { }

  store(object) {
    var key = object.id;
    console.log(key);
    var str = localStorage.getItem(STORAGE_KEY);
    console.log(str);
    if (str == null) {
      var item = {
        [key]: object
      }
      var jstr = JSON.stringify(item);
      localStorage.setItem(STORAGE_KEY, jstr);
      console.log(item);
      console.log(item[key]);
      console.log(Object.values(item));
    }
    else {
      var items = JSON.parse(str);
      if (!items[key]) {
        items[key] = object;
        console.log(items);
        var jstr = JSON.stringify(items);
        localStorage.setItem(STORAGE_KEY, jstr);
      }
    }
  }

  getStorekey() {
    if (localStorage.getItem(STORAGE_KEY) == null)
      return {};
    var str = localStorage.getItem(STORAGE_KEY);
    var items = JSON.parse(str);
    return items;
  }

  delete(key) {
    console.log(key);
    var str = localStorage.getItem(STORAGE_KEY);
    var items = JSON.parse(str);
    console.log(items[key]);
    delete items[key];
    var jstr = JSON.stringify(items);
    localStorage.setItem(STORAGE_KEY, jstr);
  }

}
