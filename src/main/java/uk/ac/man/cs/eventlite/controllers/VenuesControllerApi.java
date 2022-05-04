package uk.ac.man.cs.eventlite.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import uk.ac.man.cs.eventlite.assemblers.EventModelAssembler;
import uk.ac.man.cs.eventlite.assemblers.VenueModelAssembler;
import uk.ac.man.cs.eventlite.config.Hateoas;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;
import uk.ac.man.cs.eventlite.exceptions.EventNotFoundException;
import uk.ac.man.cs.eventlite.exceptions.VenueNotFoundException;

@RestController
@RequestMapping(value = "/api/venues", produces = { MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE })
public class VenuesControllerApi {

	private static final String NOT_FOUND_MSG = "{ \"error\": \"%s\", \"id\": %d }\n";

	@Autowired
	private VenueService venueService;
	
	@Autowired
	private VenueModelAssembler venueAssembler;
	
	@Autowired
	private EventModelAssembler eventAssembler;

	@ExceptionHandler(VenueNotFoundException.class)
	public ResponseEntity<?> venueNotFoundHandler(VenueNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(String.format(NOT_FOUND_MSG, ex.getMessage(), ex.getId()));
	}
	
	@GetMapping
	public CollectionModel<EntityModel<Venue>> getAllVenues() {
		Link selfLink = linkTo(methodOn(VenuesControllerApi.class).getAllVenues()).withSelfRel();
		Link profileLink = linkTo(Hateoas.class).slash("api").slash("profile").slash("venues").withRel("profile");
		
		return venueAssembler.toCollectionModel(venueService.findAll())
				.add(selfLink)
				.add(profileLink);
	}
	
	@GetMapping("/{id}")
	public EntityModel<Venue> getVenue(@PathVariable("id") long id) {
		
		// If not such venue id
		if (venueService.getVenueById(id) == null) throw new VenueNotFoundException(id);
		
		return venueAssembler.toModel(venueService.getVenueById(id));
	}
	
	@GetMapping("/{id}/events")
	public CollectionModel<EntityModel<Event>> getEventsFromVenue(@PathVariable("id") long venueId){
		Link selfLink = linkTo(methodOn(VenuesControllerApi.class).getEventsFromVenue(venueId)).withSelfRel();
		
		return eventAssembler.toCollectionModel(venueService.getEventsFromVenue(venueId))
				.add(selfLink);
	}
	
	@GetMapping("/{id}/next3events")
	public CollectionModel<EntityModel<Event>> getNext3EventsFromVenue(@PathVariable("id") long venueId){

		Link selfLink = linkTo(methodOn(VenuesControllerApi.class).getNext3EventsFromVenue(venueId)).withSelfRel();
		
		return eventAssembler.toCollectionModel(venueService.getNext3EventsFromVenue(venueId))
				.add(selfLink);
	}

}
