var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
var cors = require('cors');
var request = require('request');
var geohash = require('ngeohash');
var SpotifyWebApi = require('spotify-web-api-node');
var async = require('async');

var indexRouter = require('./routes/index');
var usersRouter = require('./routes/users');

var app = express();
app.use(cors());
var scopes = ['user-read-private', 'user-read-email'],
  redirectUri = 'https://example.com/callback',
  clientId = '5fe01282e44241328a84e7c5cc169165',
  state = 'some-state-of-my-choice';
var spotifyApi = new SpotifyWebApi({
  clientId: 'ba351dc0d99844fa9578c3d81081db66',
  clientSecret: '97494c8e2fa044a8955502f2e35a63aa',
  redirectUri: 'http://localhost:3000/callback'
});
var authorizeURL = spotifyApi.createAuthorizeURL(scopes, state);
console.log(authorizeURL);
var token = '';

app.get('/upcomingevents', function(req, res) {
	var ven = req.query.venue;
	var venueurl = 'https://api.songkick.com/api/3.0/search/venues.json?query='+ven+'&apikey=VCPlzGI58EL9xZ1B';
	request(venueurl, function (error, response, body) {
	    if (!error && response.statusCode == 200) {
	    	body = JSON.parse(body);
	    	if (body.resultsPage.results.venue && body.resultsPage.results.venue[0].id) {
    	        var venid = body.resultsPage.results.venue[0].id;
    	        var upeveurl = 'https://api.songkick.com/api/3.0/venues/' + venid + '/calendar.json?apikey=VCPlzGI58EL9xZ1B';
    	        request(upeveurl, function (error, response, body) {
    			    if (!error && response.statusCode == 200) {
    			        json = body;
    			        // console.log(json);
    			        res.setHeader('Content-Type', 'application/json');
    			   		res.end(json);
    			    }
    			    else {
    			    	var error = {
	    	    			error: "true"
		    	    	}
		    	    	console.log(err);
		    	    	res.setHeader('Content-Type', 'application/json');
				   		res.end(JSON.stringify(error));
	    			    }
    			});
    	    }
    	    else {
    	    	var error = {
    	    		error: "true"
    	    	}
    	    	console.log(error);

    	    	res.setHeader('Content-Type', 'application/json');
		   		res.end(JSON.stringify(error));
    	    }
	    }
	    else {
	    	var error = {
	    		error: "true"
	    	}
	    	console.log(err);
	    	res.setHeader('Content-Type', 'application/json');
			   		res.end(JSON.stringify(error));
	    }
	});
});
app.get('/venue', function(req, res) {
	var ven = req.query.venue;
	var venueurl = 'https://app.ticketmaster.com/discovery/v2/venues?apikey=2E0k5OleWAH3kJx3fDWFF5Bjz8FKILGY&keyword=' + ven;
	request(venueurl, function (error, response, body) {
	    if (!error && response.statusCode == 200) {
	        var json = body;
	        console.log(json);
	        res.setHeader('Content-Type', 'application/json');
	   		res.end(json);
	    }
	});
});

app.get('/artist', function(req, res) {
	var reqjson = JSON.parse(req.query.artist);
	var result = {};
	// console.log(reqjson);
	// console.log(req.query.type);
	console.log("/artist");
	async.map(reqjson.attractions, function(name, callback) {
		var picurl = 'https://www.googleapis.com/customsearch/v1?cx=000811504171661107335:8dsyppoatj0&q='+name.name+'&imgSize=huge&num=8&searchType=image&key=AIzaSyBQI5bd278zKn3RQuGBRuWkxSFAkThGkwo';
		console.log(picurl)
		request(picurl, function (error, response, body) {
		    if (!error && response.statusCode == 200) {
		    	console.log("google")
		    	// console.log(body);
		    	body = JSON.parse(body);
		    	if (req.query.type == 'Music') {
		    		spotifyApi.searchArtists(name.name)
					.then(function(data) {
						//spotify search success
						console.log("spotify")
						console.log(data);
						if (data.body.artists.items) {
							if (data.body.artists.items.length == 0) {
								console.log("no items");
								var item = {
									name: name.name,
									images: []
								}
								if (body.items) {
									for (i of body.items) {
										// console.log(body.items[0].link);
										item.images.push(i.link);
									}
								}
								return callback(null, item);
							}
							var count = 0;
							for (item of data.body.artists.items) {
								if (item.name.toLowerCase() == name.name.toLowerCase()) {
									count++;
									item.images = [];
									if (body.items) {
										for (i of body.items) {
											// console.log(body.items[0].link);
											item.images.push(i.link);
										}
									}
									return callback(null, item);
								}
							}
							if (count == 0) {
								var item = {
									name: name.name,
									images: []
								}
								if (body.items) {
									for (i of body.items) {
										// console.log(body.items[0].link);
										item.images.push(i.link);
									}
								}
								return callback(null, item);
							}
						}
					}, function(err) {
						console.log(err);
						spotifyApi.clientCredentialsGrant().then(
							function(data) {
								// console.log('The access token expires in ' + data.body['expires_in']);
								// console.log('The access token is ' + data.body['access_token']);
								// console.log(data);
								token = data.body['access_token'];
								// Save the access token so that it's used in future calls
								spotifyApi.setAccessToken(data.body['access_token']);
								spotifyApi.searchArtists(name.name)
								.then(function(data) {
									console.log(data);
									if (data.body.artists.items) {
										if (data.body.artists.items.length == 0) {
											console.log("no items");
											var item = {
												name: name.name,
												images: []
											}
											if (body.items) {
												for (i of body.items) {
													// console.log(body.items[0].link);
													item.images.push(i.link);
												}
											}
											return callback(null, item);
										}
										var count = 0;
										for (item of data.body.artists.items) {
											if (item.name.toLowerCase() == name.name.toLowerCase()) {
												item.images = [];
												if (body.items) {
													for (i of body.items) {
														// console.log(body.items[0].link);
														item.images.push(i.link);
													}
												}
												return callback(null, item);
											}
										}
										if (count == 0) {
											var item = {
												name: name.name,
												images: []
											}
											if (body.items) {
												for (i of body.items) {
													// console.log(body.items[0].link);
													item.images.push(i.link);
												}
											}
											return callback(null, item);
										}
									}
								}, function(err) {
									console.log(err);
									spotifyApi.clientCredentialsGrant().then(
										function(data) {
											// console.log('The access token expires in ' + data.body['expires_in']);
											// console.log('The access token is ' + data.body['access_token']);
											// console.log(data);
											token = data.body['access_token'];
											// Save the access token so that it's used in future calls
											spotifyApi.setAccessToken(data.body['access_token']);
											
										},
										function(err) {
											// console.log('Something went wrong when retrieving an access token', err);
										}
									);
								});
							},
							function(err) {
								// console.log('Something went wrong when retrieving an access token', err);
							}
						);
					});
		    	}
		    	else {
		    		item = {
		    			name: name.name,
		    			images: []
		    		}
					if (body.items) {
						for (i of body.items) {
							// console.log(body.items[0].link);
							item.images.push(i.link);
						}
					}
					return callback(null, item);
		    	}
		    }
		});
	}, function(err, results) {
		var rjson = {
			artists:[]
		};
		rjson.artists = results;
		for (var artist of rjson.artists) {
			if (artist.followers) {
				fo = '';
		        var follower = artist.followers.total;
		        // console.log(follower);
		        while (follower != 0) {
		            var temp = 0;
		            if (parseInt(follower / 1000) != 0) {
		                temp = follower % 1000;
		                if (temp < 10) {
		                	fo = ',' + '00' + temp.toString() + fo;
		              	}
		              	else if (temp > 10 && temp < 100) {
		                	fo = ',' + '0' + temp.toString() + fo;
		              	}
		              	else {
		                	fo = ',' + temp.toString() + fo;
		              	}
		              	follower = parseInt(follower / 1000);
		            }
		            else {
		            	temp = follower % 1000;
		              	fo = ',' + temp.toString() + fo;
		        		follower = parseInt(follower / 1000);
		            }
		        }
		        fo = fo.substring(1,fo.length);
		        artist.followers = fo;
			}
		}
		console.log(rjson);
		res.setHeader('Content-Type', 'application/json');
		res.end(JSON.stringify(rjson));
	});

});

app.get('/a', function (req, res) { //event search result
	console.log(req.query);
	console.log(req.query.unit);
	var geo = JSON.parse(req.query.geo);
	var geocode = geohash.encode(geo.lat, geo.lng);
	var location = req.query.location;
	var segmentId = "";
	if (req.query.category != "All")
		segmentId = req.query.category;
	if (req.query.category == "Music")
		segmentId = "KZFzniwnSyZfZ7v7nJ";
	else if (req.query.category == "Sports")
		segmentId = "KZFzniwnSyZfZ7v7nE";
	else if (req.query.category == "Arts & Theatre")
		segmentId = "KZFzniwnSyZfZ7v7na";
	else if (req.query.category == "Film")
		segmentId = "KZFzniwnSyZfZ7v7nn";
	else if (req.query.category == "Miscellaneous")
		segmentId = "KZFzniwnSyZfZ7v7n1";
	if (req.query.category == "null")
		segmentId = '';
	var distance = 10;
	if (req.query.distance != "")
		distance = req.query.distance;
	var input = "";
	if (req.query.location =="locinput") {
		var locin = encodeURIComponent(req.query.linput);
		var locurl = "https://maps.googleapis.com/maps/api/geocode/json?address="+locin+"&key=AIzaSyBQI5bd278zKn3RQuGBRuWkxSFAkThGkwo";
		request(locurl, function (error, response, body) {
	    if (!error && response.statusCode == 200) {
	    	console.log(body);
	    	var json = JSON.parse(body);
	    	console.log(json);
	        var geoloc = json.results[0].geometry.location;
	        console.log(geoloc);
	        var geoloccode = geohash.encode(geoloc.lat, geoloc.lng);
	        console.log(req.query.unit);
	        var unit = 'miles';
	        if (req.query.unit == "Miles")
	        	unit = "miles";
	        if (req.query.unit == "Kilometers")
	        	unit = "km";
	        var keyword = encodeURI(req.query.keyword);
	        var eventurl = "https://app.ticketmaster.com/discovery/v2/events.json?segmentId="+segmentId+"&radius="+distance+"&unit="+unit+"&geoPoint="+geoloccode+"&keyword="+keyword+"&apikey=2E0k5OleWAH3kJx3fDWFF5Bjz8FKILGY";
	        console.log(eventurl);
	    	request(eventurl, function (error, response, body) {
			    if (!error && response.statusCode == 200) {
			        var jsonb = body;
			    	res.setHeader('Content-Type', 'application/json');
			   		res.end(jsonb);
			    }
			});
	    }
	});
	}
	else {
		var unit = 'miles';
        if (req.query.unit == "Miles")
        	unit = "miles";
        if (req.query.unit == "Kilometers")
        	unit = "km";
        var keyword = encodeURI(req.query.keyword);
        var eventurl = "https://app.ticketmaster.com/discovery/v2/events.json?segmentId="+segmentId+"&radius="+distance+"&unit="+unit+"&geoPoint="+geocode+"&keyword="+keyword+"&apikey=2E0k5OleWAH3kJx3fDWFF5Bjz8FKILGY";
        console.log(eventurl);
        request(eventurl, function (error, response, body) {
		    if (!error && response.statusCode == 200) {
		        var jsonb = body;
		        // console.log(body);
		    	res.setHeader('Content-Type', 'application/json');
		   		res.end(jsonb);
		    }
		    else {
		    	console.log("error");
		    	var item = {
		    		error: "true"
		    	}
		    	res.setHeader('Content-Type', 'application/json');
		   		res.end(JSON.stringify(item));
		    }
		});
	}
}); 

app.get('/details', function (req, res) {
	console.log(req.query);
	url = "https://app.ticketmaster.com/discovery/v2/events/"+req.query.eventid+"?apikey=2E0k5OleWAH3kJx3fDWFF5Bjz8FKILGY";
	console.log(url);
	var json = null;
	request(url, function (error, response, body) {
	    if (!error && response.statusCode == 200) {
	        json = body;
	        // console.log(json);
	        res.setHeader('Content-Type', 'application/json');
	   		res.end(json);
	    }
	});
});

app.get('/autocomplete', function (req, res) { //autocomplete request
	console.log(req.query);
	var url = "https://app.ticketmaster.com/discovery/v2/suggest?apikey=2E0k5OleWAH3kJx3fDWFF5Bjz8FKILGY&keyword="+req.query.keyword;
	var json = null;
	console.log(url);
	request(url, function (error, response, body) {
	    if (!error && response.statusCode == 200) {
	        json = body;
	        // console.log(json);
	        res.setHeader('Content-Type', 'application/json');
	   		res.end(json);
	    }
	});
   
}); 

app.listen(4000, function () { 
	console.log('Hello World is listening at port 4000'); 
});

module.exports = app;
