package uk.ac.man.cs.eventlite.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

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

import uk.ac.man.cs.eventlite.entities.Venue;

import twitter4j.TwitterException;
import twitter4j.Status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = EventLite.class)
@DirtiesContext
@ActiveProfiles("test")

public class EventServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private EventService eventService;
	
	@Autowired
	private VenueService venueService;

	@Test
	public void searchEventByName() {
		
		int totalEvents = 6;
		Iterable<Event> eventsFound;
		
		// Make sure there are 6 test events in total
		eventsFound = eventService.findAll();
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

	// This class is here as a starter for testing any custom methods within the
	// EventService. Note: It is currently @Disabled!
	
	@Test
	public void EventFindAllAndSortTest() throws Exception {
		
		Venue VA = new Venue();
		venueService.save(VA);

		Event C = new Event("C", VA, LocalDate.of(1000,01,02),
				LocalTime.of(6,30));
		eventService.save(C);
		Event A = new Event("A", VA, LocalDate.of(1000,01,01),
				LocalTime.of(0,0));
		eventService.save(A);
		Event B = new Event("B", VA, LocalDate.of(1000,01,02),
				LocalTime.of(5,30));
		eventService.save(B);
		Iterable<Event> allEvents = eventService.findAllAndSort();
		ArrayList<Event> eventsList = (ArrayList<Event>) allEvents;
		
		assertEquals(eventsList.get(0).getName(), "A");
		assertEquals(eventsList.get(1).getName(), "B");
		assertEquals(eventsList.get(2).getName(), "C");
	}
	
	@Test
	public void testMaximum5TweetsReturned()
	{
		try {
			List<Status> tweets = eventService.getLastFiveTweetsFromTimeline();
			assertTrue(tweets.size() <= 5 && tweets.size() >= 0);	
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}
	
}

