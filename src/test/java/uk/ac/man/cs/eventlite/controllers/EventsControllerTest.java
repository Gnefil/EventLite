package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDate;
import java.time.LocalTime;
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
	
	private static final String char300 = 
				 "wyjgzxxinwgxvuvhboshbvuaodazhoxfrifnltrcowwkvclnoamthmjfqhdjhvjbhhwmdxhhylvtabyibjlxwwxpilerohavykipurjtcdiubnpdjcfvwpkvbvpuvuwwuxzidpmgnyhvfotwptqpybefkyfsqeoelilspgprgjywuwlxxfxtpoaxeblchuvbnjckurnklfseomqoxuaqmeynbtsicfcvmuescoqerfmfqtqbxqpoorkiznhkwmfdyxmlygfyqehnwaqvartkuwzognsahflfxzcszkaisnbs";
	private static final String char600 = char300 + char300 ;
	@Mock
	private Event event;
	
	@Mock
	private Venue venue;
	
	@Mock
	private Iterable<Event> events;

	/*
	@Mock
	private Iterable<Event> events;
	*/

	@MockBean
	private EventService eventService;

	@MockBean
	private VenueService venueService;
	
	@Test
	public void getSearchWithEvents() throws Exception {
		/*String keyWord = "event";
		when(eventService.search(keyWord)).thenReturn(events);
		*/	
		mvc.perform(get("/events/search?keyWords=event").accept(MediaType.TEXT_HTML))
		.andExpect(status().isOk())
		.andExpect(view().name("events/search"))
		.andExpect(handler().methodName("searchEventsByName"));
		
		verify(eventService).search("event");
	}

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

	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"ADMINISTRATOR"})
	public void getUpdatingEvent() throws Exception{
		when(eventService.getEventById(1)).thenReturn(event);

		mvc.perform(get("/events/update/1").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/update")).andExpect(handler().methodName("getEventUpdate"));
		verify(event).getId();
	}
	
	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"ADMINISTRATOR"})
	public void getUpdatingEventNotFound() throws Exception{

		mvc.perform(get("/events/update/1").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/not_found")).andExpect(handler().methodName("getEventUpdate"));
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
	
	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"ADMINISTRATOR"})
	public void postCreateEventEmptyDate() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/events")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("id", "1").param("name", "EventName")
				.param("time","08:00").param("description", "ok!").accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
		.andExpect(view().name("events/newEvent"))
		.andExpect(model().attributeHasFieldErrors("event", "date"))
		.andExpect(handler().methodName("createEvent"));

		verify(eventService, never()).save(event);
	}
	
	
	
	
	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"ADMINISTRATOR"})
	public void postCreateEventBadDate() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/events")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		        .param("date", LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
				.param("id", "1").param("name", "EventName")
				.param("time","08:00").param("description", "ok!").accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
		.andExpect(view().name("events/newEvent"))
		.andExpect(model().attributeHasFieldErrors("event", "date"))
		.andExpect(handler().methodName("createEvent"));

		verify(eventService, never()).save(event);
	}
	
	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"ADMINISTRATOR"})
	public void postCreateEventBadName() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/events")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		        .param("date", LocalDate.now().minusDays(-11).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
				.param("id", "1").param("name", char300)
				.param("time","08:00").param("description", "ok!").accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
		.andExpect(view().name("events/newEvent"))
		.andExpect(model().attributeHasFieldErrors("event", "name"))
		.andExpect(handler().methodName("createEvent"));

		verify(eventService, never()).save(event);
	}
	
	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"ADMINISTRATOR"})
	public void postCreateEventEmptyName() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/events")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
		        .param("date", LocalDate.now().minusDays(-11).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
				.param("id", "1").param("name", "")
				.param("time","08:00").param("description", "ok!").accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
		.andExpect(view().name("events/newEvent"))
		.andExpect(model().attributeHasFieldErrors("event", "name"))
		.andExpect(handler().methodName("createEvent"));

		verify(eventService, never()).save(event);
	}
	
	@Test
	public void postCreateEventLongDescription() throws Exception
	{
		mvc.perform(MockMvcRequestBuilders.post("/events").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).param("id", "1").param("name", "EventName")
				.param("date", "2019-01-01").param("time","10:30").param("description", char600)
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isOk()).andExpect(view().name("events/newEvent"))
		.andExpect(model().attributeHasFieldErrors("event", "description"))
		.andExpect(handler().methodName("createEvent"))
		.andExpect(flash().attributeCount(0));

		verify(eventService, never()).save(event);
	}
	
	
	@Test
	public void postShareTweet() throws Exception{
		String tweet = "Test tweet";
		when(eventService.getEventById(1)).thenReturn(event);
		mvc.perform(MockMvcRequestBuilders.post("/events/tweet/1")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("tweet", tweet)
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isFound())
		.andExpect(view().name("redirect:/events/details/{id}")).andExpect(model().hasNoErrors())
		.andExpect(handler().methodName("updateStatusOnTwitter")).andExpect(flash().attributeExists("response"));
	}
	
	@Test
	public void postEmptyShareTweet() throws Exception{
		String tweet = "";
		when(eventService.getEventById(1)).thenReturn(event);
		mvc.perform(MockMvcRequestBuilders.post("/events/tweet/1")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("tweet", tweet)
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isFound())
		.andExpect(view().name("redirect:/events/details/{id}")).andExpect(model().hasNoErrors())
		.andExpect(handler().methodName("updateStatusOnTwitter")).andExpect(flash().attributeExists("error"));
	}
	
	@Test
	public void postWhitespaceShareTweet() throws Exception{
		String tweet = " ";
		when(eventService.getEventById(1)).thenReturn(event);
		mvc.perform(MockMvcRequestBuilders.post("/events/tweet/1")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("tweet", tweet)
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isFound())
		.andExpect(view().name("redirect:/events/details/{id}")).andExpect(model().hasNoErrors())
		.andExpect(handler().methodName("updateStatusOnTwitter")).andExpect(flash().attributeExists("error"));
	}
	
	//New test
	
	@Test
	public void getEvent() throws Exception {
	    Venue v = new Venue();
		v.setName("Venue");
		v.setCapacity(1000);
		venueService.save(v);
		
		Event e = new Event();
		e.setId(1);
		e.setName("Event");
		e.setDate(LocalDate.now());
		e.setTime(LocalTime.now());
		e.setVenue(v);
		long id = e.getId();
		when(eventService.getEventById(id)).thenReturn(e);

		mvc.perform(MockMvcRequestBuilders.get("/events/details/"+id).accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("events/details/index")).andExpect(handler().methodName("getEventsDetails"));
		verify(eventService).getEventById(id);
	}
	
	@Test
	public void postEvent() throws Exception {
		ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);

		mvc.perform(MockMvcRequestBuilders.post("/events").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "Test Event New")
				.param("date", LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
				.param("venue", venue.getName())
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isFound())
		.andExpect(view().name("redirect:/events")).andExpect(model().hasNoErrors())
		.andExpect(handler().methodName("createEvent")).andExpect(flash().attributeExists("ok_message"));

		verify(eventService).save(arg.capture());
		assertThat("Test Event New", equalTo(arg.getValue().getName()));
	}
	
	
	

	@Test
	public void getNewEvent() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/events/newEvent").with(user("Rob").roles(Security.ADMIN_ROLE))
				.accept(MediaType.TEXT_HTML))
		.andExpect(status().isOk()).andExpect(view().name("events/newEvent"))
		.andExpect(handler().methodName("newEvent"));
	}
	
	
}
