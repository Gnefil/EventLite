package uk.ac.man.cs.eventlite.dao;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
		List<Event> events1 = (List<Event>) eventRepository.findAll();
		Collections.sort(events1,new Comparator<Event>(){
            public int compare(Event e1, Event e2){
            	return e1.getDate().compareTo(e2.getDate());
            }});
		return events1;
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
