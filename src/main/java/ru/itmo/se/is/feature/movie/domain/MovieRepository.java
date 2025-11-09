package ru.itmo.se.is.feature.movie.domain;

import ru.itmo.se.is.shared.db.PagingAndSortingRepository;

public interface MovieRepository extends PagingAndSortingRepository<Movie, Long> {
    boolean existsByNameAndDirectorName(String name, String directorName);

    boolean existsByNameAndDirectorNameAndIdNot(String name, String name1, Long id);
}
