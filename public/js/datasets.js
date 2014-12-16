function DatasetsController($scope, $http, $location) {

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

    $scope.updateData = function updateData(index) {
        $http.put($scope.host + 'datasets', $scope.datasets[index])
            .success(function (data, status, headers) {
                $scope.datasets = data;
                $scope.message = 'updated ' + new Date();
            }).error(function (data, status, headers) {
                console.log('error', data, status);
            });
        $location.search($scope.settings);
    };

    $scope.deleteDataset = function deleteDataset(index) {
        var url = $scope.host + 'datasets/' + $scope.datasets[index].name;
        console.log('calling', url);
        $http.delete(url)
            .success(function (data, status, headers) {
                $scope.datasets = data;
                $scope.message = 'deleted ' + new Date();
            }).error(function (data, status, headers) {
                console.log('error', data, status);
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
