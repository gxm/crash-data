function CrashSettings($scope) {
	var that = this;
	that.zoom = Number($scope.search('zoom', 14));

	that.cars = $scope.search('cars', true);
	that.peds = $scope.search('peds', true);
	that.bikes = $scope.search('bikes', true);
	that.alcohol = $scope.search('alcohol', false);
	that.injury = $scope.search('injury', false);
	that.fatality = $scope.search('fatality', false);
	that.day = $scope.search('day', true);
	that.night = $scope.search('night', true);
	that.twilight = $scope.search('twilight', true);
	that.dry = $scope.search('dry', true);
	that.wet = $scope.search('wet', true);
	that.snowIce = $scope.search('snowIce', true);
	that.y2007 = $scope.search('y2007', true);
	that.y2008 = $scope.search('y2008', true);
	that.y2009 = $scope.search('y2009', true);
	that.y2010 = $scope.search('y2010', true);
	that.y2011 = $scope.search('y2011', true);
	that.y2012 = $scope.search('y2012', true);


}

CrashSettings.prototype.corners = function ($scope) {
	var bounds = $scope.googleMap.getBounds();
	return {
		north: $scope.latlng(bounds.getNorthEast().lat()),
		south: $scope.latlng(bounds.getSouthWest().lat()),
		east: $scope.latlng(bounds.getNorthEast().lng()),
		west: $scope.latlng(bounds.getSouthWest().lng())
	};
};

