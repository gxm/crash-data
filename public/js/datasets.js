function DatasetsController($scope, $http, $location) {

    'use strict';

    new Common($scope, $http, $location);

    $scope.datasets = [];

    $scope.loadData = function loadData() {
        $http.jsonp($scope.host + 'datasets')
            .success(function (data, status, headers) {
                $scope.datasets = data;
            }).error(function (data, status, headers) {
                console.log('error', data, status);
            });
        $location.search($scope.settings);
    };

    $scope.updateData = function updateData() {
        $http.put($scope.host + 'datasets', $scope.datasets)
            .success(function (data, status, headers) {
                console.log('got back', data, status);
                //todo - gfm -
                // $scope.datasets = data;
            }).error(function (data, status, headers) {
                console.log('error', data, status);
            });
        $location.search($scope.settings);
    };

    $scope.loadData();
}
