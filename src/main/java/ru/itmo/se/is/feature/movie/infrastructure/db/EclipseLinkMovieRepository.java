package ru.itmo.se.is.feature.movie.infrastructure.db;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import ru.itmo.se.is.feature.movie.domain.Movie;
import ru.itmo.se.is.feature.movie.domain.MovieRepository;
import ru.itmo.se.is.feature.person.domain.Person;
import ru.itmo.se.is.platform.db.eclipselink.EclipseLinkPagingAndSortingRepository;
import ru.itmo.se.is.platform.db.eclipselink.UnitOfWorkManager;

@ApplicationScoped
@NoArgsConstructor
public class EclipseLinkMovieRepository
        extends EclipseLinkPagingAndSortingRepository<Movie, Long>
        implements MovieRepository {

    @Inject
    public EclipseLinkMovieRepository(UnitOfWorkManager unitOfWorkManager) {
        super(Movie.class, unitOfWorkManager);
    }

    @Override
    public boolean existsByNameAndDirectorName(String name, String directorName) {
        UnitOfWork uow = unitOfWorkManager.getCurrent();
        ExpressionBuilder builder = new ExpressionBuilder(Movie.class);

        Expression expression = builder.get("name").equal(name)
                .and(builder.get("director").get("name").equal(directorName));

        ReadObjectQuery query = new ReadObjectQuery(Movie.class);
        query.setSelectionCriteria(expression);
        query.conformResultsInUnitOfWork();

        Movie result = (Movie) uow.executeQuery(query);
        return result != null;
    }

    @Override
    public boolean existsByNameAndDirectorNameAndIdNot(String name, String directorName, Long id) {
        UnitOfWork uow = unitOfWorkManager.getCurrent();
        ExpressionBuilder builder = new ExpressionBuilder(Movie.class);

        Expression expression = builder.get("name").equal(name)
                .and(builder.get("director").get("name").equal(directorName))
                .and(builder.get("id").notEqual(id));

        ReadObjectQuery query = new ReadObjectQuery(Movie.class);
        query.setSelectionCriteria(expression);
        query.conformResultsInUnitOfWork();

        Movie result = (Movie) uow.executeQuery(query);
        return result != null;
    }

    @Override
    protected void registerNestedFields(UnitOfWork uow, Movie movie) {
        if (movie.getDirector() != null) {
            movie.setDirector((Person) uow.registerObject(movie.getDirector()));
        }
        if (movie.getOperator() != null) {
            movie.setOperator((Person) uow.registerObject(movie.getOperator()));
        }
        if (movie.getScreenwriter() != null) {
            movie.setScreenwriter((Person) uow.registerObject(movie.getScreenwriter()));
        }
    }
}
