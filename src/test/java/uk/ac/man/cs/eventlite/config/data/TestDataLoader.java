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
			Venue venue1 = new Venue("Kilburn", 100);
			Venue venue2 = new Venue("MECD", 200);
			log.info("Preloading: " + venueService.save(venue1));
			log.info("Preloading: " + venueService.save(venue2));
			log.info("Preloading: " + eventService.save(new Event("Team Study", venue1, LocalDate.now(), LocalTime.now())));
			log.info("Preloading: " + eventService.save(new Event("Lab", venue1, LocalDate.now(), LocalTime.now())));
			log.info("Preloading: " + eventService.save(new Event("Lecture", venue2, LocalDate.now(), LocalTime.now())));
		};
	}
}
