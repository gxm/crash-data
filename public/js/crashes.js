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
            }).error(function (data, status, headers) {
                $('.refreshDiv').text('Unable to load data from remote server');

            });

        $location.search($scope.settings);

    };

    $scope.config = function config() {
        var legendCanvas = document.createElement('canvas');
        legendCanvas.width = 100;
        legendCanvas.height = 10;
        var min = document.querySelector('#min');
        var max = document.querySelector('#max');
        var gradientImg = document.querySelector('#gradient');
        var legendCtx = legendCanvas.getContext('2d');
        var gradientCfg = {};
        return {
            "radius": 35,
            "maxOpacity": 1,
            "useLocalExtrema": false,
            latField: 'lat',
            lngField: 'lng',
            valueField: 'count',
            onExtremaChange: function updateLegend(data) {
                min.innerHTML = data.min;
                max.innerHTML = data.max;
                if (data.gradient != gradientCfg) {
                    gradientCfg = data.gradient;
                    var gradient = legendCtx.createLinearGradient(0, 0, 100, 1);
                    for (var key in gradientCfg) {
                        gradient.addColorStop(key, gradientCfg[key]);
                    }
                    legendCtx.fillStyle = gradient;
                    legendCtx.fillRect(0, 0, 100, 10);
                    gradientImg.src = legendCanvas.toDataURL();
                }
            }
        };
    };

    function getLayer(type, index, opacity) {
        opacity = opacity || 1;
        var AGSURL = 'https://{s}.oregonmetro.gov/ArcGIS/rest/services/';
        var subDomains = ['gistiles1','gistiles2', 'gistiles3', 'gistiles4'];
        //todo - gfm - figure out proper token generation
        var token = '7eXr3OCSVOChwnKR1_--HMhwvvtw9hRn9ymyFYCT4O-0-mQQpRre88v7nkfVNK16';
        return L.tileLayer( AGSURL + 'metromap/' + type + '/MapServer/tile/{z}/{y}/{x}?token=' + token, {
            maxZoom: 19,
            attribution: 'Tiles: &copy; Metro RLIS',
            opacity: opacity,
            zIndex: index,
            subdomains : subDomains
        });
    }

    function windowOnLoad() {
        var loadlat = Number($scope.search('lat', 45.52));
        var loadlng = Number($scope.search('lng', -122.67));
        var layers = [];
        if ($scope.settings.tiles === 'open') {
            layers.push(L.tileLayer('https://{s}.tiles.mapbox.com/v3/{id}/{z}/{x}/{y}.png', {
                maxZoom: 18,
                attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, ' +
                '<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
                'Imagery © <a href="http://mapbox.com">Mapbox</a>',
                id: 'examples.map-i875mjb7'
            }));

        } else {
            layers.push(getLayer('base', 0, 0.6));
            layers.push(getLayer('baseActive', 50, 0.5));
            layers.push(getLayer('baseAnno', 91));
        }

        $scope.heatMapOverlay = new HeatmapOverlay($scope.config());
        layers.push($scope.heatMapOverlay);

        $scope.map = new L.map('heatmapArea', {
            center: new L.LatLng(loadlat, loadlng),
            zoom: $scope.settings.zoom,
            zoomControl: false,
            layers: layers
        });

        if ($scope.settings.sinks) {
            var markers = [];
            sinkPoints.forEach(function (point) {
                var marker = L.marker([point.lat, point.lng]);
                var source = point.source || 'odot';
                marker.bindPopup(point.lat + ', ' + point.lng + ' source:' + source);
                markers.push(marker);
                var circle = L.circle([point.lat, point.lng], 13, {
                    color: 'red',
                    weight: 2,
                    fillOpacity: 0
                });
                markers.push(circle);

            });

            L.layerGroup(markers).addTo($scope.map);
        }

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
