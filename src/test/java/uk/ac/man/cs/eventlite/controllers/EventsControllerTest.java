package uk.ac.man.cs.eventlite.controllers;


import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import uk.ac.man.cs.eventlite.config.Security;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EventsController.class)
@Import(Security.class)
public class EventsControllerTest {

	@Autowired
	private MockMvc mvc;

	@Mock
	private Event event;

	@Mock
	private Venue venue;
	
	@Mock
	private Iterable<Event> events;

	@MockBean
	private EventService eventService;

	@MockBean
	private VenueService venueService;

//	@Test
//	public void searchEventByName() throws Exception {
//		when(eventService.search("event")).thenReturn(events);
//		mvc.perform(get("/events/search?keyWords=event").accept(MediaType.TEXT_HTML))
//			.andExpect(status().isOk())
//			.andExpect(view().name("events/byName"))
//			.andExpect(handler().methodName("getEventsByName"));
//		verify(eventService).search("event");
//	}
	
	@Test
	public void getIndexWhenNoEvents() throws Exception {
		when(eventService.findAll()).thenReturn(Collections.<Event>emptyList());
		when(venueService.findAll()).thenReturn(Collections.<Venue>emptyList());

		mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"));

//		verify(eventService).findAll();
//		verify(venueService).findAll();
		verifyNoInteractions(event);
		verifyNoInteractions(venue);
	}

	@Test
	public void getIndexWithEvents() throws Exception {
		when(venue.getName()).thenReturn("Kilburn Building");
		when(venueService.findAll()).thenReturn(Collections.<Venue>singletonList(venue));

		when(event.getVenue()).thenReturn(venue);
		when(eventService.findAll()).thenReturn(Collections.<Event>singletonList(event));

		mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"));

//		verify(eventService).findAll();
//		verify(venueService).findAll();
	}

	@Test
	public void getEventNotFound() throws Exception {
		mvc.perform(get("/events/99").accept(MediaType.TEXT_HTML)).andExpect(status().isNotFound())
				.andExpect(view().name("events/not_found")).andExpect(handler().methodName("getEvent"));
	}
	
	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"ADMINISTRATOR"})
	public void deleteEventByName() throws Exception
	{
	when(eventService.getEventById(1)).thenReturn(event);

	mvc.perform(MockMvcRequestBuilders.delete("/events/delete/1").accept(MediaType.TEXT_HTML).with(csrf()))
	.andExpect(status().isFound())
	.andExpect(view().name("redirect:/events"))
	.andExpect(handler().methodName("deleteById"));

	verify(eventService).deleteById(1);
	}
	
	
	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"USER"})
	public void deleteEventByNameUnauthorisedUser() throws Exception {
		
		when(eventService.getEventById(1)).thenReturn(event);
	
		mvc.perform(MockMvcRequestBuilders.delete("/events/delete/1").accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isForbidden());
	
		verify(eventService, never()).deleteById(1);
	}
	
	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"ADMINISTRATOR"})
	public void deleteEventByNameNoCsrf() throws Exception {
		when(eventService.getEventById(1)).thenReturn(event);
	
		mvc.perform(MockMvcRequestBuilders.delete("/events/delete/1").accept(MediaType.TEXT_HTML))
		.andExpect(status().isForbidden());
	
		verify(eventService, never()).deleteById(1);
	}
}
