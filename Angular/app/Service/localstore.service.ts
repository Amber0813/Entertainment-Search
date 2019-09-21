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
    // console.log(str);
    var items = JSON.parse(str);
    return items;
    // return this.storage.get(STORAGE_KEY);
    // var str = localStorage.getItem(STORAGE_KEY);
    // var json = JSON.parse(str);
    // // console.log(json.list.length);
    // if (json.list.length == 0)
    //   return [];
    // else 
    //   return json.list;
  }

  delete(key) {
    console.log(key);
    var str = localStorage.getItem(STORAGE_KEY);
    var items = JSON.parse(str);
    console.log(items[key]);
    delete items[key];
    var jstr = JSON.stringify(items);
    localStorage.setItem(STORAGE_KEY, jstr);
    // const currentTodoList = this.storage.get(STORAGE_KEY);
    // var currentTodoList = localStorage.getItem(STORAGE_KEY);
    // var json = JSON.parse(currentTodoList);
    // json.list.splice(i, 1);
    // // console.log(json);
    // var jinput = JSON.stringify(json);
    // // this.storage.set(STORAGE_KEY, currentTodoList);
    // // console.log(this.storage.get(STORAGE_KEY));
    // localStorage.setItem(STORAGE_KEY, jinput);
  }

}
