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

    function addSinks() {
        $scope.sinks = L.layerGroup();

        $http.get($scope.host + 'sinks')
            .success(function (data, status, headers) {
                data.forEach(function (point) {
                    var marker = L.marker([point.lat, point.lng]);
                    var source = point.source || 'odot';
                    marker.bindPopup(point.lat + ', ' + point.lng + ' source:' + source);
                    var circle = L.circle([point.lat, point.lng], 13, {
                        color: 'red',
                        weight: 2,
                        fillOpacity: 0
                    });
                    $scope.sinks.addLayer(marker);
                    $scope.sinks.addLayer(circle);
                });

            }).error(function (data, status, headers) {
                console.log('unable to load sinks', status);
            });
    }

    var subDomains = ['gistiles1', 'gistiles2', 'gistiles3', 'gistiles4'];
    var token = 'token=O95k2qhvXt3RArC0yQV5j4JinWb21wL0wLj2Ik-dLG0.';
    var attribution = "<a href='//gis.oregonmetro.gov'>Metro RLIS</a>";
    var road = L.tileLayer('http://{s}.oregonmetro.gov/arcgis/rest/services/metromap/baseAll/MapServer/tile/{z}/{y}/{x}?' + token, {
        subdomains: subDomains,
        attribution: attribution
    });
    var photo = L.tileLayer('http://{s}.oregonmetro.gov/arcgis/rest/services/photo/2013aerialphoto/MapServer/tile/{z}/{y}/{x}?' + token, {
        subdomains: subDomains,
        zIndex: 10,
        attribution: attribution
    });
    var xtraphoto = L.tileLayer('//server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {
        attribution: "<a href='//www.esri.com'>ESRI</a>",
        zIndex: 0
    });
    var label = L.tileLayer('http://{s}.oregonmetro.gov/arcgis/rest/services/metromap/baseAnno/MapServer/tile/{z}/{y}/{x}?' + token, {
        subdomains: subDomains,
        zIndex: 100,
        attribution: attribution
    });

    function windowOnLoad() {
        var loadlat = Number($scope.search('lat', 45.52));
        var loadlng = Number($scope.search('lng', -122.67));
        var layers = [];
        layers.push(road);
        $scope.heatMapOverlay = new HeatmapOverlay($scope.config());
        layers.push($scope.heatMapOverlay);

        $scope.map = new L.map('heatmapArea', {
            center: new L.LatLng(loadlat, loadlng),
            zoom: $scope.settings.zoom,
            zoomControl: false,
            layers: layers
        });

        addSinks();

        var photoGroup = L.layerGroup([photo, xtraphoto, label]);

        var overlayMaps = {
            "sinks": $scope.sinks
        };

        var baseMaps = {
            "road ": road,
            "air photo": photoGroup
        };


        $scope.map.addControl( L.control.zoom({position: 'topright'}) );

        L.control.layers(baseMaps, overlayMaps).addTo($scope.map);

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
