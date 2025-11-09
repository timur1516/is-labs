package ru.itmo.se.is.feature.movie.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import ru.itmo.se.is.feature.movie.api.dto.MovieCountResponseDto;
import ru.itmo.se.is.feature.movie.api.dto.MovieLazyBeanParamDto;
import ru.itmo.se.is.feature.movie.api.dto.MovieLazyResponseDto;
import ru.itmo.se.is.feature.movie.api.dto.MovieRequestDto;
import ru.itmo.se.is.feature.movie.domain.Movie;
import ru.itmo.se.is.feature.movie.domain.MovieRepository;
import ru.itmo.se.is.feature.movie.domain.value.MpaaRating;
import ru.itmo.se.is.feature.movie.infrastructure.mapper.MovieMapper;
import ru.itmo.se.is.feature.person.api.dto.PersonResponseDto;
import ru.itmo.se.is.feature.person.application.PersonService;
import ru.itmo.se.is.feature.person.domain.Person;
import ru.itmo.se.is.feature.person.infrastructure.mapper.PersonMapper;
import ru.itmo.se.is.platform.db.eclipselink.tx.annotation.Transactional;
import ru.itmo.se.is.shared.exception.EntityAlreadyExistsException;
import ru.itmo.se.is.shared.exception.EntityNotFoundException;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Transactional
@ApplicationScoped
public class MovieService {

    @Inject
    private MovieRepository movieRepository;

    @Inject
    private MovieMapper mapper;

    @Inject
    private PersonMapper personMapper;

    @Inject
    private PersonService personService;

    public Movie create(@Valid MovieRequestDto dto) {
        Movie movie = mapper.toMovie(dto);

        if (dto.getDirectorReference().isNew()) {
            movie.setDirector(personService.createOrGetExisting(dto.getDirectorReference().getValue()));
        }
        if (dto.getOperatorReference().isNew()) {
            movie.setOperator(personService.createOrGetExisting(dto.getOperatorReference().getValue()));
        }
        if (dto.getScreenwriterReference().isNew()) {
            movie.setScreenwriter(personService.createOrGetExisting(dto.getScreenwriterReference().getValue()));
        }

        movie.setCreationDate(ZonedDateTime.now());

        checkCreateUniqueConstraint(movie);
        Movie savedMovie = movieRepository.save(movie);

        return savedMovie;
    }

    public void update(long id, @Valid MovieRequestDto dto) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Movie with id %d not found", id)));
        Movie updatedMovie = mapper.toMovie(dto);
        updatedMovie.setId(id);

        checkUpdateUniqueConstraint(updatedMovie);
        movieRepository.update(movie, (m) -> mapper.toMovie(dto, m));
    }

    private void checkCreateUniqueConstraint(Movie movie) {
        if (movieRepository.existsByNameAndDirectorName(movie.getName(), movie.getDirector().getName())) {
            throw new EntityAlreadyExistsException(
                    String.format("Movie with name '%s' and director '%s' already exists",
                            movie.getName(),
                            movie.getDirector().getName()
                    )
            );
        }
    }

    private void checkUpdateUniqueConstraint(Movie movie) {
        if (movieRepository.existsByNameAndDirectorNameAndIdNot(movie.getName(), movie.getDirector().getName(), movie.getId())) {
            throw new EntityAlreadyExistsException(
                    String.format("Movie with name '%s' and director '%s' already exists",
                            movie.getName(),
                            movie.getDirector().getName()
                    )
            );
        }
    }

    public void delete(long id) {
        movieRepository.deleteById(id);
    }

    public MovieLazyResponseDto lazyGet(@Valid MovieLazyBeanParamDto lazyBeanParamDto) {
        Map<String, Object> filterBy = getFilterBy(lazyBeanParamDto);

        List<Movie> data = movieRepository.load(
                lazyBeanParamDto.getFirst(),
                lazyBeanParamDto.getPageSize(),
                lazyBeanParamDto.getSortField(),
                lazyBeanParamDto.getSortOrder(),
                filterBy
        );
        long totalRecords = movieRepository.count(filterBy);
        return new MovieLazyResponseDto(mapper.toDto(data), totalRecords);
    }

    private Map<String, Object> getFilterBy(@Valid MovieLazyBeanParamDto lazyBeanParamDto) {
        Map<String, Object> filterBy = new HashMap<>();
        if (lazyBeanParamDto.getIdFilter() != null)
            filterBy.put("id", lazyBeanParamDto.getIdFilter());
        if (lazyBeanParamDto.getNameFilter() != null)
            filterBy.put("name", lazyBeanParamDto.getNameFilter());
        if (lazyBeanParamDto.getGenreFilter() != null)
            filterBy.put("genre", lazyBeanParamDto.getGenreFilter());
        if (lazyBeanParamDto.getMpaaRatingFilter() != null)
            filterBy.put("mpaaRating", lazyBeanParamDto.getMpaaRatingFilter());
        if (lazyBeanParamDto.getTaglineFilter() != null)
            filterBy.put("tagline", lazyBeanParamDto.getTaglineFilter());
        return filterBy;
    }

    public MovieCountResponseDto countByTagline(String tagline) {
        Long count = movieRepository.findAll().stream()
                .map(Movie::getTagline)
                .filter(t -> t.equals(tagline))
                .count();
        return new MovieCountResponseDto(count);
    }

    public MovieCountResponseDto countLessThanGoldenPalm(long baseCount) {
        Long count = movieRepository.findAll().stream()
                .map(Movie::getGoldenPalmCount)
                .filter(c -> c < baseCount)
                .count();
        return new MovieCountResponseDto(count);
    }

    public MovieCountResponseDto countGreaterThanGoldenPalm(long baseCount) {
        Long count = movieRepository.findAll().stream()
                .map(Movie::getGoldenPalmCount)
                .filter(c -> c > baseCount)
                .count();
        return new MovieCountResponseDto(count);
    }

    public List<PersonResponseDto> getDirectorsWithoutOscars() {
        List<Movie> movies = movieRepository.findAll();
        List<Person> directors = movies.stream()
                .filter(m -> m.getOscarsCount() == 0)
                .map(Movie::getDirector)
                .filter(d -> movies.stream()
                        .filter(m -> m.getDirector().equals(d))
                        .allMatch(m -> m.getOscarsCount() == 0))
                .distinct()
                .toList();
        return personMapper.toDto(directors);
    }

    public void addOscarToRated() {
        movieRepository.findAll().stream()
                .filter(m -> Objects.equals(m.getMpaaRating(), (MpaaRating.R)))
                .forEach(m -> {
                    movieRepository.update(m, (mv) -> mv.setOscarsCount(m.getOscarsCount() + 1));
                });
    }
}
