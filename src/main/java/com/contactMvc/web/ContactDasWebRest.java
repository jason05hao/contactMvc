package com.contactMvc.web;

import com.britesnow.snow.web.param.annotation.PathVar;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.britesnow.snow.web.rest.annotation.WebPost;
//import com.britesnow.snow.web.param.annotation.WebModel;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.j8ql.query.Condition;
import java.util.ArrayList;
import com.contactMvc.entity.Contact;
import java.util.List;
import com.contactMvc.dao.*;
import com.contactMvc.AppException;
import com.contactMvc.web.annotation.JsonParam;
import java.util.Map;
import org.jomni.JomniBuilder;
import org.jomni.JomniMapper;
/**
 * <p>This is the generic Data Access Service layer from the server.</p>
 *
 * <p><strong>Note:</strong> Non parameterized @WebRest (i.e. @WebGet("/das-get-Project") will take precedence over
 * parameterized ones (i.e. @WebGet("das-get-{entity}"). Therefore, this GenericDasWebRest can been seen as a fall back
 * fall back when no specialized DasWebRest methods are handling specific entity.</p>
 *
 * <p><strong>Best Practice:</strong> Often, when starting a new project, this is a great and and simple way to start connecting
 * your client to those Web[REST] APIs. As the application becomes more sophisticated, you can override those generic bindings with
 * more specifics ones (e.g., for added security and business rules) without ever changing the UI code.</p>
 *
 */
@Singleton
public class ContactDasWebRest {

	@Inject
	private DaoRegistry daoRegistry;

	protected JomniMapper jomni = new JomniBuilder().build();
	@WebGet("/das-get-Contact")
	public WebResponse getContact(@WebParam("id") Long id){
		// read the contact
		IDao dao = daoRegistry.getDao(Contact.class);
		Object entity = dao.get(id).orElse(null);

		return WebResponse.success(entity);
	}

	@WebGet("/das-list-Contact")
	public WebResponse listContact(){
		// retrieve contact list
		IDao dao = daoRegistry.getDao(Contact.class);
		List<Object> list = dao.list(null,0,1000);
		return WebResponse.success(list);
	}

	@WebPost("/das-create-Contact")
	public WebResponse createEntity(@JsonParam("props") Map map){
		IDao dao = daoRegistry.getDao(Contact.class);
		Contact newEntity = jomni.as(Contact.class, map);
		// TODO: probably need to have a createWithReturn
		Object id = dao.create(newEntity);
		Object entity = dao.get(id).orElse(null);
		return WebResponse.success(entity);
	}

	@WebPost("/das-update-Contact")
	public WebResponse updateContact(@WebParam("id") Long id, @JsonParam("props") Map map){
		IDao dao = daoRegistry.getDao(Contact.class);
		Contact newEntity = jomni.as(Contact.class, map);

		int r = dao.update(newEntity, id);
		return WebResponse.success(r);
	}

	@WebPost("/das-delete-Contact")
	public WebResponse deleteEntity(@WebParam("id") Long id){
		IDao dao = daoRegistry.getDao(Contact.class);

		int numDeleted = dao.delete(id);

		if (numDeleted > 0){
			return WebResponse.success(id);
		}else{
			return WebResponse.fail(new AppException("Cannot delete Contact with id " + id));
		}
	}

}