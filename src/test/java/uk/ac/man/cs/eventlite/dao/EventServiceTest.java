package uk.ac.man.cs.eventlite.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import uk.ac.man.cs.eventlite.EventLite;
import uk.ac.man.cs.eventlite.entities.Event;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = EventLite.class)
@DirtiesContext
@ActiveProfiles("test")

public class EventServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private EventService eventService;

	@Test
	public void searchEventByName() {
		
		int totalEvents = 6;
		Iterable<Event> eventsFound, allEvents;
		
		// Make sure there are 6 test events in total
		eventsFound = eventService.findAll();
		allEvents = eventService.findAll();
		assertEquals(((Collection<Event>) eventsFound).size(), totalEvents);
		
		// Should get all with Event keyword
		eventsFound = eventService.search("Event");
		assertEquals(((Collection<Event>) eventsFound).size(), totalEvents);
		
		// Should get one record if unique
		eventsFound = eventService.search("alpha");
		assertEquals(((Collection<Event>) eventsFound).size(), 1);
		
		// Should not be case sensitive
		eventsFound = eventService.search("ALPHA");
		assertEquals(((Collection<Event>) eventsFound).size(), 1);
		
		// Should return zero if no match
		eventsFound = eventService.search("random");
		assertEquals(((Collection<Event>) eventsFound).size(), 0);
		
	}	
	
	
}
