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
	
	// This class is here as a starter for testing any custom methods within the
	// VenueService. Note: It is currently @Disabled!
	
	@Test
	public void VenueFindAllAndSortTest() throws Exception {
		Venue B = new Venue("B", "23 Manchester Road", "E14 3BD", 50);
		venueService.save(B);
		Venue A = new Venue("A", "23 Manchester Road", "E14 3BD", 50);
		venueService.save(A);
		Iterable<Venue> allVenues = venueService.findAllAndSort();
		ArrayList<Venue> venuesList = (ArrayList<Venue>) allVenues;
		assertEquals(venuesList.get(0).getName(), "A");
		assertEquals(venuesList.get(1).getName(), "B");
	}
}
