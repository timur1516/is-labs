package ru.itmo.se.is.feature.person.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import ru.itmo.se.is.feature.person.api.dto.PersonLazyBeanParamDto;
import ru.itmo.se.is.feature.person.api.dto.PersonLazyResponseDto;
import ru.itmo.se.is.feature.person.api.dto.PersonRequestDto;
import ru.itmo.se.is.feature.person.domain.Person;
import ru.itmo.se.is.feature.person.domain.PersonRepository;
import ru.itmo.se.is.feature.person.infrastructure.mapper.PersonMapper;
import ru.itmo.se.is.platform.db.eclipselink.tx.annotation.Transactional;
import ru.itmo.se.is.shared.exception.EntityAlreadyExistsException;
import ru.itmo.se.is.shared.exception.EntityNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@ApplicationScoped
public class PersonService {
    @Inject
    private PersonRepository personRepository;

    @Inject
    private PersonMapper mapper;

    public Person create(@Valid PersonRequestDto dto) {
        Person person = mapper.toPerson(dto);

        checkCreateUniqueConstraint(person);
        Person savedPerson = personRepository.save(person);

        return savedPerson;
    }

    public Person createOrGetExisting(@Valid PersonRequestDto dto) {
        return personRepository.findByName(dto.getName())
                .orElseGet(() -> {
                    Person person = mapper.toPerson(dto);
                    Person savedPerson = personRepository.save(person);
                    return savedPerson;
                });
    }

    public void update(long id, @Valid PersonRequestDto dto) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Person with id %d not found", id)));
        Person updatedPerson = mapper.toPerson(dto);
        updatedPerson.setId(id);

        checkUpdateUniqueConstraint(updatedPerson);
        personRepository.update(person, (p) -> mapper.toPerson(dto, p));
    }

    private void checkCreateUniqueConstraint(Person person) {
        if (personRepository.existsByName(person.getName())) {
            throw new EntityAlreadyExistsException(
                    String.format("Person with name %s already exists", person.getName())
            );
        }
    }

    private void checkUpdateUniqueConstraint(Person person) {
        if (personRepository.existsByNameAndIdNot(person.getName(), person.getId())) {
            throw new EntityAlreadyExistsException(
                    String.format("Person with name %s already exists", person.getName())
            );
        }
    }

    public void delete(long id) {
        personRepository.deleteById(id);
    }

    public PersonLazyResponseDto lazyGet(@Valid PersonLazyBeanParamDto lazyBeanParamDto) {
        Map<String, Object> filterBy = getFilterBy(lazyBeanParamDto);

        List<Person> data = personRepository.load(
                lazyBeanParamDto.getFirst(),
                lazyBeanParamDto.getPageSize(),
                lazyBeanParamDto.getSortField(),
                lazyBeanParamDto.getSortOrder(),
                filterBy
        );
        long totalRecords = personRepository.count(filterBy);
        return new PersonLazyResponseDto(mapper.toDto(data), totalRecords);
    }

    private Map<String, Object> getFilterBy(@Valid PersonLazyBeanParamDto lazyBeanParamDto) {
        Map<String, Object> filterBy = new HashMap<>();
        if (lazyBeanParamDto.getNameFilter() != null)
            filterBy.put("name", lazyBeanParamDto.getNameFilter());
        if (lazyBeanParamDto.getEyeColorFilter() != null)
            filterBy.put("eyeColor", lazyBeanParamDto.getEyeColorFilter());
        if (lazyBeanParamDto.getHairColorFilter() != null)
            filterBy.put("hairColor", lazyBeanParamDto.getHairColorFilter());
        if (lazyBeanParamDto.getNationalityFilter() != null)
            filterBy.put("nationality", lazyBeanParamDto.getNationalityFilter());
        return filterBy;
    }

    public Person getById(long id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Person with id %d not found ", id)));
    }
}
