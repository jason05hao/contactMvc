package com.contactMvc.dao;

import com.google.inject.Inject;
import com.googlecode.gentyref.GenericTypeReflector;

import org.j8ql.query.*;
import com.contactMvc.entity.BaseEntity;

import java.lang.reflect.Type;
import java.util.*;

import static org.j8ql.query.Query.insert;


@SuppressWarnings({"rawtypes", "unchecked"})
public  class BaseDao<E extends BaseEntity,I> implements IDao<E,I> {

    protected Class<E> entityClass;
    protected Class<I> idClass;

    @Inject
    protected DaoHelper daoHelper;


    //// Here we have the baseQueries that will be initialized in the initBaseQueries with the
    // correct default exclusion and type.
    // Note: that all J8QL Query object are immutable, so safe to be used to create more specialized queries
    // insertQuery will return the id of the entity
    protected InsertQuery<I> baseInsertQuery;
    // updateQuery will return the number of updated rows
    protected UpdateQuery<Integer> baseUpdateQuery;

    // DefaultOrderBy used whe no orderBy is explicitly specified
    // Can be set in subclass daos to change the
    protected String[] defaultOrderBy = new String[]{"id"};


    public BaseDao() {
        initEntityClass();
    }

    public BaseDao(boolean entityClassProvided){
        if (!entityClassProvided){
            initEntityClass();
        }
    }

    private void initEntityClass(){
        if (entityClass == null && idClass == null) {
            Type persistentType = GenericTypeReflector.getTypeParameter(getClass(), BaseDao.class.getTypeParameters()[0]);
            Type persistentIdType = GenericTypeReflector.getTypeParameter(getClass(), BaseDao.class.getTypeParameters()[1]);
            if (persistentType instanceof Class && persistentIdType instanceof Class) {
                this.entityClass = (Class<E>) persistentType;
                this.idClass = (Class<I>) persistentIdType;
                initBaseQueries();
            } else {
                throw new IllegalStateException("concrete class " + getClass().getName()
                        + " must have a generic Entity and ID types "
                        + BaseDao.class.getName());
            }
        }
    }

    protected void initBaseQueries(){
        if (entityClass != null){
            // by default, all entityClasses returns the PK Id
            baseInsertQuery = insert(entityClass).returningIdAs(idClass);

            // exclude the createUserId (cid), createTime (ctime), and the id of any update
            baseUpdateQuery = Query.update(entityClass).excludeColumns("id", "cid","ctime");
        }else {
            throw new RuntimeException("Cannot init baseDao, somehow no entity class for dao " + this);
        }

    }

    public Optional<E> getWithColumns(I id, String... columns) {
        return daoHelper.first(Query.select(entityClass).whereId(id).columns(columns));
    }

    // --------- IDao Interface --------- //
    @Override
    public Optional<E> get(I id) {
        return daoHelper.first(Query.select(entityClass).whereId(id));
    }

    /**
     * Create a entity entity type.
     *
     * Note: This is marked final as it is not a customization point.
     *       The appropriate customization/overridable point is doCreate
     *
     * @param newEntity
     * @return
     */
    @Override
    public final I create(E newEntity) {
        return doCreate(newEntity, null);
    }

    /**
     * Cannonical method that perform the create.
     *
     * Note: This is the method to override when some subclass need to do some custom behavior.
     *
     * Note: This class is protected as it is NOT a public api, but just a customization
     *       point for the sub classes
     *
     * @param newEntity
     * @param columns
     * @return
     */
    protected I doCreate(E newEntity, Set<String> columns){
        InsertQuery<I> insertQuery = getBaseInsertQuery().value(newEntity);
        List<String> validColumns = daoHelper.getValidColumns(insertQuery, columns);
        return daoHelper.execute(insertQuery.columns((List<String>)validColumns));
    }

    @Override
    public final int update(E entity, I id) {
        return doUpdate(entity, id, null);
    }

    /**
     * Perform the update. As with the doCreate, this is a customization point by subclasses.
     *
     * @param entity
     * @param id
     * @param columns
     * @return
     */
    protected int doUpdate(E entity, I id, Set<String> columns){
        // similar to doCreate
        UpdateQuery<Integer> updateQuery = getBaseUpdateQuery().value(entity).whereId(id);
        List<String> validColumns = daoHelper.getValidColumns(updateQuery, columns);
        return daoHelper.execute(updateQuery.columns((List<String>) validColumns));
    }

    @Override
    public int delete(I id) {
        return daoHelper.execute(Query.delete(entityClass).whereId(id));
    }

    @Override
    public List<E> list(Condition filter, int pageIdx, int pageSize, String... orderBy) {
        return daoHelper.list(listSelectBuilder(filter,pageIdx,pageSize,orderBy));
    }

    @Override
    public Long count(Condition filter) {
        return daoHelper.count(Query.select(entityClass).where(filter));
    }

    @Override
    public Class<E> getEntityClass() {
        return entityClass;
    }

    @Override
    public Class<I> getIdClass(){
        return idClass;
    }
    // --------- IDao Interface --------- //


    // --------- Protected --------- //
    protected String[] getOrderByOrDefault(String[] orderBy){
        return (orderBy != null && orderBy.length > 0) ? orderBy : defaultOrderBy;
    }

    /**
     * Base selectBuilder for the list api. Reused by sub class DAOs that need to add joins to the list.
     */
    protected SelectQuery<E> listSelectBuilder(Condition filter, int pageIdx, int pageSize, String... orderBy) {
        int limit = pageSize;
        int offset = pageIdx * pageSize;
        String[] o = getOrderByOrDefault(orderBy);
        return Query.select(entityClass).where(filter).limit(limit).offset(offset).orderBy(getOrderByOrDefault(orderBy));
    }
    // --------- /Protected --------- //

    protected final InsertQuery<I> getBaseInsertQuery(){
        //InsertQuery<I> baseInsertQuery = insert(entityClass).returningIdAs(idClass);
        return baseInsertQuery;
    }

    protected final UpdateQuery<Integer> getBaseUpdateQuery(){
        //UpdateQuery<Integer> baseUpdateQuery = Query.update(entityClass).excludeColumns("cid","ctime");
        return baseUpdateQuery;
    }
}