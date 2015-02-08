function CrashSettings($scope) {
	var that = this;
	that.zoom = Number($scope.search('zoom', 11));

	that.cars = $scope.search('cars', true);
	that.peds = $scope.search('peds', true);
	that.bikes = $scope.search('bikes', true);
	that.alcohol = $scope.search('alcohol', true);
	that.drug = $scope.search('drug', true);
	that.sober = $scope.search('sober', true);
	that.fatal = $scope.search('fatal', true);
	that.injuryA = $scope.search('injuryA', true);
	that.injuryB = $scope.search('injuryB', false);
	that.injuryC = $scope.search('injuryC', false);
	that.property = $scope.search('property', false);
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
	that.y2013 = $scope.search('y2013', true);
	that.angle = $scope.search('angle', true);
	that.headOn = $scope.search('headOn', true);
	that.rearEnd = $scope.search('rearEnd', true);
	that.sideSwipe = $scope.search('sideSwipe', true);
	that.turning = $scope.search('turning', true);
	that.other = $scope.search('other', true);
	that.scope = $scope.search('scope', 'Window');
	that.sinks = $scope.search('sinks', false);
	that.colors = $scope.search('colors', 'default');
    that.arterial = $scope.search('arterial', false);
}


