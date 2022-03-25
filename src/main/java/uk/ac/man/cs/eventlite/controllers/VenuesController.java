package uk.ac.man.cs.eventlite.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;
import uk.ac.man.cs.eventlite.exceptions.EventNotFoundException;
import uk.ac.man.cs.eventlite.exceptions.VenueNotFoundException;
import uk.ac.man.cs.eventlite.entities.Event;

@Controller
@RequestMapping(value = "/venues", produces = { MediaType.TEXT_HTML_VALUE })
public class VenuesController {

	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;

	@ExceptionHandler(EventNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String venueNotFoundHandler(VenueNotFoundException ex, Model model) {
		model.addAttribute("not_found_id", ex.getId());

		return "venues/not_found";
	}

	@GetMapping("/{id}")
	public String getEvent(@PathVariable("id") long id, Model model) {
		throw new EventNotFoundException(id);
	}

	@GetMapping
	public String getAllEvents(Model model) {
		List<Event> upcoming = new ArrayList<Event>();
		List<Event> previous = new ArrayList<Event>();
		
		for(Event event: eventService.findAllAndSort()) {
			if (event.getDate().isAfter(LocalDate.now())) {
				upcoming.add(event);
			} else {
				previous.add(event);
			}
		}
		
		// Reorder the previous ones descending
		Collections.sort(previous, (a, b)-> b.getDate().compareTo(a.getDate()));

		model.addAttribute("upcomingEvents", upcoming);
		model.addAttribute("previousEvents", previous);

//		model.addAttribute("events", eventService.findAllAndSort());
//		model.addAttribute("venues", venueService.findAll());

		return "events/index";
	}
	
	@GetMapping("update/{id}")
	public String getEventUpdate(@PathVariable("id") long id, Model model) {
		Event e = eventService.getEventById(id);
		model.addAttribute("event", e);
		model.addAttribute("allVenues", venueService.findAll());
		return "events/update";
	}

	@GetMapping("/details/{id}")
	public String getVenueDetails(@PathVariable("id") long id, Model model) {
		Venue v = venueService.getVenueById(id);
		if(v == null) throw new EventNotFoundException(id);
		
		List<Event> upcoming = new ArrayList<Event>();
				
		for(Event event: eventService.findAllAndSort()) {
			if (event.getVenue() == v && event.getDate().isAfter(LocalDate.now())) 
				upcoming.add(event);
		}
		
		model.addAttribute("venue", v);
		model.addAttribute("upcomingEvents", upcoming);
		
		return "venues/details/index";
	}
	
	 @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	 public String deleteById(@PathVariable("id") long id) {

		eventService.deleteById(id);

		return "redirect:/events";
	 } 
	 
	 @RequestMapping(value="/update/{id}", method= RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	 public String updateEvent(@PathVariable("id") Long id, Event event) {
		 Event e = eventService.getEventById(id);
		 e.setName(event.getName());
		 e.setDate(event.getDate());
		 e.setTime(event.getTime());
		 e.setVenue(event.getVenue());
		 e.setSummary(e.getName() + " | " + e.getVenue().getName() + " | " + e.getDate().toString());
		 e.setDescription(event.getDescription());
		 eventService.save(e);
		 return "redirect:/events";
	}
	
	@RequestMapping(value = "/newEvent", method = RequestMethod.GET)
	public String newEvent(Model model) {
		if (!model.containsAttribute("event")) {
			model.addAttribute("event", new Event());
		}

		if (!model.containsAttribute("venueList")) {
			model.addAttribute("venueList", venueService.findAll());
		}

		return "events/newEvent";
	}
	
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String createEvent(@RequestBody @Valid @ModelAttribute Event event, 
			BindingResult errors, Model model, RedirectAttributes redirectAttrs) {

		if (errors.hasErrors()) {
			model.addAttribute("event", event);
			model.addAttribute("venueList", venueService.findAll());

			return "events/new";
		}

		eventService.save(event);
		redirectAttrs.addFlashAttribute("ok_message", "New event added.");

		return "redirect:/events";
	}
	
	@GetMapping(value="/search")
	public String searchEventsByName(Model model, @Param("keyWords") String keyWords) {
		List<Event> upcoming = new ArrayList<Event>();
		List<Event> previous = new ArrayList<Event>();
		
		for(Event event: eventService.search(keyWords)) {
			if (event.getDate().isAfter(LocalDate.now())) {
				upcoming.add(event);
			} else {
				previous.add(event);
			}
		}
		
		// Reorder the previous ones descending
		Collections.sort(previous, (a, b)-> b.getDate().compareTo(a.getDate()));

		model.addAttribute("eventsFoundUpcoming", upcoming);
		model.addAttribute("eventsFoundPrevious", previous);

		return "events/search";
	}
	
}
