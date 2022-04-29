package uk.ac.man.cs.eventlite.dao;

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
import uk.ac.man.cs.eventlite.entities.Venue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = EventLite.class)
@DirtiesContext
@ActiveProfiles("test")

public class VenueServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private VenueService venueService;

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