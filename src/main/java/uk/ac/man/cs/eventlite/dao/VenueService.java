package uk.ac.man.cs.eventlite.dao;

import java.util.List;

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
	
	List<Venue> findThreeVenuesWithMostEvents();
}
