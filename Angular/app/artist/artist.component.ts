import { Component, OnInit } from '@angular/core';
import { DetailService } from '../Service/detail.service';

@Component({
  selector: 'app-artist',
  templateUrl: './artist.component.html',
  styleUrls: ['./artist.component.css']
})
export class ArtistComponent implements OnInit {
  number = 10; 

  constructor(public detailService: DetailService) { }

  ngOnInit() {
  }

}
