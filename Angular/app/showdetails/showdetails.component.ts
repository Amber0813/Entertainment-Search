import { Inject, Component, OnInit } from '@angular/core';
import { DetailService } from '../Service/detail.service';
import { SearchService } from '../Service/search.service';
import { LocalstoreService } from '../Service/localstore.service';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatDialogConfig} from '@angular/material';
import { AngularWaitBarrier } from 'blocking-proxy/built/lib/angular_wait_barrier';
import { trigger, transition, animate, style } from '@angular/animations'

export interface DialogData {
  url: string;
}

@Component({
  selector: 'app-showdetails',
  templateUrl: './showdetails.component.html',
  styleUrls: ['./showdetails.component.css'],
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
export class ShowdetailsComponent implements OnInit {
  storehashmap = (JSON.parse(localStorage.getItem('local_favorite')) == null ? {}: JSON.parse(localStorage.getItem('local_favorite')));
  link = "active";

  constructor(public detailService: DetailService, public searchService: SearchService, public lstoreService: LocalstoreService, public dialog: MatDialog) { }

  artist() {
    var html = '';
    if (this.detailService.detailData._embedded.attractions) {
      for (var attraction of this.detailService.detailData._embedded.attractions) {
        html += attraction.name + ' | ';
      }
      if (html.charAt(html.length-2) == '|') {
        html = html.substring(0, html.length-3);
      }
    }
    return html;
  }

  category() {
    var html = '';
    if (this.detailService.detailData.classifications) {
      var classification = this.detailService.detailData.classifications[0];
      if (classification.segment.name && classification.segment.name != 'Undefined') {
        html += classification.segment.name + ' | ';
      }
      if (classification.genre.name && classification.genre.name != 'Undefined') {
        html += classification.genre.name + ' | ';
      }
      if (classification.subGenre.name && classification.subGenre.name != 'Undefined') {
        html += classification.subGenre.name + ' | ';
      }
      if (classification.type && classification.type.name != 'Undefined') {
        html += classification.type.name  + ' | ';
      }
      if (classification.subType && classification.subType.name != 'Undefined') {
        html += classification.subType.name;
      }
      if (html.charAt(1) == '|') {
        html = html.substring(3, html.length);
      }
      if (html.charAt(html.length-2) == '|') {
        html = html.substring(0, html.length-3);
      }
    }
    return html;
  }

  pricerange() {
    var html = '';
    if (this.detailService.detailData.priceRanges) {
      html += '$' + this.detailService.detailData.priceRanges[0].min + ' - ' + '$' + this.detailService.detailData.priceRanges[0].max;
    }
    return html;
  }

  event() {
    this.detailService.event = true;
    this.detailService.artist = false;
    this.detailService.venue = false;
    this.detailService.upcomingevent = false;
  }

  artistteam() {
    this.detailService.event = false;
    this.detailService.artist = true;
    this.detailService.venue = false;
    this.detailService.upcomingevent = false;
    this.detailService.getArtist();
  }

  venue() {
    this.detailService.event = false;
    this.detailService.artist = false;
    this.detailService.venue = true;
    this.detailService.upcomingevent = false;
    this.detailService.getVenue();
  }

  upcomingevents() {
    this.detailService.event = false;
    this.detailService.artist = false;
    this.detailService.venue = false;
    this.detailService.upcomingevent = true;
    this.detailService.getUpcomingEvents();
  }

  back() {
    this.detailService.details = false;
    this.searchService.showresult = true;
    this.searchService.slidein = false;
    // this.detailService.event = false;
  }

  storeitem() {
    var item = this.searchService.jsonData._embedded.events[this.detailService.id];
    this.lstoreService.store(item);
    if (this.searchService.storehashmap[item.id]){
      this.lstoreService.delete(item.id);
      this.searchService.storehashmap = this.lstoreService.getStorekey();
      // this.storagearray = Object.values(this.storehashmap);
    }
    else {
      this.lstoreService.store(item);
      this.searchService.storehashmap = this.lstoreService.getStorekey();
      // this.storagearray = Object.values(this.storehashmap);
    }
  }

  openDialog(): void {
    var mapurl = (this.detailService.detailData.seatmap ? this.detailService.detailData.seatmap.staticUrl : '');
    const dialogRef = this.dialog.open(Showseatmap, {
      width: '350px',
      data: {url: mapurl}
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      // this.animal = result;
    });
  }

  ngOnInit() {
  }

}

@Component({
  selector: 'showseatmap',
  templateUrl: 'showseatmap.html',
  styleUrls: ['./showseatmap.css']
})
export class Showseatmap {
  isdisplay = false;
  constructor(
    public dialogRef: MatDialogRef<Showseatmap>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) {}

    poplarge() {
      this.isdisplay = true;
    }
    hide() {
      this.isdisplay = false;
    }
  // onNoClick(): void {
  //   this.dialogRef.close();
  // }

}
