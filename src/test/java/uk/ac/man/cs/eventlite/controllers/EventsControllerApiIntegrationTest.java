package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.web.reactive.server.WebTestClient;

import uk.ac.man.cs.eventlite.EventLite;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = EventLite.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class EventsControllerApiIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

	@LocalServerPort
	private int port;

	private int currentRows;
	
	@Autowired
	private EventService eventService;

	private WebTestClient client;

	@BeforeEach
	public void setup() {
		currentRows = countRowsInTable("events");
		client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port + "/api").build();
	}

	
//	These tests function individually but fail during running coverage, this was discussed with the professor and we settled on commenting them
//	@Test
//	public void getEventsList() {
//		client.get().uri("/events").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk().expectHeader()
//				.contentType(MediaType.APPLICATION_JSON).expectBody().jsonPath("$._links.self.href")
//				.value(endsWith("/api/events")).jsonPath("$._embedded.events.length()").value(equalTo(currentRows));
//	}
//
//	@Test
//	public void getEvent() {
//		long id = 4;
//
//		client.get().uri("/events/{id}", id).accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk()
//				.expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody().jsonPath("$._links.self.href")
//				.value(endsWith("/" + id)).jsonPath("$._links.event.href").value(endsWith("/" + id))
//				.jsonPath("$._links.venue.href").value(endsWith("/" + id + "/venue"));
//	}
//	
//	@Test
//	public void getEventNotFound() {
//		client.get().uri("/events/99").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isNotFound()
//				.expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody().jsonPath("$.error")
//				.value(containsString("event 99")).jsonPath("$.id").isEqualTo(99);
//	}	

}
