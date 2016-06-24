	// grab all the methods, and checks the ones that are in the uuids array, uncheck the others.
	function checkMethods(uuids){
		$('.clickable').each(function(index) {
			var content_id= $(this).attr('content_id');
			// if that's a method 
    			if (content_id != "NI" ){
				// if the method is in the array of methods to be checked
				if ( $.inArray(content_id,uuids) != -1  ){
					$(this).addClass('clickedOnce');
				} else  {
					$(this).removeClass('clickedOnce');
				}
				
    			}
			
		});
	}
	
	// browse the page and return a set of all the methods uuid. 
	function getMethodUuids(){
		var uuids = new Array()
		$('.clickable').each(function(index) {
			var content_id= $(this).attr('content_id');
    			if (content_id != "NI" && $.inArray(content_id,uuids) == -1 ){
				uuids.push(content_id);
    			}
			
		});
		return uuids;
	}
		
		
		
	$(document).ready(function(){ 
	
		var iframe = $('iframe#currentFrameId');
		
		// logic to show and add the tabs.
		$('.tab').click(function(event){
			$(this).parent().children().removeClass('active');;
			$(this).addClass('active');
			var tabId= $(this).attr('id');
			$('div.tabContentContainer').hide();
			$('div#container_'+tabId).show();
		});
		
		// by default load the first visible one.
		$('.tab:first').click();
		
		
		// when a clickable item is clicked, propage that click to the element with the same id on the 
		// page ( the same method could be in several tabs ) and to the server.
		$('.clickable').click(function(event){
			var content_id= $(this).attr('content_id');
			
			if ("NI" == content_id ){
				return;
			}

			$('td.first').each(function(index) {
    			if ($(this).attr('content_id') == content_id){
    				$(this).toggleClass('clickedOnce');	
    			}
  			});
			
			// display the result in the iframe at the bottom of the page
			iframe.attr('src', content_id + '.html');
		});
		
	  });