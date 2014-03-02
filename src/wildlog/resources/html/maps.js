	// Adding Data Points
	var taxiData = [
	new google.maps.LatLng(44.5402999, -78.546312),
	new google.maps.LatLng(44.5403020, -78.546311),
	new google.maps.LatLng(44.541012, -78.546310),
	new google.maps.LatLng(44.5403012, -78.546312),
	new google.maps.LatLng(44.542011, -78.546312),
	new google.maps.LatLng(44.5403013, -78.546310),
	new google.maps.LatLng(44.545021, -78.546310)
	];
	function initialize() {
	// Inititalize map
	var map_options = {
	center: new google.maps.LatLng(44.5403, -78.5463),
	zoom: 13,
	mapTypeId: google.maps.MapTypeId.ROADMAP
	}
	var map = new google.maps.Map(document.getElementById('map-canvas'), map_options)
	var pointArray = new google.maps.MVCArray(taxiData);
	var heatmap = new google.maps.visualization.HeatmapLayer({
	data: pointArray
	});
	heatmap.setMap(map);
	// Add marker 1
	var marker = new google.maps.Marker({
	position: new google.maps.LatLng(44.5403, -78.5463),
	map: map,
	title: 'Hello World!'
	});
	// Setup info popup
	var infowindow = new google.maps.InfoWindow({
	content: 'contentString<br/>cxx'
	});
	google.maps.event.addListener(marker, 'click', function() {
	infowindow.open(map,marker);
	});
	}
	google.maps.event.addDomListener(window, 'load', initialize);