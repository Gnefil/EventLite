package uk.ac.man.cs.eventlite.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import uk.ac.man.cs.eventlite.controllers.VenuesControllerApi;
import uk.ac.man.cs.eventlite.entities.Venue;

@Component
public class VenueModelAssembler implements RepresentationModelAssembler<Venue, EntityModel<Venue>> {

	@Override
	public EntityModel<Venue> toModel(Venue venue) {
		
		Long id = venue.getId();
		
		Link selfLink = linkTo(methodOn(VenuesControllerApi.class).getVenue(id)).withSelfRel();
		Link venueLink = linkTo(methodOn(VenuesControllerApi.class).getVenue(id)).withRel("venue");
		Link eventsLink = linkTo(methodOn(VenuesControllerApi.class).getVenue(id)).slash("events").withRel("events");
		Link next3eventsLink = linkTo(methodOn(VenuesControllerApi.class).getVenue(id)).slash("next3events").withRel("next3events");
		
		
		return EntityModel.of(venue, selfLink, venueLink, eventsLink, next3eventsLink);
	}
}
