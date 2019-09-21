import { Component, OnInit } from '@angular/core';
import { SearchService } from '../Service/search.service';
// import { TruncateWordsPipe } from '../TruncatePipe.pipe';
import { ShowdetailsComponent } from '../showdetails/showdetails.component';
import { DetailService } from '../Service/detail.service';
import { LocalstoreService } from '../Service/localstore.service';
import { ProgressbarConfig } from 'ngx-bootstrap/progressbar';
import { trigger, transition, animate, style } from '@angular/animations'


@Component({
  selector: 'app-showresults',
  templateUrl: './showresults.component.html',
  styleUrls: ['./showresults.component.css'],
  animations: [
    trigger('slideInOut', [
      transition(':enter', [
        style({transform: 'translateX(-100%)'}),
        animate('400ms ease-in', style({transform: 'translateX(0%)'}))
      ])
    ]),
    trigger('slideIn', [
      transition(':enter', [
        style({transform: 'translateX(200%)'}),
        animate('400ms ease-in', style({transform: 'translateX(0%)'}))
      ])
    ])
  ]
})

export class ShowresultsComponent implements OnInit {
  
  constructor(public searchService: SearchService,public detailService: DetailService,public lstoreService: LocalstoreService) { }

  resultjson = this.searchService.jsonData;
  testhtml = '';
  // showresult = true;
  tableresult = false;
  noresult = false;
  // detaildisable = true;
  currentid = -1;
  result = this.searchService.result;
  favorite = this.searchService.favor;
  nofavor = true;
  storagearray = null;
  flag = false; //set if the details are visible or not,true not,   false visible
  buffer = false;
  progress: any;
  storehashmap = (JSON.parse(localStorage.getItem('local_favorite')) == null ? {}: JSON.parse(localStorage.getItem('local_favorite')));
  venueid = -1;
  showdet = false;

  show() {
    if (this.detailService.details == true)
      this.searchService.isfavorate = false;
    this.storagearray = null;
    this.searchService.result = "btn btn-primary";
    this.searchService.favor = "btn btn-outline-primary";
    this.result = this.searchService.result;
    this.favorite = this.searchService.favor;
    
    this.nofavor = false;
    if (this.detailService.details == false) {
      // this.searchService.progressbar = false;
      // this.progress = setTimeout(()=>{
        this.searchService.progressbar = true;
        this.searchService.showresult = true;
        this.resultDisplay();
        // this.result = "btn btn-primary";
        // this.favorite = "btn btn-outline-primary";
      // },1000);
      // this.searchService.showresult = true;
    }
    else if (this.searchService.showresult == false) {
      // this.searchService.progressbar = false;
      // this.progress = setTimeout(()=>{
        this.searchService.progressbar = true;
        this.searchService.showresult = false;
        this.flag = false;
        this.resultDisplay();
        // this.result = "btn btn-primary";
        // this.favorite = "btn btn-outline-primary";
      // },1000);
      // this.searchService.showresult = false;
      // this.flag = false;
    }
    // this.resultDisplay();
    // this.result = "btn btn-primary";
    // this.favorite = "btn btn-outline-primary";
    
  }

  favor() {
    this.searchService.isclear = false;
    this.searchService.isfavorate = true;
    this.searchService.result = "btn btn-outline-primary";
    this.searchService.favor = "btn btn-primary";
    this.result = this.searchService.result;
    this.favorite = this.searchService.favor;
    this.nofavor = false;
    this.flag = true;
    this.searchService.showresult = false;
      // this.flag = true;
      if (Object.values(this.lstoreService.getStorekey()).length == 0) {
        this.nofavor = true;
      }
      else {
        this.nofavor = false;
        this.searchService.storehashmap = this.lstoreService.getStorekey();
        this.storagearray = Object.values(this.searchService.storehashmap).length == 0 ? null : Object.values(this.searchService.storehashmap);
        console.log(this.storagearray);
      }
    // }
  }

  resultDisplay() {
    // if (this.detailService.details == false) {
    //   this.searchService.showresult = true;
    // }
    // else {
    //   this.searchService.showresult = false;
    //   this.flag = false;
    // }
    // this.resultjson = this.searchService.getEventResult();
    if (this.searchService.jsonData != null) {
      console.log(this.searchService.jsonData);
      if (this.searchService.jsonData.page.totalElements == 0) {
        this.noresult = true;
        // this.testhtml = "no result";
      }
      else {
        this.tableresult = true;
        this.tableresult = true;
        //this.testhtml = this.searchService.jsonData._embedded.events[0]._embedded.attractions[0].name;
      }
    }
  }

  time(i) {
    var html = '';
    if (this.searchService.jsonData._embedded.events[i].dates.start.localDate) {
      html += this.searchService.jsonData._embedded.events[i].dates.start.localDate;
    }
    else {
      html += 'N/A';
    }
    return html;
  }

  eventname(i) {
    var html = '';
    if (this.searchService.jsonData._embedded.events[i].name) {
      html += this.searchService.jsonData._embedded.events[i].name;
    }
    else {
      html += 'N/A';
    }
    return html;
  }

  category(i) {
    var html = '';
    if (this.searchService.jsonData._embedded.events[i].classifications) {
      if (this.searchService.jsonData._embedded.events[i].classifications[0].genre.name) {
        html += this.searchService.jsonData._embedded.events[i].classifications[0].genre.name + ' - ';
      }
      if (this.searchService.jsonData._embedded.events[i].classifications[0].segment.name) {
        html += this.searchService.jsonData._embedded.events[i].classifications[0].segment.name
      }
    }
    else {
      html += 'N/A';
    }
    return html;
  }

  venuename(i) {
    var html = '';
    if (this.searchService.jsonData._embedded.events[i]._embedded.venues[0].name) {
      html += this.searchService.jsonData._embedded.events[i]._embedded.venues[0].name;
    }
    else {
      html += 'N/A';
    }
    return html;
  }

  favordetail(i) {
    this.searchService.result = "btn btn-primary";
    this.searchService.favor = "btn btn-outline-primary";
    this.searchService.issearch = false;
    this.searchService.isfavorate = false;
    // this.detailService.details = true;
    this.searchService.showresult = false;
    this.detailService.details = true;
    this.searchService.disablebutton = false;
    this.nofavor = false;
    this.showdet = true;
    console.log(this.venueid == this.searchService.jsonData._embedded.events[i].id)
    console.log(this.venueid)
    if (this.venueid == this.searchService.jsonData._embedded.events[i].id) {
      this.flag = false;
    }
    else {
      this.searchService.progressbar = false;
      this.progress = setTimeout(()=>{
        this.searchService.progressbar = true;
        this.detailService.getDetails(this.searchService.jsonData._embedded.events[i].id);
        this.flag = false;
        this.showdet = false;
      },1000);
      this.detailService.event = true;
      this.detailService.artist = false;
      this.detailService.venue = false;
      this.detailService.upcomingevent = false;
      
    }
    
  }

  detail(i) {
    this.searchService.result = "btn btn-primary";
    this.searchService.favor = "btn btn-outline-primary";
    this.searchService.issearch = false;
    this.searchService.isfavorate = false;
    // this.detailService.details = true;
    this.searchService.showresult = false;
    this.searchService.disablebutton = false;
    this.nofavor = false;
    // this.detailService.getDetails(this.searchService.jsonData._embedded.events[i].id);
    // this.searchService.progressbar = false;
    if (this.detailService.id != i) {
    this.searchService.progressbar = false;
      this.progress = setTimeout(()=>{
        this.searchService.progressbar = true;
        this.detailService.getDetails(this.searchService.jsonData._embedded.events[i].id);
        this.flag = false;
      },1000);
    }
    else {
      this.searchService.progressbar = true;
        this.detailService.getDetails(this.searchService.jsonData._embedded.events[i].id);
        this.flag = false;
    }
    // this.progress = setTimeout(()=>{
    //   this.searchService.progressbar = true;
    //   this.detailService.getDetails(this.searchService.jsonData._embedded.events[i].id);
    //   this.flag = false;
    // },1000);
    // this.flag = false;
    console.log(this.detailService.details)
    if (this.detailService.id == i && this.detailService.details == true) {
      if (this.detailService.artist || this.detailService.venue || this.detailService.upcomingevent)
        this.detailService.event = false;
    }
    else {
      // this.progress = setTimeout(()=>{
      //   this.searchService.progressbar = true;
      //   this.detailService.getDetails(this.searchService.jsonData._embedded.events[i].id);
      //   this.flag = false;
      // },1000);
      this.detailService.id = i;
      this.currentid = i;
      this.venueid = this.searchService.jsonData._embedded.events[i].id;
      this.detailService.event = true;
      this.detailService.artist = false;
      this.detailService.venue = false;
      this.detailService.upcomingevent = false;
    }
    this.detailService.event = true;
      this.detailService.artist = false;
      this.detailService.venue = false;
      this.detailService.upcomingevent = false;
      
  }

  showdetail() {
    this.searchService.isfavorate = false;
    this.searchService.issearch = false;
    // this.detailService.details = true;
    this.searchService.showresult = false;
    this.detailService.event = true;
      this.detailService.artist = false;
      this.detailService.venue = false;
      this.detailService.upcomingevent = false;
    // this.flag = false;
    this.progress = setTimeout(()=>{
      this.detailService.details = true;
      this.flag = false;
    })
  }


  storeitem(i) {
    var item = this.searchService.jsonData._embedded.events[i];
    this.lstoreService.store(item);
    if (this.searchService.storehashmap[item.id]){
      this.lstoreService.delete(item.id);
      this.searchService.storehashmap = this.lstoreService.getStorekey();
      this.storagearray = Object.values(this.searchService.storehashmap).length == 0 ? null : Object.values(this.searchService.storehashmap);
    }
    else {
      // this.lstoreService.store(item);
      this.searchService.storehashmap = this.lstoreService.getStorekey();
      this.storagearray = Object.values(this.searchService.storehashmap).length == 0 ? null : Object.values(this.searchService.storehashmap);
    }
    
  }

  deleteitem(i) {
    // console.log(i);
    var hashmap = this.lstoreService.getStorekey();
    var array = Object.values(this.searchService.storehashmap);
    // console.log(this.storagearray);
    var key = this.storagearray[i].id;
    this.lstoreService.delete(key);
    this.searchService.storehashmap = this.lstoreService.getStorekey();
    this.storagearray = Object.values(this.searchService.storehashmap).length == 0 ? null : Object.values(this.searchService.storehashmap);
    if (this.lstoreService.getStorekey() == null || Object.values(this.lstoreService.getStorekey()).length == 0) {
      this.nofavor = true;
    }
    // console.log(this.storagearray);
    // this.searchService.templocal = this.lstoreService.getStorekey();
  }

  favortime(i) {
    var html = '';
    if (this.storagearray[i].dates.start.localDate) {
      html += this.storagearray[i].dates.start.localDate;
    }
    else {
      html += 'N/A';
    }
    return html;
  }

  favorcategory(i) {
    var html = '';
    if (this.storagearray[i].classifications) {
      if (this.storagearray[i].classifications[0].genre.name) {
        html += this.storagearray[i].classifications[0].genre.name + ' - ';
      }
      if (this.storagearray[i].classifications[0].segment.name) {
        html += this.storagearray[i].classifications[0].segment.name
      }
    }
    else {
      html += 'N/A';
    }
    return html;
  }

  favorvenuename(i) {
    var html = '';
    if (this.storagearray[i]._embedded.venues[0].name) {
      html += this.storagearray[i]._embedded.venues[0].name;
    }
    else {
      html += 'N/A';
    }
    return html;
  }


  ngOnInit() {
    // this.resultDisplay();
  }

}
