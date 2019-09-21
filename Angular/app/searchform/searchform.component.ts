import { Component, OnInit } from '@angular/core';
import {Form} from './form';
import { SearchService } from '../Service/search.service';
import { DetailService } from '../Service/detail.service';
import {Observable} from 'rxjs';
import {map, startWith} from 'rxjs/operators';
import { LocalstoreService } from '../Service/localstore.service';


@Component({
  selector: 'app-searchform',
  templateUrl: './searchform.component.html',
  styleUrls: ['./searchform.component.css']
})
export class SearchformComponent implements OnInit {
  form: Form = {
    keyword: '',
    category: 'All',
    distance: '',
    unit: 'Miles',
    location: 'here',
    linput: '',
    geo: undefined
  };
  geojson = true; 
  filteredOptions: Observable<string[]>;
  autoComps;
  resultjson = null;
  timeout: any;
  // testhtml='';

  constructor(public searchService: SearchService, public detailService: DetailService, public lstoreService: LocalstoreService) { }

  events = [
    'All',
    'Music',
    'Sports',
    'Arts & Theatre',
    'Film',
    'Miscellaneous',
  ];

  options = [
    'one',
    'two',
    'three'
  ];

  units = [
    'Miles',
    'Kilometers',
  ];

  locs = [
    'here',
    'locinput',
  ];
  red = '';

  isDisabled() {
    if (this.form.location === 'here') {
    return true;
    } else {
    return false;
    }
  }

  getGeoLoc() {
    this.searchService.getGeolocation().subscribe(data => {
      this.form.geo = {
        lat: data["lat"],
        lng: data["lon"] 
      };
      // console.log(this.form.geo); 
      this.geojson = false;
    });
  }

  doFilter() {
    this.autoComps = this.searchService.getAutoData(this.form)
    .pipe(
      map(autoComps =>this.filter(autoComps._embedded.attractions)),
    );
    // console.log(this.autoComps);
  }

  filter(values) {
    console.log(values);
    let j = values.filter(attraction => attraction.name);
    console.log(j);
    return j;
  }

  onSubmit() {
    console.log(JSON.parse(localStorage.getItem('local_favorite')));
    // console.log(Object.values(JSON.parse(localStorage.getItem('local_favorite'))).length);
    this.searchService.result = "btn btn-primary";
    this.searchService.favor = "btn btn-outline-primary";
    this.searchService.isclear = true;
    console.log(this.form.unit)
    this.searchService.search(this.form);
    this.searchService.showresult = false;
    this.searchService.progressbar = false;
    this.searchService.issearch = true;
    this.searchService.jsonData = null;
    this.detailService.detailData = null;
    this.detailService.artistData = null;
    this.detailService.venueData = null;
    this.detailService.upcomingData = null;
    this.detailService.id = -1;
    this.detailService.event = false;
    this.detailService.artist = false;
    this.detailService.venue = false;
    this.detailService.upcomingevent = false;
    this.timeout = setTimeout(()=>{
      // this.searchService.search(this.form);
      this.searchService.isclear = false;
      this.searchService.showresult = true;
      this.detailService.details = false;
      this.searchService.progressbar = true;
    },1000);
    // this.searchService.showresult = true;
    // this.detailService.details = false;
  }
  onClear(keyword, linput) {
    this.searchService.result = "btn btn-primary";
    this.searchService.favor = "btn btn-outline-primary";
    keyword.reset();
    linput.reset();
    this.searchService.isclear = true;
    this.form.location = 'here';
    this.form.keyword = '';
    this.form.category = 'All';
    this.form.distance = '';
    this.form.linput = '';
    this.form.unit = 'Miles';
    this.searchService.showresult = false;
    this.searchService.progressbar = true;
    this.searchService.issearch = true;
    this.searchService.jsonData = null;
    this.detailService.detailData = null;
    this.detailService.artistData = null;
    this.detailService.venueData = null;
    this.detailService.upcomingData = null;
    this.detailService.id = -1;
    this.detailService.event = false;
    this.detailService.artist = false;
    this.detailService.venue = false;
    this.detailService.upcomingevent = false;
  }

  checkspace() {
    var str = this.form.keyword;
    var boole = str.trim().length === 0;
    if (boole) {
      this.red = "red";
    }
    else {
      this.red = "";
    }
    return boole;
  }

  ngOnInit() {
    this.getGeoLoc();
  }

}
