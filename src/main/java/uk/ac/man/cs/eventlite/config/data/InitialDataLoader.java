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
import uk.ac.man.cs.eventlite.dao.EventServiceImpl;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@Configuration
@Profile("default")
public class InitialDataLoader {

	private final static Logger log = LoggerFactory.getLogger(InitialDataLoader.class);

	//private EventServiceImpl eService = new EventServiceImpl();
	
	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;

	@Bean
	CommandLineRunner initDatabase() {
		return args -> {
			Venue venue1 = new Venue("Kilburn", "Kilburn", 100);
			Venue venue2 = new Venue("MECD", "MECD", 200);
			Venue venue3 = new Venue("Emptyhad", "Emptyhad", 0);
			if (venueService.count() > 0) {
				log.info("Database already populated with venues. Skipping venue initialization.");
			} else {
				// Build and save initial venues here.
				log.info("Preloading: " + venueService.save(venue1));
				log.info("Preloading: " + venueService.save(venue2));
				log.info("Preloading: " + venueService.save(venue3));
			}

			if (eventService.count() > 0) {
				log.info("Database already populated with events. Skipping event initialization.");
			} else {
				// Build and save initial events here.
				log.info("Preloading: " + eventService.save(new Event("Team Study", venue1, LocalDate.now(), LocalTime.now(), "")));
				log.info("Preloading: " + eventService.save(new Event("Lab", venue1, LocalDate.now(), LocalTime.now(), "")));
				log.info("Preloading: " + eventService.save(new Event("Lecture", venue2, LocalDate.now(), LocalTime.now(), "")));
			}
		};
	}
}
