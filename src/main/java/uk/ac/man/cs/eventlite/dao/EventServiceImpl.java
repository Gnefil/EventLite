package uk.ac.man.cs.eventlite.dao;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import uk.ac.man.cs.eventlite.entities.Event;

@Service
public class EventServiceImpl implements EventService{

	private final static Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

	private final static String DATA = "data/events.json";

	@Autowired
	private EventRepository eventRepository;
	
	@Override
	public long count() {
		return eventRepository.count();
	}

	@Override
	public Iterable<Event> findAll() {
		return eventRepository.findAll();
	}
	
	@Override
	public Iterable<Event> findAllAndSort() {
		ArrayList<Event> events1 =(ArrayList<Event>) eventRepository.findAll();
		Event[] events2 = (Event[]) events1.toArray();
		Arrays.sort(events2, (a,b) -> (a.getDate().compareTo(b.getDate()) == 0) ? a.getTime().compareTo(b.getTime()) : a.getDate().compareTo(b.getDate()));
		Iterable<Event> events = Arrays.asList(events2);
		return events;
	}
	
	@Override
	public Event getEventById(long id) {
		return eventRepository.findById(id).orElse(null);
	}
	
	@Override
	public Event save(Event event) {
		return eventRepository.save(event);
	}
	
	@Override
	public Iterable<Event> search(String keyWords) {
		
		// Format the key words input
		String lowerCaseWords = keyWords.toLowerCase().trim();
		
		return eventRepository.search(lowerCaseWords);
	}
	
	@Override
	public void deleteById(long id) {
		eventRepository.deleteById(id);
	}
}
