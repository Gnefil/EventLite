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
import uk.ac.man.cs.eventlite.dao.EventRepository;
@Controller
@RequestMapping(value = "/venues", produces = { MediaType.TEXT_HTML_VALUE })
public class VenuesController {

	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;
	
	String MAPBOX_ACCESS_TOKEN = "pk.eyJ1IjoiYXNvbGkiLCJhIjoiY2wxYWl3NzUyMXk3bTNpc2d4a3BrYmlpMiJ9.T1Lq2KsPQlAuWIAhg1Lh2g";
	
	@RequestMapping(method = RequestMethod.GET)
	public String getAllVenues(Model model) {

		model.addAttribute("venues", venueService.findAllAndSort());
		return "venues/index";
	}

	@GetMapping("update/{id}")
	public String getVenueUpdate(@PathVariable("id") long id, Model model, RedirectAttributes redirectAttrs) {
		Venue v = venueService.getVenueById(id);
		if (v == null) {
			model.addAttribute("not_found_id", id);
			return "venues/not_found";
	 	}
	 	
		model.addAttribute("venue", v);
		
		model.addAttribute("allVenues", venueService.findAll());
		return "venues/update";
	}

	@GetMapping("/details/{id}")
	public String getVenueDetails(@PathVariable("id") long id, Model model) {
		Venue v = venueService.getVenueById(id);
		if(v == null) {
			model.addAttribute("not_found_id", id);
			return "venues/not_found";
	 	}
		
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
		 boolean isVenueEmpty = true;
		 
		 Iterable<Event> events = eventService.findAll();
		 
		 for(Event event : events) {
			 Venue venue = event.getVenue();
			 
			 if(venue.getId() == id) {
				 isVenueEmpty = false;
			 }
		 }
		 
		 if(isVenueEmpty) {
			 venueService.deleteById(id);
		 }
		return "redirect:/venues";
	 } 
	 
	 @RequestMapping(value="/update/{id}", method= RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	 public String updateVenue(@RequestBody @Valid @ModelAttribute Venue venue, BindingResult errors, Model model, @PathVariable("id") long id, RedirectAttributes redirectAttrs) {
		 if (errors.hasErrors()) {
			 model.addAttribute("venue", venue);
			 return "venues/update";
		 }
		 Iterable<Event> events = eventService.findAll();
		 
		 Venue v = venueService.getVenueById(id);
		 v.setName(venue.getName());
		 v.setCapacity(venue.getCapacity());
		 v.setRoadName(venue.getRoadName());
		 v.setPostcode(venue.getPostcode());
		 
		 for(Event event : events) {
			 Venue ven = event.getVenue();
			 
			 if(ven.getId() == id) {
				 event.setVenue(v);
				 String summ = event.getName() + " | " + v.getName() + " | " + event.getDate().toString();
				 event.setSummary(summ);
				 redirectAttrs.addFlashAttribute("ok_message", "updated events with venue");
			 }
		 }
		 
		 
		 venueService.save(v);
		 return "redirect:/venues";
	}
	
	
	@GetMapping(value="/search")
	public String searchVenuesByName(Model model, @Param("keyWords") String keyWords) {

		model.addAttribute("venuesFound", venueService.search(keyWords));

		return "venues/search";
	}
	
	@RequestMapping(value = "/newVenue", method = RequestMethod.GET)
	public String newVenue(Model model) {
		if (!model.containsAttribute("venue")) {
			model.addAttribute("venue", new Venue());
		}

		return "venues/newVenue";
	}
	
	@RequestMapping(value="/newVenue", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String createVenue(@RequestBody @Valid @ModelAttribute Venue venue, 
			BindingResult errors, Model model, RedirectAttributes redirectAttrs) {

		if (errors.hasErrors()) {
			model.addAttribute("venue", venue);
			
			return "venues/newVenue";
		}
		
		venue = venueService.geocode(venue, MAPBOX_ACCESS_TOKEN);
		
		venueService.save(venue);
		redirectAttrs.addFlashAttribute("ok_message", "New venue added.");	
		
		return "redirect:/venues";
	}
}
