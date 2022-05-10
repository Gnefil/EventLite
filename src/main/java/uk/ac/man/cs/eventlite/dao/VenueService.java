package uk.ac.man.cs.eventlite.dao;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

public interface VenueService {

	public long count();

	public Iterable<Venue> findAll();
	
	public Venue save(Venue venue);
	
	public Venue getVenueById(long id);
	
	public Iterable<Venue> search(String keyWords);
	
	public void deleteById(long id);
	
	Iterable<Venue> findAllAndSort();

	public Venue geocode(Venue venue, String token);
	
	public List<Event> getEventsFromVenue(long venueId);
	
	public List<Event> getNext3EventsFromVenue(long venueId);
	
	public List<Venue> findThreeVenuesWithMostEvents();
	
	public Map<Venue, Integer> sortByValue(Map<Venue, Integer> unsortMap);
}
