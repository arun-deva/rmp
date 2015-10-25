var rmpApp = angular.module('RemoteMusicPlayer',[]);

/****************Music Source Controller********************/
var musicSrcCtrl = rmpApp.controller('musicSourceController', ['$scope', '$http', function($scope, $http) {
	//implementation here
	$scope.status = "";

	$scope.initMusicSources = function(data, status, headers, config) {
		  $scope.currentMusicSource = data.currentMusicSource;
		  $scope.potentialMusicSources = data.potentialMusicSources;
	};

	//get the music sources when controller is constructed
	$http.get("rest/musicsource/info")
	  .success($scope.initMusicSources)
	  .error(function(data, status, headers, config) {
		  $scope.status =  "Failed to retrieve music sources! Status code: " + status;
	    });


	//update music source
    $scope.updateMusicSource = function() {
		  //create and post an update request
		  var updateReq = {
		    method: "POST",
			url: "rest/musicsource/update",
			headers: {
			  "Content-Type": "application/json"
			},
			data: $scope.currentMusicSource
		  }
		  $http(updateReq)
		    .success(
		      function(data, status, headers, config) { 
        		$scope.status="success! " + status;
              })
        	.error(
        	  function(data, status, headers, config) {
        		  $scope.status="error! " + status;
        	  });  
	  }
	}]);

/****************Search Controller********************/
var searchCtrl = rmpApp.controller('SearchController',  ['$scope', '$http', function($scope, $http) {
	$scope.searchText = "";
	$scope.searchResults = [];
    $scope.status = "";
    $scope.addedSongs = {};
	$scope.search = 
      function() {
		$scope.status = ""; //reset status
		var searchReq = {
				  method: "GET",
				  url: "rest/search",
				  params: { "searchText": $scope.searchText }
	    };
        $http(searchReq)
          .success(
            function(data, status, headers, config) { 
        		  $scope.searchResults = data;
        	})
          .error(
            function(data, status, headers, config) {
            	$scope.status=data.message;
            });
      }
	
	$scope.addToPlayList = function(resultIdx) {
		var addReq = {
		  method: "POST",
		  url: "rest/play/add",
		  params: { "key": $scope.searchResults[resultIdx]["key"] }
	    };
        $http(addReq)
        .success(
          function(data, status, headers, config) {
        	//setAdded for this search result so we can reflect that in display
        	$scope.setAdded(resultIdx)
      	})
        .error(
          function(data, status, headers, config) {
          	$scope.status="error! " + status;
          });
	}
	
	$scope.setAdded = function(resultIdx) {
		$scope.addedSongs[resultIdx] = 1;
	}
	
	$scope.isAdded = function(idx) {
		return ($scope.addedSongs[idx] == 1);
	}
}]);
/****************Play Controller********************/
var playCtrl = rmpApp.controller('PlayController',  ['$scope', '$http', '$interval', function($scope, $http, $interval) {
	$scope.playList = [];	
	$scope.playListStatus = {
		"nowPlayingChangedMillis" : 0,
		"playListChangedMillis" : 0,
	};
	$scope.editingSongKey = "";
	
	$scope.pause = function() {
		$http.post("rest/play/pause");
	}
	$scope.stop = function() {
		$http.post("rest/play/stop");
	}
	$scope.skip = function() {
		$http.post("rest/play/skip");
	}
	$scope.random = function() {
		$http.post("rest/play/random");
	}     
	$scope.isCurrentlyEditing = function(key) {
		return (key == $scope.editingSongKey);
	}
	$scope.isEditable = function(song) {
		if (typeof song == 'undefined') return false;
		if (typeof song.songInfo == 'undefined') return false;
		return song.songInfo.editable;
	}
	$scope.editMetaData = function(key) {
		$scope.editingSongKey = key;
	}
	$scope.cancelEditMetaData = function() {
		$scope.editingSongKey = "";
	}
	$scope.updateMetaData = function(song) {
      $http.post("rest/tagEdit/update", song)
      .success(
        function(data, status, headers, config) {
        	$scope.editingSongKey = "";
    	})
      .error(
        function(data, status, headers, config) {
        	$scope.status="error! " + status;
        });
	}
	$scope.removeFromPlayList = function(idx) {
		var removeReq = {
				  method: "POST",
				  url: "rest/play/remove",
				  params: { "key": $scope.playList[idx]["key"] }
			    };
        $http(removeReq)
        .success(
          function(data, status, headers, config) {
        	  //splice removes idx th element
        	  $scope.playList.splice(idx, 1);
      	})
        .error(
          function(data, status, headers, config) {
          	$scope.status="error! " + status;
          });
	}

	$scope.refreshPlayList = function() {
		$http.get("rest/play/playList")
		.success(
				function(data, status, headers, config) { 
					$scope.playList = data;
				})
		.error(
				function(data, status, headers, config) {
					$scope.status="error! " + status;
				});
	}
	
	function pollPlayListStatus() {
		$http.get("rest/play/playListStatus")
		.success(
				function(data, status, headers, config) { 
					if (isPlayListChanged(data)) {
						$scope.refreshPlayList();
					}
					if (isNowPlayingSongChanged(data)) {
						$scope.playListStatus.nowPlayingSong = data.nowPlayingSong;
						$scope.editingNowPlaying = false;
					}
					$scope.playListStatus.playListChangedMillis = data.playListChangedMillis;
					$scope.playListStatus.nowPlayingChangedMillis = data.nowPlayingChangedMillis;
				});
	}
	
	function isPlayListChanged(polledStatus) {
		return (polledStatus["playListChangedMillis"] != $scope.playListStatus["playListChangedMillis"]);
	}
	
	function isNowPlayingSongChanged(polledStatus) {
		return (polledStatus["nowPlayingChangedMillis"] != $scope.playListStatus["nowPlayingChangedMillis"]);
	}
	$interval(pollPlayListStatus, 10000);
	
	
}]);

/****************Tab Controller********************/
var tabCtrl = rmpApp.controller('TabController', ['$scope', function($scope) {
	$scope.currentTab = "MusicSourceTab";
	$scope.selectTab = function(tabName) {
		$scope.currentTab = tabName;
	};
	$scope.isTabSelected = function(tabName) {
		return ($scope.currentTab == tabName);
	};
}]);