package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import uk.ac.man.cs.eventlite.config.data.InitialDataLoader;
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

	@MockBean
	private EventService eventService;

	@MockBean
	private VenueService venueService;
	
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
	public void getUpdatingEvent() throws Exception{
		when(eventService.getEventById(1)).thenReturn(event);

		mvc.perform(get("/events/update/1").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/update")).andExpect(handler().methodName("getEventUpdate"));
		verify(event).getId();
	}
	
	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"ADMINISTRATOR"})
	public void postUpdateEventAuthorised() throws Exception {
		ArgumentCaptor<Event> newEventArg = ArgumentCaptor.forClass(Event.class);
		when(eventService.getEventById(1)).thenReturn(event);
		mvc.perform(MockMvcRequestBuilders.post("/events/update/1")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "Test Event New")
				.param("date", LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
				.param("venue", venue.getName())
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isFound())
		.andExpect(view().name("redirect:/events")).andExpect(model().hasNoErrors())
		.andExpect(handler().methodName("updateEvent"));
		verify(eventService).save(newEventArg.capture());
	}
	
	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"USER"})
	public void postUpdateEventUnauthorised() throws Exception {
		ArgumentCaptor<Event> newEventArg = ArgumentCaptor.forClass(Event.class);
		when(eventService.getEventById(1)).thenReturn(event);
		mvc.perform(MockMvcRequestBuilders.post("/events/update/1")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "Test Event New")
				.param("date", LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
				.param("venue", venue.getName())
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isForbidden());
		verify(eventService, never()).save(newEventArg.capture());
	}
	
	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"ADMINISTRATOR"})
	public void postUpdateEventBadName() throws Exception {
		ArgumentCaptor<Event> newEventArg = ArgumentCaptor.forClass(Event.class);
		when(eventService.getEventById(1)).thenReturn(event);
		mvc.perform(MockMvcRequestBuilders.post("/events/update/1")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "")
				.param("date", LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isOk())
		.andExpect(view().name("events/update")).andExpect(model().hasErrors())
		.andExpect(handler().methodName("updateEvent"));
		verify(eventService, never()).save(newEventArg.capture());
	}
	
	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"ADMINISTRATOR"})
	public void postUpdateEventBadDate() throws Exception {
		ArgumentCaptor<Event> newEventArg = ArgumentCaptor.forClass(Event.class);
		when(eventService.getEventById(1)).thenReturn(event);
		mvc.perform(MockMvcRequestBuilders.post("/events/update/1")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "new event")
				.param("date", LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isOk())
		.andExpect(view().name("events/update")).andExpect(model().hasErrors())
		.andExpect(handler().methodName("updateEvent"));
		verify(eventService, never()).save(newEventArg.capture());
	}
}
