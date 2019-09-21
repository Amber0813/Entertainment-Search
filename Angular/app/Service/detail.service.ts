import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders,HttpParams } from '@angular/common/http';
import {Observable} from 'rxjs';

const httpOptions = {
  headers: new HttpHeaders({ "Content-Type": "application/json" })
};

@Injectable({
  providedIn: 'root'
})
export class DetailService {
  id = -1;
  details = false;
  event = false;
  artist = false;
  venue = false;
  upcomingevent = false;
  detailData = null;
  artistData = null;
  venueData = null;
  location = null;
  locquery = '';
  followers = '';
  upcomingData = null;
  upcomingnum = 0;
  
  constructor(public http: HttpClient) { }

  getDetails(id) {
    console.log(this.details);
    console.log(id);
    let params = new HttpParams()
    .set('eventid', id);
    let url = "http://mycsci571homeworkann.us-west-1.elasticbeanstalk.com/details";
    console.log(url);
    let response = null;
    this.details = true;
    response = this.http.get(url, { params: params });
    response.subscribe(
      data => {
        this.detailData = data;
        console.log(data);
      }
    );
    // this.getUpcomingEvents();
  }

  getArtist() {
    let a = {
      attractions:[]
    };
    if (!this.detailData._embedded.attractions) {
      this.artistData = {
        error:'true'
      };
    }
    else {
      for (var attraction of this.detailData._embedded.attractions) {
        let temp = {name: attraction.name};
        a['attractions'].push(temp);
      }
      console.log(a);
      let params = new HttpParams()
      .set('artist',JSON.stringify(a))
      .set('type',this.detailData.classifications[0].segment.name);
      let url = "http://mycsci571homeworkann.us-west-1.elasticbeanstalk.com/artist";
      console.log(url);
      let response = null;
      response = this.http.get(url, { params: params });
      response.subscribe(
        data => {
          this.artistData = data;
          console.log(this.artistData);
        }
      );
    }
    
  }
  getVenue() {
    let venue = this.detailData._embedded.venues[0].name;
    let params = new HttpParams()
    .set('venue',venue);
    let response = null;
    let url = "http://mycsci571homeworkann.us-west-1.elasticbeanstalk.com/venue";
    console.log(url);
    response = this.http.get(url, { params: params });
    response.subscribe(
      data => {
        this.venueData = data;
        this.location = data._embedded.venues[0].location;
        this.location.latitude = parseFloat(this.location.latitude);
        this.location.longitude = parseFloat(this.location.longitude);
        this.locquery = encodeURI(data._embedded.venues[0].name);
        console.log(this.venueData);
        console.log(this.location);
      }
    );
  }

  getUpcomingEvents() {
    let venue = this.detailData._embedded.venues[0].name;
    let params = new HttpParams()
    .set('venue',venue);
    let response = null;
    let url = "http://mycsci571homeworkann.us-west-1.elasticbeanstalk.com/upcomingevents";
    console.log(url);
    response = this.http.get(url, { params: params });
    response.subscribe(
      data => {
        if (data.error && data.error == "true")
          this.upcomingnum = 0;
        else if(data.resultsPage.totalEntries == 0)
        this.upcomingnum = 0;
        else
          this.upcomingnum = data.resultsPage.results.event.length;
        this.upcomingData = data;
        console.log(this.upcomingData);
        console.log(this.upcomingnum);
      }
    );
  }
  
}
