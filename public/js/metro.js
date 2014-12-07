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
        $scope.refreshDiv();
        var config = {params: $scope.settings};
        //todo - gfm - there might be a bug here on resize
        var corners = $scope.settings.corners($scope);

        var url = $scope.createUrl(corners) + '?callback=JSON_CALLBACK';
        config.params.zoom = $scope.map.getZoom();
        var center = $scope.map.getCenter();
        config.params.lat = $scope.latlng(center.lat);
        config.params.lng = $scope.latlng(center.lng);
        $http.jsonp(url, config)
            .success(function (data, status, headers) {
                $scope.total = data.total;
                $scope.heatMapOverlay.setData(data);

                for (var prop in data.summary) {
                    $scope.summary[prop] = $scope.percents(data.summary[prop] / $scope.total);

                }
                $('.refreshDiv').attr('class', 'refreshDiv well hidden');
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
                $('.refreshDiv').text('Unable to load data from remote server');

            });

        $location.search($scope.settings);

    };

    $scope.config = function config() {
        return {
            // radius should be small ONLY if scaleRadius is true (or small radius is intended)
            // if scaleRadius is false it will be the constant radius used in pixels
            "radius": 35,
            "maxOpacity": 1,
            // scales the radius based on map zoom
            //"scaleRadius": true,
            // if set to false the heatmap uses the global maximum for colorization
            // if activated: uses the data maximum within the current map boundaries
            //   (there will always be a red spot with useLocalExtremas true)
            //"useLocalExtrema": true,
            latField: 'lat',
            lngField: 'lng',
            valueField: 'count'
        };
        /**
         *  legend: {
                position: 'br',
                title: title
            }
         */
    };

    function windowOnLoad() {

        var loadlat = Number($scope.search('lat', 45.52));
        var loadlng = Number($scope.search('lng', -122.67));

        var baseLayer = L.tileLayer('https://{s}.tiles.mapbox.com/v3/{id}/{z}/{x}/{y}.png', {
            maxZoom: 18,
            attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, ' +
            '<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
            'Imagery © <a href="http://mapbox.com">Mapbox</a>',
            id: 'examples.map-i875mjb7'
        });

        $scope.heatMapOverlay = new HeatmapOverlay($scope.config());

        $scope.map = new L.map('heatmapArea', {
            center: new L.LatLng(loadlat, loadlng),
            zoom: $scope.settings.zoom,
            zoomControl: false,
            layers: [baseLayer, $scope.heatMapOverlay]
        });

        $scope.map.addControl( L.control.zoom({position: 'topright'}) );

        $scope.map.on('moveend', function (e) {
            $scope.loadData();
        });

        $('#changeSettings').show();
        $('#summary').show();
        $('#settingsTable').show();
        $('#settingsTable').collapse('show');
        $('#summaryTable').show();
        $('#summaryTable').collapse('show');
        $scope.loadData();
    }

    windowOnLoad();

}
