/**
 * View: ContactListNav
 *
 * Responsibilities:
 *   - Manage the list of contact (create, delete, select, rename)
 *
 */
(function($) {
	
	brite.registerView("ContactListNav",{
		
		create: function(){
			return render("tmpl-ContactListNav");
		},
		
		postDisplay: function(){
			var view = this;
			view.$listContainer = view.$el.find(".list-container");
			
			refreshDetail.call(view);
		},
		
		events: {
			"btap; li[data-entity='Contact']" : function(event){
				var $li = $(event.currentTarget);
				var contactId = $li.bEntity("Contact").id;
				$li.trigger("DO_SELECT_CONTACT",{contactId:contactId});
			},
            "click; [data-action='new']": function(event){
				var $li = $(event.currentTarget);
				$li.trigger("DO_NEW_CONTACT");
            }
		},
		
		docEvents: {
			"CONTACT_SELECTION_CHANGE": function(event,extra){
				showContactSelected.call(this,extra.contact.id);
			}
		},
		
		daoEvents: {
			"dataChange; Contact": refreshDetail
		} 
		
	});


	function refreshDetail(){
		var view = this;
		app.contactDao.list().done(function(contactList){
			view.$listContainer.html(render("tmpl-ContactListNav-list",{contacts:contactList}));
		});
	}

	function showContactSelected(contactId){
		var view = this;

		// deselect any eventual selection
		view.$el.find("li.sel").removeClass("sel");
		view.$el.find("i.icon-folder-open").removeClass("icon-folder-open").addClass("icon-folder-close");
		
		// select the li
		var $selectedLi = view.$el.find("li[data-entity-id='" + contactId + "']");
		$selectedLi.addClass("sel");
		$selectedLi.find("i.icon-folder-close").removeClass("icon-folder-close").addClass("icon-folder-open");

		// keep that for dataChangeEvent (to keep the item selected)
		view.selectedContactId = contactId;
	}

})(jQuery);