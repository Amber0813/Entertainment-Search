import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders,HttpParams } from '@angular/common/http';
import {Observable} from 'rxjs';

const httpOptions = {
  headers: new HttpHeaders({ "Content-Type": "application/json" })
};

@Injectable({
  providedIn: 'root'
})

export class SearchService {
  jsonData = null;
  showresult = false;
  disablebutton = true;
  progressbar = true;
  slidein = false;
  issearch = false;
  isfavorate = false; 
  isclear = false;
  result = "btn btn-primary";
  favor = "btn btn-outline-primary"; 
  storehashmap = (JSON.parse(localStorage.getItem('local_favorite')) == null ? {}: JSON.parse(localStorage.getItem('local_favorite')));

  constructor(public http: HttpClient) {}
  getGeolocation() {
    const url = 'http://ip-api.com/json';
    let json = this.http.get(url);
    return json;

  }

  getAutoData(form) {
    let params = new HttpParams()
    .set("keyword", form.keyword);
    let url = "http://mycsci571homeworkann.us-west-1.elasticbeanstalk.com/autocomplete";
    let json = this.http.get<any>(url, { params: params });
    console.log(json);
    return json;
  }

  search(form) {
    let params = new HttpParams()
    .set("keyword",form.keyword)
    .set("category", form.category)
    .set("distance", form.distance||"10")
    .set("unit", form.unit)
    .set("location", form.location)
    .set("linput", form.linput)
    .set("geo", JSON.stringify(form.geo));
    let url = "http://mycsci571homeworkann.us-west-1.elasticbeanstalk.com/a";
    console.log(JSON.stringify(form.geo));
    let response = null;
    response = this.http.get(url, { params: params });

    response.subscribe(
      data => {
        this.jsonData = data;
        this.disablebutton = true;
        if (this.jsonData._embedded) {
          this.jsonData._embedded.events.sort((a, b)=> a.dates.start.localDate <= b.dates.start.localDate ? -1 : 1);
        }
        console.log(this.jsonData);
      }
    );
    return response;
  }

  getEventResult() {
    return this.jsonData;
  }
}
