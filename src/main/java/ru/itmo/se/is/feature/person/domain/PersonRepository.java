package ru.itmo.se.is.feature.person.domain;

import ru.itmo.se.is.shared.db.PagingAndSortingRepository;

import java.util.Optional;

public interface PersonRepository extends PagingAndSortingRepository<Person, Long> {
    boolean existsByName(String name);

    Optional<Person> findByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
