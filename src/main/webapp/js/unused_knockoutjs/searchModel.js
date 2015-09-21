function SearchModel() {
	var self = this;
	this.searchText = ko.observable("");
	this.searchResults = ko.observableArray([]);
	this.search = function() {
        $.ajax({
        	  type: "GET",
        	  url: "rest/search",
        	  data: {
        		  "searchText": self.searchText
        	  },
        	  success: function(data, textStatus, xhr) { 
        		  alert("search status " + xhr.status);
        		  self.searchResults.removeAll();
        		  $.each(data, function(index, value) {
        			  self.searchResults.push(value);
        		  });
        	  },
        	  dataType: "json",
        	  contentType: "application/json"
        	});
    };
}


$(document).ready(function() {
		ko.applyBindings(new SearchModel(), document.getElementById('search'));
});
