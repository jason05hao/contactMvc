package com.contactMvc.dao;

import com.google.inject.Singleton;
import org.j8ql.query.Query;
import org.j8ql.query.Condition;
import org.j8ql.query.SelectQuery;
import com.contactMvc.entity.*;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * ProjectDao subclass BaseDao for the following reasons:
 *
 * - to override doCreate method to create the defaultOwnerTeam
 * - to add the @RequireOrgPrivileges to add privileges
 *
 * TODO: we will need to add the @RequireOrgPrivileges for list Project as well.
 */
@Singleton
public class ContactDao extends BaseDao<Contact, Long> {

    @Inject
    public ContactDao(){
        defaultOrderBy = new String[]{"firstName", "lastName"};
    }

    // --------- Create --------- //
    /**
     * @param id
     * @return
     */
    @Override
    public Optional<Contact> get(Long id) {
        return super.get(id);
    }

    @Override
    protected Long doCreate(Contact newEntity, Set<String> columns) {
        if (columns == null)
            columns = new HashSet<>();
        Class aContact = Contact.class;
        Field[] fields = aContact.getDeclaredFields();
        for(Field field : fields)
        {
            columns.add(field.getName());
        }

        Long projectId = super.doCreate(newEntity, columns);
        return projectId;
    }

    @Override
    public List<Contact> list(Condition filter, int pageIdx, int pageSize, String... orderBy) {
        // get the basic list select
        SelectQuery<Contact> select = listSelectBuilder(filter, pageIdx, pageSize, orderBy);
        return daoHelper.list(select);
    }

}