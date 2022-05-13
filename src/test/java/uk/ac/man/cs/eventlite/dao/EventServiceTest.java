package uk.ac.man.cs.eventlite.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import twitter4j.Status;
import twitter4j.TwitterException;
import uk.ac.man.cs.eventlite.EventLite;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

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
	public void testEventlistSorting() throws Exception {
		
		Venue VA = new Venue();
		venueService.save(VA);

		Event C = new Event("pingpong1_", VA, LocalDate.of(1000,01,02),
				LocalTime.of(6,30));
		eventService.save(C);
		Event A = new Event("Hunt1_", VA, LocalDate.of(1000,01,01),
				LocalTime.of(0,0));
		eventService.save(A);
		Event B = new Event("match1_", VA, LocalDate.of(1000,01,02),
				LocalTime.of(16,30));
		eventService.save(B);
		Iterable<Event> allEvents = eventService.findAllAndSort();
		ArrayList<Event> eventsList = (ArrayList<Event>) allEvents;
		
		assertEquals(eventsList.get(0).getName(), "Hunt1_");
		assertEquals(eventsList.get(1).getName(), "match1_");
		assertEquals(eventsList.get(2).getName(), "pingpong1_");
	}
	
	@Test
	public void testMaximum5TweetsReturned() throws TwitterException
	{
		List<Status> tweets = eventService.getLastFiveTweetsFromTimeline();
		assertTrue(tweets.size() <= 5 && tweets.size() >= 0);	
		
	}
	
	@Test
	public void testGetRealEvent() {
		Venue VA = new Venue();
		venueService.save(VA);
		Event B = new Event("B", VA, LocalDate.of(1000,01,02),
				LocalTime.of(5,30));
		eventService.save(B);
		Event e = eventService.getEventById(B.getId());
		assertEquals(B, e);
	}
	
	@Test
	public void testGetNullEvent() {
		Venue VA = new Venue();
		venueService.save(VA);
		Event B = new Event("B", VA, LocalDate.of(1000,01,02),
				LocalTime.of(5,30));
		long id = B.getId();
		B.setId(id+1);
		eventService.save(B);
		Event e = eventService.getEventById(B.getId());
		assertEquals(null, e);
	}
	
	@Test
	public void testCount() {
		Venue VA = new Venue();
		venueService.save(VA);
		Event B = new Event("B", VA, LocalDate.of(1000,01,02),
				LocalTime.of(5,30));
		long count_before = eventService.count();
		eventService.save(B);
		assertEquals(eventService.count(),count_before+1);
	}
	
	@Test
	public void testDelete() {
		Venue VA = new Venue();
		venueService.save(VA);
		Event B = new Event("B", VA, LocalDate.of(1000,01,02),
				LocalTime.of(5,30));
		long count_before = eventService.count();
		eventService.save(B);
		assertEquals(eventService.count(),count_before+1);
		eventService.deleteById(B.getId());
		assertEquals(eventService.count(),count_before);
	}
	
	@Test
	public void testShareTweet() throws TwitterException {
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
		String tweet = "Test time: " + timeStamp;
		eventService.shareTweet(tweet);
		assertTrue(true);
	}
	
	
}

