/**
 * Component: ContactView
 *
 * Responsibilities:
 *   - Manage a single contact screen
 *   - Manage the task list of a contact
 *
 */
(function($) {
	brite.registerView("ContactView",{
		create: function(data){
			var view = this;

			// if the contact is given, then, just render it.
			if (data.contact){
				view.contact = data.contact;
				return render("tmpl-ContactView",{contact:view.contact});
			}
			// otherwise, we fetch it and return the appropriate promise.
			else{
				return app.contactDao.get(data.contactId).pipe(function(contact){
					view.contact = contact;
					return render("tmpl-ContactView",{contact:contact});
				});
			}
		},
        postDisplay: function(){
            var view = this;
            // cache some fixed elements
            view.$card = view.$el.find(".card");
            view.$cardBack = view.$el.find(".card-back");
            view.$cardFront = view.$el.find(".card-front");
            view.$sectionContent = view.$el.find("section.content");
        },
        events: {
            "click; [data-action='save']": function(event){
                var view = this;
                var contact = {};
                var $allInputs = view.$el.find("input[data-prop^='contact']");
                $allInputs.each(function(index){
                    contact[$(this)[0].name] = $(this)[0].value;
                });
                // Submit save to dao
                if (contact["id"]) {
                    // update
                    app.contactDao.update(contact["id"], contact).done(function (){
                        setTimeout(function() {view.$el.trigger("DO_SELECT_CONTACT",{contactId:contact["id"]})}, 1000);
                    });
                }
                else {
                    // insert
                    contact["id"] = 0;
                    app.contactDao.create(contact).done(function (contact){
                        setTimeout(function() {view.$el.trigger("DO_SELECT_CONTACT",{contactId:contact["id"]})}, 1000);
                    });
                }
            },
            "click; [data-action='delete']": function(event){
                var view = this;
                var contact = {};
                contact.id = view.$el.find("input[name='id']")[0].value;
                if (!contact.id || contact.id <= 0) {
                    alert("Please select contact.");
                    return;
                }
                if (confirm("Are you sure you want to delete the contact?")) {
                    // Call delete function
                    app.contactDao.delete(contact.id).done(function (){
                        var $li = $(event.currentTarget);
                        $li.trigger("DO_NEW_CONTACT");
                    });
                }
            },
        },
	});

})(jQuery); 