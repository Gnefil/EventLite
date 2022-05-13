package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import uk.ac.man.cs.eventlite.assemblers.EventModelAssembler;
import uk.ac.man.cs.eventlite.assemblers.VenueModelAssembler;
import uk.ac.man.cs.eventlite.config.Security;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@ExtendWith(SpringExtension.class)
@WebMvcTest(VenuesControllerApi.class)
@Import({ Security.class, VenueModelAssembler.class, EventModelAssembler.class })

class VenuesControllerApiTest {

	@Autowired
	private MockMvc mvc;
	
	@Mock
	private Venue venue;
	
	@MockBean
	private VenueService venueService;
	
	@Test
	public void getVenueNotFound() throws Exception {
		
		long id = 0;
		
		when(venueService.getVenueById(id)).thenReturn(null);
				
		mvc.perform(get("/api/venues/0").accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
			.andExpect(jsonPath("$.error", containsString("venue 0")))
			.andExpect(jsonPath("$.id", equalTo((int)id)))
			.andExpect(handler().methodName("getVenue"));
		
		verify(venueService).getVenueById(id);
	}
	
	@Test
	public void getAllVenuesWhenIndex() throws Exception {
		Venue venue = new Venue();
		venue.setId(0);
		venue.setName("Graduation venue");
		venue.setCapacity(800);
		venue.setRoadName("Oxfor road");
		venue.setPostcode("M13 9PL");
		
		when(venueService.findAll()).thenReturn(Collections.<Venue>singletonList(venue));

		mvc.perform(get("/api/venues").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("getAllVenues")).andExpect(jsonPath("$.length()", equalTo(2)))
				.andExpect(jsonPath("$._embedded.venues.length()", equalTo(1)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues")))
				.andExpect(jsonPath("$._links.profile.href", endsWith("/api/profile/venues")))
				;

		verify(venueService).findAll();
	}
	
	@Test
	public void getIndexWhenNoVenues() throws Exception {
		when(venueService.findAll()).thenReturn(Collections.<Venue>emptyList());

		mvc.perform(get("/api/venues").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("getAllVenues"))
				.andExpect(jsonPath("$.length()", equalTo(1)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues")));

		verify(venueService).findAll();
	}
	
	@Test
	public void getVenueDetailsById() throws Exception {
		Venue venue = new Venue();
		venue.setId(0);
		venue.setName("Graduation venue");
		venue.setCapacity(800);
		venue.setRoadName("Oxfor road");
		venue.setPostcode("M13 9PL");
		
		when(venueService.getVenueById(0)).thenReturn(venue);

		mvc.perform(get("/api/venues/0").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
			.andExpect(handler().methodName("getVenue"))
			.andExpect(jsonPath("$.length()", equalTo(9))) // Id, name, capacity, address, road, postcode, lon, lat
			.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues/0")))
			.andExpect(jsonPath("$._links.venue.href", endsWith("/api/venues/0")))
			.andExpect(jsonPath("$._links.events.href", endsWith("/api/venues/0/events")))
			.andExpect(jsonPath("$._links.next3events.href", endsWith("/api/venues/0/next3events")))
			;
		
		
		verify(venueService, atLeast(1)).getVenueById(0);
	}
	
	@Test
	public void getEventsFromVenue() throws Exception {
		
		Event event = new Event();
		event.setId(0);
		event.setName("Graduation");
		event.setDate(LocalDate.now());
		event.setTime(LocalTime.now());
		event.setDescription("Welcome to our graduation ceremony");
		event.setSummary("Graduation ceremony");
		
		when(venueService.getEventsFromVenue(0)).thenReturn(Collections.<Event>singletonList(event));

		mvc.perform(get("/api/venues/0/events").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
			.andExpect(handler().methodName("getEventsFromVenue"))
			.andExpect(jsonPath("$.length()", equalTo(2)))
			.andExpect(jsonPath("$._embedded.events.length()", equalTo(1))) // 1 event
			.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues/0/events")))
			;
		
		verify(venueService).getEventsFromVenue(0);
	}
	
	@Test
	public void getEventsFromVenueWhen2Events() throws Exception {
		
		List<Event> events = new ArrayList();
		
		Event event = new Event();
		event.setId(0);
		event.setName("Graduation");
		event.setDate(LocalDate.now());
		event.setTime(LocalTime.now());
		event.setDescription("Welcome to our graduation ceremony");
		event.setSummary("Graduation ceremony");
		
		Event event1 = new Event();
		event1.setId(1);
		event1.setName("Wedding");
		event1.setDate(LocalDate.now());
		event1.setTime(LocalTime.now());
		event1.setDescription("Welcome to our wedding ceremony");
		event1.setSummary("Wedding ceremony");
		
		events.add(event);
		events.add(event1);
		
		when(venueService.getEventsFromVenue(0)).thenReturn(events);

		mvc.perform(get("/api/venues/0/events").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
			.andExpect(handler().methodName("getEventsFromVenue"))
			.andExpect(jsonPath("$.length()", equalTo(2)))
			.andExpect(jsonPath("$._embedded.events.length()", equalTo(2))) // 2 events
			.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues/0/events")))
			;
		
		verify(venueService).getEventsFromVenue(0);
	}
	
	@Test
	public void getEventsFromVenueWhen0Events() throws Exception {
				
		when(venueService.getEventsFromVenue(0)).thenReturn(Collections.emptyList());

		mvc.perform(get("/api/venues/0/events").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
			.andExpect(handler().methodName("getEventsFromVenue"))
			.andExpect(jsonPath("$.length()", equalTo(1))) // no embedded
			.andExpect(jsonPath("$._embedded.events").doesNotExist()) // no events
			.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues/0/events")))
			;
		
		verify(venueService).getEventsFromVenue(0);
	}
	
	@Test
	public void getNext3EventsWhen3() throws Exception {
		
		List<Event> events = new ArrayList();
		
		Venue venue = new Venue();
		venue.setId(0);
		venue.setName("Graduation venue");
		venue.setCapacity(800);
		venue.setRoadName("Oxfor road");
		venue.setPostcode("M13 9PL");
		
		Event event = new Event();
		event.setId(0);
		event.setName("Graduation");
		event.setDate(LocalDate.of(2022, 12, 31));
		event.setTime(LocalTime.of(12, 0));
		event.setDescription("Welcome to our graduation ceremony");
		event.setSummary("Graduation ceremony");
		event.setVenue(venue);
		
		Event event1 = new Event();
		event1.setId(1);
		event1.setName("Wedding");
		event1.setDate(LocalDate.of(2022, 12, 31));
		event1.setTime(LocalTime.of(11, 0));
		event1.setDescription("Welcome to our wedding ceremony");
		event1.setSummary("Wedding ceremony");
		
		Event event2 = new Event();
		event2.setId(2);
		event2.setName("Classmate Reunion");
		event2.setDate(LocalDate.of(2022, 12, 31));
		event2.setTime(LocalTime.of(10, 0));
		event2.setDescription("Welcome to our 20-21 undergraduates classmate reunion");
		event2.setSummary("20-21 undergraduates classmate reunion");
		
		event.setVenue(venue);
		event1.setVenue(venue);
		event2.setVenue(venue);
		
		events.add(event);
		events.add(event1);
		events.add(event2);

		when(venueService.getNext3EventsFromVenue(0)).thenReturn(events);

		mvc.perform(get("/api/venues/0/next3events").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
			.andExpect(handler().methodName("getNext3EventsFromVenue"))
			.andExpect(jsonPath("$.length()", equalTo(2)))
			.andExpect(jsonPath("$._embedded.events.length()", equalTo(3)))
			.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues/0/next3events")))
			;
		
		verify(venueService).getNext3EventsFromVenue(0);
	}
	
	@Test
	public void getNext3EventsWhen0() throws Exception {

		when(venueService.getNext3EventsFromVenue(0)).thenReturn(Collections.emptyList());

		mvc.perform(get("/api/venues/0/next3events").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
			.andExpect(handler().methodName("getNext3EventsFromVenue"))
			.andExpect(jsonPath("$.length()", equalTo(1))) // only links
			.andExpect(jsonPath("$._embedded.events").doesNotExist()) // no events
			.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues/0/next3events")))
			;
		
		verify(venueService).getNext3EventsFromVenue(0);
	}
	
	@Test
	public void getNext3EventsWhen1() throws Exception {
		
		List<Event> events = new ArrayList();
		
		Venue venue = new Venue();
		venue.setId(0);
		venue.setName("Graduation venue");
		venue.setCapacity(800);
		venue.setRoadName("Oxfor road");
		venue.setPostcode("M13 9PL");
		
		Event event = new Event();
		event.setId(0);
		event.setName("Graduation");
		event.setDate(LocalDate.of(2022, 12, 31));
		event.setTime(LocalTime.of(12, 0));
		event.setDescription("Welcome to our graduation ceremony");
		event.setSummary("Graduation ceremony");
		event.setVenue(venue);		
		
		event.setVenue(venue);

		events.add(event);

		when(venueService.getNext3EventsFromVenue(0)).thenReturn(events);

		mvc.perform(get("/api/venues/0/next3events").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
			.andExpect(handler().methodName("getNext3EventsFromVenue"))
			.andExpect(jsonPath("$.length()", equalTo(2)))
			.andExpect(jsonPath("$._embedded.events.length()", equalTo(1))) // only 1 event
			.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues/0/next3events")))
			;
		
		verify(venueService).getNext3EventsFromVenue(0);
	}
	
	@Test
	public void getNext3EventsWhen4() throws Exception {
		
		List<Event> events = new ArrayList();
		
		Venue venue = new Venue();
		venue.setId(0);
		venue.setName("Graduation venue");
		venue.setCapacity(800);
		venue.setRoadName("Oxfor road");
		venue.setPostcode("M13 9PL");
		
		Event event = new Event();
		event.setId(0);
		event.setName("Graduation");
		event.setDate(LocalDate.of(2022, 12, 31));
		event.setTime(LocalTime.of(12, 0));
		event.setDescription("Welcome to our graduation ceremony");
		event.setSummary("Graduation ceremony");
		event.setVenue(venue);
		
		Event event1 = new Event();
		event1.setId(1);
		event1.setName("Wedding");
		event1.setDate(LocalDate.of(2022, 12, 31));
		event1.setTime(LocalTime.of(11, 0));
		event1.setDescription("Welcome to our wedding ceremony");
		event1.setSummary("Wedding ceremony");
		
		Event event2 = new Event();
		event2.setId(2);
		event2.setName("Classmate Reunion");
		event2.setDate(LocalDate.of(2022, 12, 31));
		event2.setTime(LocalTime.of(10, 0));
		event2.setDescription("Welcome to our 20-21 undergraduates classmate reunion");
		event2.setSummary("20-21 undergraduates classmate reunion");
		
		Event event3 = new Event();
		event3.setId(3);
		event3.setName("Funeral");
		event3.setDate(LocalDate.of(2022, 12, 31));
		event3.setTime(LocalTime.of(9, 0));
		event3.setDescription("RIP Unknown, please be careful with the social and dress code in this serious occasion");
		event3.setSummary("Unknown's funeral");
		
		event.setVenue(venue);
		event1.setVenue(venue);
		event2.setVenue(venue);
		event3.setVenue(venue);
		
		events.add(event);
		events.add(event1);
		events.add(event2);

		when(venueService.getNext3EventsFromVenue(0)).thenReturn(events);

		mvc.perform(get("/api/venues/0/next3events").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
			.andExpect(handler().methodName("getNext3EventsFromVenue"))
			.andExpect(jsonPath("$.length()", equalTo(2)))
			.andExpect(jsonPath("$._embedded.events.length()", equalTo(3))) // should still be 3 events
			.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues/0/next3events")))
			;
		
		verify(venueService).getNext3EventsFromVenue(0);
	}
	
}
