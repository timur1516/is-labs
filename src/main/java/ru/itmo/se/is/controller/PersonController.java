package ru.itmo.se.is.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import ru.itmo.se.is.dto.person.PersonLazyBeanParamDto;
import ru.itmo.se.is.dto.person.PersonRequestDto;
import ru.itmo.se.is.dto.person.PersonResponseDto;
import ru.itmo.se.is.service.NotificationService;
import ru.itmo.se.is.service.PersonService;
import ru.itmo.se.is.websocket.WebSocketMessageType;

import java.net.URI;

@Path("/people")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PersonController {

    @Inject
    private PersonService service;
    @Inject
    private NotificationService notificationService;

    @GET
    public Response getAllPeople(@Valid @BeanParam PersonLazyBeanParamDto lazyBeanParamDto) {
        return Response.ok(service.lazyGet(lazyBeanParamDto)).build();
    }

    @POST
    public Response createPerson(@Context UriInfo uriInfo, @Valid PersonRequestDto dto) {
        PersonResponseDto createdPerson = service.create(dto);

        URI location = uriInfo.getAbsolutePathBuilder()
                .path("{id}")
                .resolveTemplate("id", createdPerson.getId())
                .build();
        notificationService.notifyAll(WebSocketMessageType.PERSON);
        return Response.created(location).entity(createdPerson).build();
    }

    @PATCH
    @Path("/{id}")
    public Response updatePerson(@PathParam("id") long id, @Valid PersonRequestDto dto) {
        service.update(id, dto);
        notificationService.notifyAll(WebSocketMessageType.PERSON);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}")
    public Response deletePerson(@PathParam("id") long id) {
        service.delete(id);
        notificationService.notifyAll(WebSocketMessageType.PERSON);
        return Response.noContent().build();
    }
}
