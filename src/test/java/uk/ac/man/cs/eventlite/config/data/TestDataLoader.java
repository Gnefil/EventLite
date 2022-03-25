

package uk.ac.man.cs.eventlite.config.data;

import java.time.LocalDate;
import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@Configuration
@Profile("test")
public class TestDataLoader {

	private final static Logger log = LoggerFactory.getLogger(TestDataLoader.class);

	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;

	@Bean
	CommandLineRunner initDatabase() {
		return args -> {
			// Build and save test events and venues here.
			// The test database is configured to reside in memory, so must be initialized
			// every time.
			
			Venue A = new Venue("Venue A", 100, "Oxford Road");
			Venue B = new Venue("Venue B", 200, "Oxford Road");
			log.info("Preloading: " + venueService.save(A));
			log.info("Preloading: " + venueService.save(B));
			log.info("Preloading: " + eventService.save(new Event("Event Alpha", B, LocalDate.of(2022, 7, 11), LocalTime.of(12, 30), "Event Alpha is the first of its kind…")));
			log.info("Preloading: " + eventService.save(new Event("Event Beta", A, LocalDate.of(2022, 7, 11), LocalTime.of(10, 0), "Event Beta is not the first of its kind…")));
			log.info("Preloading: " + eventService.save(new Event("Event Apple", A, LocalDate.of(2022, 7, 12), "Event Apple will be host to some of the world’s best iOS developers…"))); // No time
			log.info("Preloading: " + eventService.save(new Event("Event Former", B, LocalDate.of(2022, 1, 11), LocalTime.of(11, 0), "Event Former happened long time ago.")));
			log.info("Preloading: " + eventService.save(new Event("Event Previous", A, LocalDate.of(2022, 1, 11), LocalTime.of(18, 30)))); // No description
			log.info("Preloading: " + eventService.save(new Event("Event Past", A, LocalDate.of(2022, 1, 10), LocalTime.of(17, 00), "Event Past happened long time ago.")));

		};
	}
}
