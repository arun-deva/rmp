function MusicSourceModel(potentialSources, currentSource) {
	var self = this;
	this.potentialMusicSources = ko.observableArray(potentialSources);
	if (currentSource == null) {
		this.currentMusicSource = {
			location: ko.observable()
		};
	} else {
		this.currentMusicSource = {
			location: ko.observable(currentSource.location)
		};
	}

	this.updateStatus = ko.observable();
	this.updateMusicSource = function() {
        alert("need to update music source");
        $.ajax({
        	  type: "POST",
        	  url: "rest/musicsource/update",
        	  data: ko.toJSON(this.currentMusicSource),
        	  success: function(data, textStatus, xhr) { 
        		  alert("update status " + textStatus); 
        		  self.updateStatus(textStatus);
        		  },
        	  error: 
        		  function(data, textStatus, xhr) {
        		    alert(xhr.status); 
        		    self.updateStatus(textStatus);
        		  },
        	  dataType: "json",
        	  contentType: "application/json"
        	});
        //update music source
        //update status
    };
}

//Get the "musicSource" element (a div) from the html page - here we are using jquery syntax shortcut to lookup the element 
//ko.applyBindings(<the model containing the bindings to apply>, <the element to which to confine the search for databind>)
$(document).ready(function() {
	var model;
	var potentialSources = [];
	var currentSource;
	$.get("rest/musicsource/info", function(data) {
		currentSource = data.currentMusicSource;
		potentialSources = data.potentialMusicSources;
		model = new MusicSourceModel(potentialSources, currentSource);
		ko.applyBindings(model, document.getElementById('musicSource'));
	});	
});
