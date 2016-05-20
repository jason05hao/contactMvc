/**
 * Component: MainView
 *
 * Responsibilities:
 *   - MainView of the application.
 *   - Will create all the necessary sub components
 *   - Manage all the application wide events
 *
 */
(function($) {

	brite.registerView("MainView", {emptyParent:true}, {
		
		create: function(data, config) {
			return render("tmpl-MainView");
		},
		
		postDisplay: function(data,config){
			var view = this;
			
			// caching some fixed jQuery elements that will be used later 
			view.$mainContent = view.$el.find(".MainView-content");
			view.$mainPanels = view.$el.find(".MainView-panels");
			view.$mainPanelsInner = view.$el.find(".MainView-panels-inner");
			
			var contactListNavPromise = brite.display("ContactListNav", view.$el.find(".MainView-left"));

			contactListNavPromise.done(function(){
				app.contactDao.list().done(function(contactList){
					view.contactList = contactList; // keep the list in this object. TODO: needs to keep this list fresh
					view.$el.trigger("DO_SELECT_CONTACT",{contactId:contactList[0].id});
				});			
		 	});			

		 	// clean the old child on transitionend
			view.$mainPanelsInner.on("btransitionend",function(){
				view.$mainPanelsInner.find("[data-state='old']").bRemove();
			});
	 	},
	 	
	 	events: {
	 		"DO_SELECT_CONTACT": doSelectContact,
	 		
	 		"btap; .MainView-next": goNext, 
	 		
	 		"btap; .MainView-prev": goPrev,

	 		"DO_NEW_CONTACT": doNewContact
	 	} 
		
	});
	
	
	// --------- events --------- //
	// This is optional, it gives a way to add some logic after the component is displayed to the user.
	// This is a good place to add all the events binding and all	
	function doSelectContact(event,extra){
		var view = this;
		
		var $contactViewPanel = $("<div class='MainView-contactViewPanel current'></div>");
		
		var oldIdx = brite.array.getIndex(view.contactList,"id",view.currentContactId);
		view.currentContactId = extra.contactId;
		var newIdx = brite.array.getIndex(view.contactList,"id",view.currentContactId);
		
		var forward = (oldIdx < newIdx);

		app.contactDao.get(extra.contactId).done(function(contact){
			
			// display the new view, and do the animation
			brite.display("ContactView",$contactViewPanel, {contact:contact}).done(function(){
				
				var $lastChild = view.$mainPanelsInner.children().filter(":last");
                $lastChild.bRemove();
                view.$mainPanelsInner.append($contactViewPanel);
			});
			
			view.$el.trigger("CONTACT_SELECTION_CHANGE",{contact:contact});
		});
				
	}
	function doNewContact(event){
        var view = this;
		var $contactViewPanel = $("<div class='MainView-contactViewPanel current'></div>");
        brite.display("ContactView",$contactViewPanel, {contact:{}}).done(function(){
            var $lastChild = view.$mainPanelsInner.children().filter(":last");
            $lastChild.bRemove();
            view.$mainPanelsInner.append($contactViewPanel);
        });
    }
	function goNext(){
		var view = this;
		var idx = brite.array.getIndex(view.contactList,"id",view.currentContactId);
		if (idx < view.contactList.length - 1){
			var nextContact = view.contactList[idx + 1];
			// just trigger the DO_SELECT_CONTACT
			view.$el.trigger("DO_SELECT_CONTACT",{contactId:nextContact.id});
		}	 				
	}
	
	function goPrev(){
		var view = this;
		var idx = brite.array.getIndex(view.contactList,"id",view.currentContactId);
		if (idx > 0){
			var nextContact = view.contactList[idx - 1];
			// just trigger the DO_SELECT_CONTACT
			view.$el.trigger("DO_SELECT_CONTACT",{contactId:nextContact.id});
		}	 			
		
	}
	// --------- /events --------- //

})(jQuery);