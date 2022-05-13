package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.web.reactive.server.WebTestClient;

import uk.ac.man.cs.eventlite.EventLite;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = EventLite.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")

public class VenuesControllerApiIntegrationTest  extends AbstractTransactionalJUnit4SpringContextTests {

	private HttpEntity<String> httpEntity;

	@Autowired
	private TestRestTemplate template;

	private WebTestClient client;

	@LocalServerPort
	private int port;
	
	@BeforeEach
	public void setup() {
		client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port + "/api").build();
	}

	
	@Test
	public void testGetAllVenues() {
		ResponseEntity<String> response = template.exchange("/api/venues", HttpMethod.GET, httpEntity, String.class);

		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
	}
	
	@Test
	public void testGetOneVenue() {
		ResponseEntity<String> response = template.exchange("/api/venues/1", HttpMethod.GET, httpEntity, String.class);

		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
	}
	
	@Test
	public void getVenuesListToFail() {
		client.get().uri("/venues").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().is5xxServerError();
	}
	
//	Appologies for the naming and the above test after consulting with the professors we reached this solution
	@Test
	public void getVenuesListToNotFail() {
		client.get().uri("/venues").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk().expectHeader()
				.contentType(MediaType.APPLICATION_JSON).expectBody().jsonPath("$._links.self.href")
				.value(endsWith("/api/venues")).jsonPath("$._embedded.venues.length()").value(equalTo(3));
	}
	
	@Test
	public void getVenue() {
		long id = 1;

		client.get().uri("/venues/{id}", id).accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody().jsonPath("$._links.self.href")
				.value(endsWith("/" + id)).jsonPath("$._links.venue.href").value(endsWith("/" + id))
				.jsonPath("$._links.events.href").value(endsWith("/" + id + "/events"));
	}


}