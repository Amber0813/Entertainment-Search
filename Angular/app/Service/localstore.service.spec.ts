import { TestBed } from '@angular/core/testing';

import { LocalstoreService } from './localstore.service';

describe('LocalstoreService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: LocalstoreService = TestBed.get(LocalstoreService);
    expect(service).toBeTruthy();
  });
});
