package uk.ac.man.cs.eventlite.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
	public String getEventsAndVenuesForHomePage(Model model) {
		List<Event> upcomingEvents = new ArrayList<Event>();
		List<Venue> venues = new ArrayList<Venue>();
		
		List<Event> events = ((List<Event>) eventService.findAll()).stream()
				.filter(event -> event.getDate().isAfter(LocalDate.now()))
				.collect(Collectors.toList());
		Collections.sort(events, (a, b)-> a.getDate().compareTo(b.getDate()));
		int len = events.size() < 3 ? events.size() : 3;
		upcomingEvents = events.subList(0, len);

		venues = venueService.findThreeVenuesWithMostEvents();
		
		model.addAttribute("upcomingEvents", upcomingEvents);
		model.addAttribute("venues", venues);

		return "../static/index";
	}
}
