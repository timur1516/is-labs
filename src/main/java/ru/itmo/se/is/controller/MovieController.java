package ru.itmo.se.is.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import ru.itmo.se.is.dto.movie.MovieLazyBeanParamDto;
import ru.itmo.se.is.dto.movie.MovieRequestDto;
import ru.itmo.se.is.dto.movie.MovieResponseDto;
import ru.itmo.se.is.service.MovieService;
import ru.itmo.se.is.service.NotificationService;
import ru.itmo.se.is.websocket.WebSocketMessageType;

import java.net.URI;

@Path("/movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MovieController {

    @Inject
    private MovieService service;
    @Inject
    private NotificationService notificationService;

    @GET
    public Response getAllMovies(@Valid @BeanParam MovieLazyBeanParamDto lazyBeanParamDto) {
        return Response.ok(service.lazyGet(lazyBeanParamDto)).build();
    }

    @POST
    public Response createMovie(@Context UriInfo uriInfo, @Valid MovieRequestDto dto) {
        MovieResponseDto createdMovie = service.create(dto);

        URI location = uriInfo.getAbsolutePathBuilder()
                .path("{id}")
                .resolveTemplate("id", createdMovie.getId())
                .build();

        notificationService.notifyAll(WebSocketMessageType.MOVIE);

        return Response.created(location).entity(createdMovie).build();
    }

    @PATCH
    @Path("/{id}")
    public Response updateMovie(@PathParam("id") long id, @Valid MovieRequestDto dto) {
        service.update(id, dto);
        notificationService.notifyAll(WebSocketMessageType.MOVIE);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteMovie(@PathParam("id") long id) {
        service.delete(id);
        notificationService.notifyAll(WebSocketMessageType.MOVIE);
        return Response.noContent().build();
    }

    @GET
    @Path("/count-by-tagline/{tagline}")
    public Response countByTagline(@PathParam("tagline") String tagline) {
        return Response.ok(service.countByTagline(tagline)).build();
    }

    @GET
    @Path("/count-less-than-golden-palm/{count}")
    public Response countLessThanGoldenPalm(@PathParam("count") long count) {
        return Response.ok(service.countLessThanGoldenPalm(count)).build();
    }

    @GET
    @Path("/count-greater-than-golden-palm/{count}")
    public Response countGreaterThanGoldenPalm(@PathParam("count") long count) {
        return Response.ok(service.countGreaterThanGoldenPalm(count)).build();
    }

    @GET
    @Path("/directors-without-oscars")
    public Response getDirectorsWithoutOscars() {
        return Response.ok(service.getDirectorsWithoutOscars()).build();
    }

    @PATCH
    @Path("/add-oscar-to-r-rated")
    public Response addOscarToRated() {
        service.addOscarToRated();
        notificationService.notifyAll(WebSocketMessageType.MOVIE);
        return Response.noContent().build();
    }
}
