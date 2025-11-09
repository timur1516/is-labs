package ru.itmo.se.is.feature.person.infrastructure.db;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import ru.itmo.se.is.feature.person.domain.Person;
import ru.itmo.se.is.feature.person.domain.PersonRepository;
import ru.itmo.se.is.platform.db.eclipselink.EclipseLinkPagingAndSortingRepository;
import ru.itmo.se.is.platform.db.eclipselink.UnitOfWorkManager;

import java.util.Optional;

@ApplicationScoped
@NoArgsConstructor
public class EclipseLinkPersonRepository
        extends EclipseLinkPagingAndSortingRepository<Person, Long>
        implements PersonRepository {

    @Inject
    public EclipseLinkPersonRepository(UnitOfWorkManager unitOfWorkManager) {
        super(Person.class, unitOfWorkManager);
    }

    @Override
    public boolean existsByName(String name) {
        UnitOfWork uow = unitOfWorkManager.getCurrent();
        ExpressionBuilder builder = new ExpressionBuilder(Person.class);
        Expression expression = builder.get("name").equal(name);

        ReadObjectQuery query = new ReadObjectQuery(Person.class);
        query.setSelectionCriteria(expression);
        query.conformResultsInUnitOfWork();

        Person result = (Person) uow.executeQuery(query);
        return result != null;
    }

    @Override
    public Optional<Person> findByName(String name) {
        UnitOfWork uow = unitOfWorkManager.getCurrent();
        System.out.println("UOW in findByName: " + System.identityHashCode(uow));
        ExpressionBuilder builder = new ExpressionBuilder(Person.class);
        Expression expression = builder.get("name").equal(name);

        ReadObjectQuery query = new ReadObjectQuery(Person.class);
        query.setSelectionCriteria(expression);
        query.conformResultsInUnitOfWork();

        Person person = (Person) uow.executeQuery(query);
        return Optional.ofNullable(person);
    }

    @Override
    public boolean existsByNameAndIdNot(String name, Long id) {
        UnitOfWork uow = unitOfWorkManager.getCurrent();
        ExpressionBuilder builder = new ExpressionBuilder(Person.class);
        Expression expression = builder.get("name").equal(name)
                .and(builder.get("id").notEqual(id));

        ReadObjectQuery query = new ReadObjectQuery(Person.class);
        query.setSelectionCriteria(expression);
        query.conformResultsInUnitOfWork();

        Person result = (Person) uow.executeQuery(query);
        return result != null;
    }
}
