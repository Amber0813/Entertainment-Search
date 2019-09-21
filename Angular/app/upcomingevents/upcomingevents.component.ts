import { Component, OnInit } from '@angular/core';
import { DetailService } from '../Service/detail.service';
import { trigger, transition, animate, style, state,stagger,query } from '@angular/animations'

@Component({
  selector: 'app-upcomingevents',
  templateUrl: './upcomingevents.component.html',
  styleUrls: ['./upcomingevents.component.css'],
  animations: [
    trigger('slideInOut', [
      transition(':enter', [
        style({transform: 'translateY(-100%)'}),
        animate('400ms ease-in', style({transform: 'translateY(0%)'}))
      ])
    ]),
    trigger('slideIn', [ //show less
      transition(':enter', [
        style({transform: 'translateY(0%)'}),
        animate('400ms ease-out', style({transform: 'translateY(200%)'}))
      ])
    ]),
    trigger('Fading', [
      state('*', style({ opacity: 0 })),
      state('void', style({ opacity: 1 })),
      transition(':enter', animate('800ms ease-out')),
      transition(':leave', animate('800ms ease-in')),
    ]),

    trigger('parentAnimation', [
      transition('void => *', [
          query('.child', style({opacity: 0})),
          query('.child', stagger('1ms', [
              animate('400ms .1s ease-out', style({opacity: 1}))
          ]))
      ]),
      transition('* => void', [
          query('.child', style({opacity: 1})),
          query('.child', stagger('1ms', [
              animate('400ms .1s ease-out', style({opacity: 0}))
          ])),
      ])
  ])

  ]
})

export class UpcomingeventsComponent implements OnInit {
  slidein = false;
  category = 'Default';
  order = 'Ascending';
  disable = true;
  cats = [
    'Default',
    'Event Name',
    'Time',
    'Artist',
    'Type'
  ];
   orders = [
     'Ascending',
     'Descending'
   ];
  show = false;
  moreishidden = false;
  lessishidden = true;
  fun: any;

  constructor(public detailService: DetailService) { }

  showmore() {
    this.slidein = false;
    this.show = true;
    this.moreishidden = true;
    this.lessishidden = false;
  }

  showless() {
    this.slidein = true;
    this.show = false;
    this.moreishidden = false;
    this.lessishidden = true;
  }

  onchange(str) {
    this.category = str;
    if (str != 'Default') {
      this.disable = false;
    }
    if (str == 'Default') {
      this.disable = true;
      console.log(str)
    }
    this.sort();
  }

  orderchange(order) {
    this.order = order;
    this.sort();
  }

  sort() {
    if (this.category == 'Default') {
      this.detailService.upcomingData.resultsPage.results.event.sort((a,b) => a.start.date <= b.start.date ? -1 : 1);
    }
    else if (this.order == 'Ascending') {
      console.log("this.order");
      if (this.category == 'Event Name') {
        this.detailService.upcomingData.resultsPage.results.event.sort((a,b) => a.displayName <= b.displayName ? -1 : 1);
      }
      if (this.category == 'Time') {
        this.detailService.upcomingData.resultsPage.results.event.sort((a,b) => a.start.date <= b.start.date ? -1 : 1);
      }
      if (this.category == 'Artist') {
        this.detailService.upcomingData.resultsPage.results.event.sort((a,b) => a.performance[0].artist.displayName <= b.performance[0].artist.displayName ? -1 : 1);
      }
      if (this.category =='Type') {
        this.detailService.upcomingData.resultsPage.results.event.sort((a,b) => a.type <= b.type ? -1 : 1);
      }
    }
    else if (this.order == 'Descending') {
      console.log("this.order");
      if (this.category == 'Event Name') {
        this.detailService.upcomingData.resultsPage.results.event.sort((a,b) => a.displayName <= b.displayName ? 1 : -1);
      }
      if (this.category == 'Time') {
        this.detailService.upcomingData.resultsPage.results.event.sort((a,b) => a.start.date <= b.start.date ? 1 : -1);
      }
      if (this.category == 'Artist') {
        this.detailService.upcomingData.resultsPage.results.event.sort((a,b) => a.performance[0].artist.displayName <= b.performance[0].artist.displayName ? 1 : -1);
      }
      if (this.category =='Type') {
        this.detailService.upcomingData.resultsPage.results.event.sort((a,b) => a.type <= b.type ? 1 : -1);
      }
    }
  }
  sortevent(order) {

  }

  sorttime(order) {

  }

  sortartist(order) {

  }

  sorttype(order) {

  }

  ngOnInit() {
  }

}
