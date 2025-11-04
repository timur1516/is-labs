package ru.itmo.se.is.repository;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.DeleteAllQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unchecked")
public abstract class GenericEclipseLinkRepository<T, ID> implements Repository<T, ID> {

    private Class<T> entityClass;
    private DatabaseSession session;

    @Override
    public List<T> findAll() {
        UnitOfWork uow = session.getActiveUnitOfWork();
        return (List<T>) uow.readAllObjects(entityClass);
    }

    @Override
    public Optional<T> findById(ID id) {
        UnitOfWork uow = session.getActiveUnitOfWork();
        ExpressionBuilder b = new ExpressionBuilder();
        T entity = (T) uow.readObject(
                entityClass,
                b.get("id").equal(id)
        );
        return Optional.ofNullable(entity);
    }

    @Override
    public T save(T entity) {
        UnitOfWork uow = session.getActiveUnitOfWork();
        T managed = (T) uow.registerNewObject(entity);
        registerNestedFields(uow, managed);
        uow.writeChanges();
        return managed;
    }

    public void update(T entity, Consumer<T> fieldUpdater) {
        UnitOfWork uow = session.getActiveUnitOfWork();
        T managed = (T) uow.readObject(entity);
        fieldUpdater.accept(managed);
        registerNestedFields(uow, managed);
    }

    @Override
    public void deleteById(ID id) {
        UnitOfWork uow = session.getActiveUnitOfWork();
        findById(id).ifPresent(uow::deleteObject);
    }

    protected abstract void registerNestedFields(UnitOfWork uow, T entity);
}
