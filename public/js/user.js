function UserController($scope, $http, $location, $cookieStore) {

    'use strict';

    new Common($scope, $http, $location);

    $scope.links = [
        {name:'Crash Map', href:'crashes.html'},
        {name:'Login', href:'login.html'}
    ];

    //todo - gfm - 12/28/14 - get this to return user type

    $scope.user = '';
    $scope.password = '';
    $scope.message = '';

    $scope.loadLinks = function loadData() {
        var user = $cookieStore.get('user');
        console.log('user', user);
        if (user) {
            if (user.type === 'admin') {
                $scope.links = [
                    {name:'Crash Map', href:'crashes.html'},
                    {name:'Data Sets', href:'datasets.html'},
                    {name:'Logout', href:'logout.html'}
                ];
            }
        }
    };

    $scope.loadLinks();

    $scope.login = function login() {
        console.log('login');
        $http.post($scope.host + 'user',
            { user: $scope.user, password: $scope.password })
            .success(function (data, status, headers) {
                console.log('login!', data);
                $cookieStore.put('user', data);
                window.location.replace($scope.host + 'file/index.html');
            }).error(function (data, status, headers) {
                console.log('login error', status);
                $scope.message = data || 'Unable to login ' + status;
            });
    };

    $scope.logout = function login() {
        $cookieStore.put('user', {});
        window.location.replace($scope.host + 'file/index.html');
    }

}
