function Common($scope, $http, $location) {
    'use strict';

    //todo - gfm - this should be in configuration
    $scope.hosts = ['http://104.237.130.146:8080/',
        'http://localhost:8080/'];

    $scope.host = $scope.hosts[0];

    var host = $location.host();
    if (host === undefined || host === '' || host === 'localhost') {
        $scope.host = $scope.hosts[1];
    }

    $scope.infoDivRefresh = function infoDivRefresh() {
        var infoDiv = $('.infoDiv');
        infoDiv.attr('class', 'infoDiv well');
        infoDiv.text('Refreshing Data...');
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

/*    $scope.options = function options(lat, lng, controls, zoom) {
        return {
            zoom: zoom,
            //center: new google.maps.LatLng(lat, lng),
            //mapTypeId: google.maps.MapTypeId.ROADMAP,
            disableDefaultUI: false,
            scrollwheel: true,
            draggable: true,
            navigationControl: true,
            mapTypeControl: false,
            scaleControl: false,
            disableDoubleClickZoom: false,
            streetViewControl: false,
            zoomControl: controls,
            //zoomControlOptions: {style: google.maps.ZoomControlStyle.SMALL},
            panControl: false
        }
    };*/

    $scope.percents = function percents(number, sign) {
        sign = sign || false;
        if (isNaN(number)) {
            return '-';
        }
        number = number == Number.POSITIVE_INFINITY ? .99 : number;
        number = number * 100;
        if (Math.abs(number) >= 10) {
            number = Math.round(number);
        } else if (Math.abs(number) >= 1) {
            number = number.toPrecision(2);
        } else {
            number = number.toPrecision(1);
        }
        var plusSign = number > 0 && sign ? '+' : '';
        return plusSign + number + '%';
    };

    $scope.createUrl = function createUrl(corners) {
        return $scope.host + 'metro/' + corners.north +
            '/' + corners.south + '/' + corners.east + '/' + corners.west;
    }

    $scope.latlng = function latlng(number) {
        return number.toPrecision(9).toString();
    }
}