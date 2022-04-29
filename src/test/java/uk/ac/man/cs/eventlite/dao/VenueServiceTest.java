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
import uk.ac.man.cs.eventlite.entities.Venue;

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
}
