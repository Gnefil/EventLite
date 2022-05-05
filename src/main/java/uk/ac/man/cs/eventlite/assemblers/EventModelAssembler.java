package uk.ac.man.cs.eventlite.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import uk.ac.man.cs.eventlite.controllers.EventsControllerApi;
import uk.ac.man.cs.eventlite.controllers.VenuesControllerApi;
import uk.ac.man.cs.eventlite.entities.Event;

@Component
public class EventModelAssembler implements RepresentationModelAssembler<Event, EntityModel<Event>> {

	@Override
	public EntityModel<Event> toModel(Event event) {
		Link eventLink = linkTo(methodOn(EventsControllerApi.class).getEvent(event.getId())).withSelfRel();
		Link allEvents = linkTo(methodOn(EventsControllerApi.class).getEvent(event.getId())).withSelfRel();
		Link venueLink = linkTo(methodOn(EventsControllerApi.class).getEvent(event.getId())).slash("venue").withRel("venue");
		return EntityModel.of(event, eventLink, allEvents, venueLink);
	}
}
