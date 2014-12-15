function DatasetsController($scope, $http, $location) {

    'use strict';

    new Common($scope, $http, $location);

    $scope.datasets = [];
    $scope.message = 'loading...';
    $scope.progressVisible = false

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

    $scope.loadData();

    $scope.setFiles = function (element) {
        $scope.$apply(function (scope) {
            console.log('files:', element.files);
            // Turn the FileList object into an Array
            scope.files = []
            for (var i = 0; i < element.files.length; i++) {
                scope.files.push(element.files[i])
            }
            scope.progressVisible = false
        });
    };

    $scope.uploadFile = function () {
        var fd = new FormData()
        for (var i in $scope.files) {
            fd.append("file", $scope.files[i])
        }
        console.log('$scope.datasetName', $scope.datasetName);
        fd.append("datasetName", $scope.datasetName);
        var xhr = new XMLHttpRequest()
        xhr.upload.addEventListener("progress", uploadProgress, false)
        xhr.addEventListener("load", uploadComplete, false)
        xhr.addEventListener("error", uploadFailed, false)
        xhr.addEventListener("abort", uploadCanceled, false)
        xhr.open("POST", "/datasets")
        $scope.progressVisible = true
        xhr.send(fd)
    }

    function uploadProgress(evt) {
        $scope.$apply(function () {
            if (evt.lengthComputable) {
                $scope.progress = Math.round(evt.loaded * 100 / evt.total)
            } else {
                $scope.progress = 'unable to compute'
            }
        })
    }

    function uploadComplete(evt) {
        $scope.datasets = JSON.parse(evt.target.responseText);
        $scope.message = 'upload complete ' + new Date();
    }

    function uploadFailed(evt) {
        alert("There was an error attempting to upload the file.")
    }

    function uploadCanceled(evt) {
        $scope.$apply(function () {
            $scope.progressVisible = false
        })
        alert("The upload has been canceled by the user or the browser dropped the connection.")
    }
}
