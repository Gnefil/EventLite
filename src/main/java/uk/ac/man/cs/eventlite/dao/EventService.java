package uk.ac.man.cs.eventlite.dao;

import java.util.ArrayList;
import java.util.Arrays;

import uk.ac.man.cs.eventlite.entities.Event;

public interface EventService {

	public long count();

	public Iterable<Event> findAll();
	
	public Event save(Event event);

	public Iterable<Event> findAllAndSort();
	
	public Iterable<Event> search(String keyWords);
	
	public Event getEventById(long id);

	public void deleteById(long id);
}
