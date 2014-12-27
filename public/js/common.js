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

    $scope.refreshDiv = function refreshDiv() {
        var refreshDiv = $('.refreshDiv');
        refreshDiv.attr('class', 'refreshDiv well');
        refreshDiv.text('Refreshing Data...');
    };

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

    $scope.latlng = function latlng(number) {
        return number.toPrecision(9).toString();
    }
}