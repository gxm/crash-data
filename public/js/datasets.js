function DatasetsController($scope, $http, $location) {

    'use strict';

    new Common($scope, $http, $location);

    $scope.datasets = [];

    $scope.loadData = function loadData() {
        $http.get($scope.host + 'datasets')
            .success(function (data, status, headers) {
                $scope.datasets = data;
            }).error(function (data, status, headers) {
                console.log('error', status);
            });
        $location.search($scope.settings);
    };

    $scope.updateData = function updateData(index) {
        $http.put($scope.host + 'datasets', $scope.datasets[index])
            .success(function (data, status, headers) {
                $scope.datasets = data;
            }).error(function (data, status, headers) {
                console.log('error', data, status);
            });
        $location.search($scope.settings);
    };

    $scope.loadData();
}
