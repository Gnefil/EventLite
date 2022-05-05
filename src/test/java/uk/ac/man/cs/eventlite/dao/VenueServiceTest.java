package uk.ac.man.cs.eventlite.dao;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import uk.ac.man.cs.eventlite.EventLite;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;
import uk.ac.man.cs.eventlite.entities.Venue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = EventLite.class)
@DirtiesContext
@ActiveProfiles("test")

public class VenueServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private VenueService venueService;

	@Test
	public void searchVenueByName() {
		
		int totalVenues = 3;
		Iterable<Venue> venuesFound;
		
		// Make sure there are 6 test events in total
		venuesFound = venueService.findAll();
		assertEquals(((Collection<Venue>) venuesFound).size(), totalVenues);
		
		// Should get all with Event keyword
		venuesFound = venueService.search("Venue");
		assertEquals(((Collection<Venue>) venuesFound).size(), totalVenues);
		
		// Should get one record if unique
		venuesFound = venueService.search("B");
		assertEquals(((Collection<Venue>) venuesFound).size(), 1);
		
		// Should not be case sensitive
		venuesFound = venueService.search("b");
		assertEquals(((Collection<Venue>) venuesFound).size(), 1);
		
		// Should return zero if no match
		venuesFound = venueService.search("random");
		assertEquals(((Collection<Venue>) venuesFound).size(), 0);
		
	}	
	
	@Test
	public void venueFindAllAndSortTest() throws Exception {
		Venue B = new Venue("B", "23 Manchester Road", "E14 3BD", 50);
		venueService.save(B);
		Venue A = new Venue("A", "23 Manchester Road", "E14 3BD", 50);
		venueService.save(A);
		Iterable<Venue> allVenues = venueService.findAllAndSort();
		ArrayList<Venue> venuesList = (ArrayList<Venue>) allVenues;
		assertEquals(venuesList.get(0).getName(), "A");
		assertEquals(venuesList.get(1).getName(), "B");
	}
	
	@Test
	public void findEventsFromVenue() throws Exception {
		Venue A = new Venue("A", "23 Manchester Road", "E14 3BD", 50);
		A.setId(0);
		venueService.save(A);

		Event e1 = new Event("Event 1", A, LocalDate.now());
		Event e2 = new Event("Event 2", A, LocalDate.now());
		Event e3 = new Event("Event 3", A, LocalDate.now());
		Event e4 = new Event("Event 4", A, LocalDate.now());
		
		List<Event> events = venueService.getEventsFromVenue(0);
		
		for (Event event: events) {
			assertEquals(event.getVenue().getId(), 0);
		}

	}
	
	@Test
	public void findEventsFromVenueWhen0Events() throws Exception {
		Venue A = new Venue("A", "23 Manchester Road", "E14 3BD", 50);
		A.setId(0);
		venueService.save(A);
		
		List<Event> events = venueService.getEventsFromVenue(0);
		
		assertEquals(events.size(), 0);
	}
	

}
