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
			// Pretty clean stuff Lifeng
			Venue A = new Venue("Venue A", "23 Manchester Road", "E14 3BD", 50);
			A.setLatitude(51.48764591576528);
			A.setLongitude(0.012130553645957599);
			
			Venue B = new Venue("Venue B", "Highland Road", "S43 2EZ", 1000);
			B.setLatitude(53.279748907167544);
			B.setLongitude(-1.4016698156695326);
			
			Venue C = new Venue("Venue C", "19 Acacia Avenue", "WA15 8QY", 10);
			C.setLatitude(53.38209646705332);
			C.setLongitude(-2.3373165003209593);

			if (venueService.count() > 0) {
				log.info("Database already populated with venues. Skipping venue initialization.");
			} else {
				// Build and save initial venues here.

				log.info("Preloading: " + venueService.save(A));
				log.info("Preloading: " + venueService.save(B));
				log.info("Preloading: " + venueService.save(C));
			}

			if (eventService.count() > 0) {
				log.info("Database already populated with events. Skipping event initialization.");
			} else {
				// Build and save initial events here.
				log.info("Preloading: " + eventService.save(new Event("Event Alpha", B, LocalDate.of(2022, 7, 11), LocalTime.of(12, 30), "Event Alpha is the first of its kind…")));
				log.info("Preloading: " + eventService.save(new Event("Event Beta", A, LocalDate.of(2022, 7, 11), LocalTime.of(10, 0), "Event Beta is not the first of its kind…")));
				log.info("Preloading: " + eventService.save(new Event("Event Apple", A, LocalDate.of(2022, 7, 12), "Event Apple will be host to some of the world’s best iOS developers…"))); // No time
				log.info("Preloading: " + eventService.save(new Event("Event Former", B, LocalDate.of(2022, 1, 11), LocalTime.of(11, 0), "Event Former happened long time ago.")));
				log.info("Preloading: " + eventService.save(new Event("Event Previous", A, LocalDate.of(2022, 1, 11), LocalTime.of(18, 30)))); // No description
				log.info("Preloading: " + eventService.save(new Event("Event Past", A, LocalDate.of(2022, 1, 10), LocalTime.of(17, 00), "Event Past happened long time ago.")));

			}
		};
	}
}
