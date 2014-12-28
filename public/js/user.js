function UserController($scope, $http, $location) {

    'use strict';

    new Common($scope, $http, $location);

    $scope.data = [{name:'Crash Map', href:'crashes.html'}];

    $scope.loadData = function loadData() {
        $http.jsonp($scope.host + 'links?callback=JSON_CALLBACK')
            .success(function (data, status, headers) {
                console.log('links', data);
                $scope.data = data;
            }).error(function (data, status, headers) {
                console.log('error', status);
            });
    };

    $scope.loadData();

}
