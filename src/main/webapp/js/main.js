// Set brite.js to load on demand
brite.viewDefaultConfig.loadTmpl = true;
brite.viewDefaultConfig.loadCss = true;

// on document ready, display MainView
$(document).ready(function(){
  brite.display("MainView","#pageBody");
});

// Handlebars.js render wrapper
Handlebars.templates = Handlebars.templates || {};	
function render(templateName,data){
	var tmpl = Handlebars.templates[templateName];
	
	if (!tmpl){
		var tmplContent = $("#" + templateName).html();
		tmpl = Handlebars.compile(tmplContent);
		Handlebars.templates[templateName] = tmpl;		
	}
	return tmpl(data);
}


var app = app || {};
(function(){
	
	// --------- Remote Das --------- //
	app.contactDao = brite.registerDao(new RemoteDaoHandler("Contact"));
	// --------- /Remote Das --------- //
	
})();