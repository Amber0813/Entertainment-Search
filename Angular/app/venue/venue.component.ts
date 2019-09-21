import { Component, OnInit } from '@angular/core';
import { DetailService } from '../Service/detail.service';

@Component({
  selector: 'app-venue',
  templateUrl: './venue.component.html',
  styleUrls: ['./venue.component.css']
})
export class VenueComponent implements OnInit {
  lat: number = 51.678418;
  lng: number = 7.809007;
  
  constructor(public detailService: DetailService) { }

  ngOnInit() {
  }

}
