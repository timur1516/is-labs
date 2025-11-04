package ru.itmo.se.is.repository;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
public abstract class GenericEclipseLinkLazyRepository<T> implements LazyRepository<T> {

    private Class<T> entityClass;
    private DatabaseSession session;

    @Override
    public long count(Map<String, Object> filterBy) {
        UnitOfWork uow = session.getActiveUnitOfWork();
        ExpressionBuilder builder = new ExpressionBuilder();

        ReportQuery query = new ReportQuery(entityClass, builder);
        query.addCount();
        query.setShouldReturnSingleValue(true);

        applyFilters(query, builder, filterBy);

        Number count = (Number) uow.executeQuery(query);
        return count.longValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> load(int first, int pageSize, String sortField, int sortOrder, Map<String, Object> filterBy) {
        UnitOfWork uow = session.getActiveUnitOfWork();
        ExpressionBuilder builder = new ExpressionBuilder();

        ReadAllQuery query = new ReadAllQuery(entityClass);

        applyFilters(query, builder, filterBy);
        applySort(query, builder, sortField, sortOrder);

        query.setFirstResult(first);
        query.setMaxRows(pageSize);

        return (List<T>) uow.executeQuery(query);
    }

    private void applySort(ReadAllQuery query, ExpressionBuilder builder, String sortField, int sortOrder) {
        if (sortField != null && !sortField.isEmpty()) {
            Expression sortExpr = builder.get(sortField);
            query.addOrdering(sortOrder == -1 ? sortExpr.descending() : sortExpr.ascending());
        }
    }

    private void applyFilters(ReadAllQuery query, ExpressionBuilder builder, Map<String, Object> filterBy) {
        Expression expr = null;
        for (Map.Entry<String, Object> entry : filterBy.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Expression condition = builder.get(key).equal(value);
            expr = (expr == null) ? condition : expr.and(condition);
        }
        if (expr != null) {
            query.setSelectionCriteria(expr);
        }
    }
}

