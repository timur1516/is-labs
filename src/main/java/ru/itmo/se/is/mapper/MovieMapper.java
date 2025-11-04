package ru.itmo.se.is.mapper;

import jakarta.inject.Inject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.itmo.se.is.config.MapperConfig;
import ru.itmo.se.is.dto.EmbeddedObjectDto;
import ru.itmo.se.is.dto.movie.MovieRequestDto;
import ru.itmo.se.is.dto.movie.MovieResponseDto;
import ru.itmo.se.is.dto.person.PersonRequestDto;
import ru.itmo.se.is.entity.Movie;
import ru.itmo.se.is.entity.Person;
import ru.itmo.se.is.service.PersonService;

import java.util.List;

@Mapper(config = MapperConfig.class)
public abstract class MovieMapper {

    @Inject
    private PersonService personService;

    @Inject
    private PersonMapper personMapper;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "director", source = "directorReference")
    @Mapping(target = "screenwriter", source = "screenwriterReference")
    @Mapping(target = "operator", source = "operatorReference")
    public abstract Movie toMovie(MovieRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "director", source = "directorReference")
    @Mapping(target = "screenwriter", source = "screenwriterReference")
    @Mapping(target = "operator", source = "operatorReference")
    public abstract void toMovie(MovieRequestDto dto, @MappingTarget Movie movie);

    public abstract MovieResponseDto toDto(Movie movie);

    public abstract List<MovieResponseDto> toDto(List<Movie> movies);

    public Person map(EmbeddedObjectDto<Long, PersonRequestDto> personReference) {
        if (personReference == null || personReference.isEmpty()) {
            return null;
        }
        if(personReference.isReference()) {
            return personService.getById(personReference.getId());
        }
        return personMapper.toPerson(personReference.getValue());
    }
}
