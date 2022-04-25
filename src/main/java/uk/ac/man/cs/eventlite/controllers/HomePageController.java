package uk.ac.man.cs.eventlite.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@Controller
@RequestMapping(value = "/", produces = { MediaType.TEXT_HTML_VALUE })
public class HomePageController {
	
	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;
	
	@GetMapping
	public String getAllEvents(Model model) {
		List<Event> upcomingEvents = new ArrayList<Event>();
		List<Venue> venues = new ArrayList<Venue>();
		
		for(Event event: eventService.findAllAndSort()) {
			if (event.getDate().isAfter(LocalDate.now())) {
				upcomingEvents.add(event);
			}
			if (upcomingEvents.size() == 3) break;
		}
		
		for(Venue venue: venueService.findAll()) {
			venues.add(venue);
		}
		
		Collections.sort(upcomingEvents, (a, b)-> a.getDate().compareTo(b.getDate()));
		model.addAttribute("upcomingEvents", upcomingEvents);
		model.addAttribute("venues", venues);

		return "../static/index";
	}
}
