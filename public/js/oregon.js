function CrashController($scope, $http, $location) {

	'use strict';
	
	new Common($scope, $http, $location);

	$scope.search = function search(name, defaultValue) {
		var value = $location.search()[name];
		if (value === undefined) {
			return defaultValue;
		}
		if (value === 'true') {
			return true;
		} else if (value === 'false') {
			return false;
		}
		return value;
	};

	$scope.total = 0;

	$scope.settings = new CrashSettings($scope);
	$scope.summary = {};

	$scope.loadData = function loadData() {
		$scope.infoDivRefresh();
		var center = $scope.googleMap.getCenter();

		var config = { params: $scope.settings };
		var corners = $scope.settings.corners($scope);

		var url = $scope.createUrl(corners) + '?callback=JSON_CALLBACK';
		config.params.zoom = $scope.googleMap.getZoom();
		config.params.lat = $scope.latlng(center.lat());
		config.params.lng = $scope.latlng(center.lng());
		$http.jsonp(url, config)
			.success(function (data, status, headers) {
				$scope.total = data.total;
				$scope.heatMapOverlay.setDataSet(data);
				$scope.heatMapOverlay.draw();

				for (var prop in data.summary){
					$scope.summary[prop] = $scope.percents(data.summary[prop] / $scope.total);

				}
				$('.infoDiv').attr('class', 'infoDiv well hidden');
				var downloadUrl = $scope.createUrl(corners) + '?';
				for (var prop in config.params) {
					if (prop !== 'corners') {
						downloadUrl += prop + '=' + config.params[prop] + '&';
					}
				}
				downloadUrl += 'download=true';
				$('#download').attr('href', downloadUrl);
				$('#download').attr('download', 'odot_' + corners.north + '_' + corners.south
					+ '_' + corners.east + '_' + corners.west + '.csv');

			}).error(function (data, status, headers) {
				$('.infoDiv').text('Unable to load data from remote server');

			});

		$location.search($scope.settings);

	};

	function windowOnLoad() {

		var loadlat = Number($scope.search('lat', 45.52));
		var loadlng = Number($scope.search('lng', -122.67));

		var myOptions = $scope.options(loadlat, loadlng, true, $scope.settings.zoom);
		myOptions.zoomControlOptions.position = google.maps.ControlPosition.RIGHT_TOP;
		$scope.googleMap = new google.maps.Map(document.getElementById('heatmapArea'), myOptions);
		$scope.googleMap.controls[google.maps.ControlPosition.TOP_LEFT].push($('#sideSettingsDiv')[0]);
		$scope.googleMap.controls[google.maps.ControlPosition.BOTTOM_CENTER].push($('#infoDiv')[0]);

		var config = $scope.config(document.getElementById('heatmapArea'), 'Crash Count');

		$scope.heatMapOverlay = new HeatmapOverlay($scope.googleMap, config);

		google.maps.event.addListener($scope.googleMap, 'idle', function () {
			$scope.loadData();
		});

		$('#changeSettings').show();
		$('#summary').show();
		$('#settingsTable').show();
		$('#settingsTable').collapse('show');
		$('#summaryTable').show();
		$('#summaryTable').collapse('show');

	}

	windowOnLoad();

}
