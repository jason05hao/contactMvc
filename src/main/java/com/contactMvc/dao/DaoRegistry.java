package com.contactMvc.dao;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.britesnow.snow.util.PackageScanner;
import com.britesnow.snow.web.binding.EntityClasses;
import com.google.common.base.Predicate;
import com.google.inject.Injector;
import com.googlecode.gentyref.GenericTypeReflector;
import com.contactMvc.entity.BaseEntity;

@Singleton
public class DaoRegistry {

    private static Class[] daoClasses = new PackageScanner(BaseDao.class.getPackage().getName()).findClasses(new Predicate<Class>() {
        public boolean apply(Class cls) {
            if (cls != BaseDao.class && BaseDao.class.isAssignableFrom(cls)) {
                return true;
            }
            return false;
        }
    });

    private Injector injector;

    private Map<Class,Class> daoClassByEntityClass = new HashMap<Class, Class>();

    private Map<String,Class> entityClassByLowerCaseName = new HashMap<String, Class>();

    @Inject
    public void init(Injector injector, @EntityClasses Class[] entityClasses){
        this.injector = injector;


        // build up the daoClassByEntityClass
        for (Class daoClass : daoClasses){
            Type persistentType = GenericTypeReflector.getTypeParameter(daoClass, BaseDao.class.getTypeParameters()[0]);
            if (persistentType instanceof Class){
                daoClassByEntityClass.put((Class)persistentType, daoClass);
            }
        }

        // for all missing entityClasses that do not have an explicity Dao, build one and put to the map
        for (Class entityClass : entityClasses){
            // Use this loop to also populate this entityClassBySimpleName map
            entityClassByLowerCaseName.put(entityClass.getSimpleName().toLowerCase(),entityClass);

            Class daoClass = daoClassByEntityClass.get(entityClass);
            // build an generic instance of the DAO if not specific one where given
            if (daoClass == null){
                // get the idClass for this entity
                throw new RuntimeException("Cannot find the Dao class for entity class: " + entityClass.getSimpleName());
            }
        }
    }

    public IDao getDao(String entitySimpleClassName) {
        Class entityClass = getEntityClass(entitySimpleClassName);
        return getDao(entityClass);
    }

    public Class<? extends BaseEntity> getEntityClass(String entitySimpleClassName){
        Class entityClass = entityClassByLowerCaseName.get(entitySimpleClassName.toLowerCase());
        return entityClass;
    }

    public <E,I> IDao<E,I> getDao(Class<E> entityClass) {
        Class daoClass = daoClassByEntityClass.get(entityClass);
        // if we have match, then, return what would return the app injection
        if (daoClass != null){
            return (IDao<E,I>) injector.getInstance(daoClass);
        }
        return null;
    }
}