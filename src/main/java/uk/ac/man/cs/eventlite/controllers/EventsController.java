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

import twitter4j.TwitterException;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.exceptions.EventNotFoundException;

import uk.ac.man.cs.eventlite.entities.Event;

@Controller
@RequestMapping(value = "/events", produces = { MediaType.TEXT_HTML_VALUE })
public class EventsController {

	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;
	
	String MAPBOX_ACCESS_TOKEN = "pk.eyJ1IjoiYXNvbGkiLCJhIjoiY2wxYWl3NzUyMXk3bTNpc2d4a3BrYmlpMiJ9.T1Lq2KsPQlAuWIAhg1Lh2g";


	@ExceptionHandler(EventNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String eventNotFoundHandler(EventNotFoundException ex, Model model) {
		model.addAttribute("not_found_id", ex.getId());

		return "events/not_found";
	}

	@GetMapping("/{id}")
	public String getEvent(@PathVariable("id") long id, Model model) {
		throw new EventNotFoundException(id);
	}

	@GetMapping
	public String getAllEvents(Model model) throws TwitterException {
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
		model.addAttribute("lastFiveTweets", eventService.getLastFiveTweetsFromTimeline());

		return "events/index";
	}
	
	@GetMapping("update/{id}")
	public String getEventUpdate(Model model, @PathVariable("id") Long id, RedirectAttributes redirectAttrs) {
		Event e = eventService.getEventById(id);
		if (e == null) {
	 		redirectAttrs.addFlashAttribute("error_message", "event not found");
	 	}
	 	
		model.addAttribute("event", e);
		model.addAttribute("allVenues", venueService.findAll());
		
        return "events/update";
	}

	@GetMapping("/details/{id}")
	public String getEventsDetails(@PathVariable("id") long id, Model model) {
		Event e = eventService.getEventById(id);
		
		if(e == null) throw new EventNotFoundException(id);
		
		model.addAttribute("event", e);
		model.addAttribute("latit", e.getVenue().getLatitude());
		model.addAttribute("longtit", e.getVenue().getLongitude());
		
		return "events/details/index";
	}
	
	 @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	 public String deleteById(@PathVariable("id") long id) {

		eventService.deleteById(id);

		return "redirect:/events";
	 } 
	 
	 @RequestMapping(value="/update/{id}", method= RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	 public String updateEvent(@RequestBody @Valid @ModelAttribute Event event, BindingResult errors, Model model, @PathVariable("id") long id, RedirectAttributes redirectAttrs ) {
		 Event e = eventService.getEventById(id);	

		 if (errors.hasErrors()) {
			 model.addAttribute("event", event);
			 model.addAttribute("allVenues", venueService.findAll());	
			 return "events/update";
		 }
		 e.setName(event.getName());
		 e.setDate(event.getDate());
		 e.setTime(event.getTime());
		 e.setVenue(event.getVenue());
		 e.setSummary(event.getSummary());
		 e.setDescription(event.getDescription());	
		 eventService.save(e);
		 redirectAttrs.addFlashAttribute("ok_message", "Event updated.");	
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
			model.addAttribute("events", event);
			model.addAttribute("venues", venueService.findAll());
			return "events/newEvent";
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
	
	@RequestMapping(value="/tweet/{id}", method= RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String updateStatusOnTwitter(@PathVariable("id") Long id, String tweet, RedirectAttributes redirectAttrs) {
		try {
			eventService.shareTweet(tweet);
			redirectAttrs.addFlashAttribute("response",tweet);
		} catch (TwitterException e) {
			e.printStackTrace();
		}

		return "redirect:/events/details/{id}";
	}
	
}
