function DatasetsController($scope, $http, $location, $cookieStore) {

    'use strict';

    new Common($scope, $http, $location);

    $scope.datasets = [];
    $scope.message = 'loading...';

    $scope.loadData = function loadData() {
        $http.get($scope.host + 'datasets')
            .success(function (data, status, headers) {
                $scope.datasets = data;
                $scope.message = 'loaded ' + new Date();
            }).error(function (data, status, headers) {
                console.log('error', status);
            });
        $location.search($scope.settings);
    };

    function getConfig() {
        var user = $cookieStore.get('user');
        var config = {};
        if (user && user.type === 'admin') {
            config['headers'] = {key: user.key};
        }
        return config;
    }

    $scope.updateData = function updateData(index) {
        $http.put($scope.host + 'datasets', $scope.datasets[index], getConfig() )
            .success(function (data, status, headers) {
                $scope.datasets = data;
                $scope.message = 'updated ' + new Date();
            }).error(function (data, status, headers) {
                console.log('error', data, status);
                $scope.message = 'error ' + status + ' ' + new Date();
            });
        $location.search($scope.settings);
    };

    $scope.deleteDataset = function deleteDataset(index) {
        var url = $scope.host + 'datasets/' + $scope.datasets[index].name;
        console.log('calling', url);
        $http.delete(url, config)
            .success(function (data, status, headers) {
                $scope.datasets = data;
                $scope.message = 'deleted ' + new Date();
            }).error(function (data, status, headers) {
                console.log('error', data, status);
                $scope.message = 'error ' + status + ' ' + new Date();
            });
        $location.search($scope.settings);
    };

    $scope.loadData();

    $scope.setFiles = function (element) {
        $scope.$apply(function (scope) {
            scope.files = [element.files[0]];
            $scope.datasetName = element.files[0].name;
        });
    };

    $scope.uploadFile = function () {
        var fd = new FormData();
        for (var i in $scope.files) {
            fd.append("file", $scope.files[i])
        }
        fd.append("datasetName", $scope.datasetName);
        var xhr = new XMLHttpRequest();
        xhr.addEventListener("load", uploadComplete, false);
        xhr.addEventListener("error", uploadFailed, false);
        xhr.addEventListener("abort", uploadCanceled, false);
        xhr.open("POST", "/datasets");
        var user = $cookieStore.get('user');
        xhr.setRequestHeader('key', user.key);
        xhr.send(fd)
    };

    function uploadComplete(evt) {
        $scope.$apply(function () {
            $scope.datasets = JSON.parse(evt.target.responseText);
            $scope.message = 'upload complete ' + new Date();
            $scope.datasetName = '';
        });
    }

    function uploadFailed(evt) {
        alert("There was an error attempting to upload the file.")
    }

    function uploadCanceled(evt) {
        alert("The upload has been canceled by the user or the browser dropped the connection.")
    }
}
