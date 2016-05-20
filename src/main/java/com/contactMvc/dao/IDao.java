package com.contactMvc.dao;
import org.j8ql.query.Condition;
import java.util.List;
import java.util.Optional;

public interface IDao<E,I> {

    public enum SortOrder {
        ASC, DESC
    }

    public Optional<E> get(I id);

    /**
     * Create the entity and returns its id.
     * @return entity id
     */
    public I create(E newEntity);

    public int update(E entity, I id);

    public int delete(I id);

    public List<E> list(Condition filter, int pageIdx, int pageSize, String... orderBy);

    public Long count(Condition filter);

    public Class<E> getEntityClass();

    public Class<I> getIdClass();

}