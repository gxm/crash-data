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
        var legendCanvas = document.createElement('canvas');
        legendCanvas.width = 100;
        legendCanvas.height = 10;
        var min = document.querySelector('#min');
        var max = document.querySelector('#max');
        var gradientImg = document.querySelector('#gradient');
        var legendCtx = legendCanvas.getContext('2d');
        var gradientCfg = {};

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
            "useLocalExtrema": true,
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
        var baseLayer = getLayer('base', 0, 0.6);
        var activeLayer = getLayer('baseActive', 50, 0.5);
        var annoLayer = getLayer('baseAnno', 91);

        $scope.heatMapOverlay = new HeatmapOverlay($scope.config());

        $scope.map = new L.map('heatmapArea', {
            center: new L.LatLng(loadlat, loadlng),
            zoom: $scope.settings.zoom,
            zoomControl: false,
            layers: [baseLayer, activeLayer, annoLayer, $scope.heatMapOverlay]
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
