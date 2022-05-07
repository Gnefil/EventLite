package uk.ac.man.cs.eventlite.controllers;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
@WebMvcTest(VenuesController.class)
@Import(Security.class)
public class VenuesControllerTest {

	@Autowired
	private MockMvc mvc;

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
	public void getSearchWithVenues() throws Exception {

		mvc.perform(get("/venues/search?keyWords=venue").accept(MediaType.TEXT_HTML))
		.andExpect(status().isOk())
		.andExpect(view().name("venues/search"))
		.andExpect(handler().methodName("searchVenuesByName"));
		
		verify(venueService).search("venue");
	}
	
	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"ADMINISTRATOR"})
	public void getUpdatingVenue() throws Exception{
		when(venueService.getVenueById(1)).thenReturn(venue);

		mvc.perform(get("/venues/update/1").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("venues/update")).andExpect(handler().methodName("getVenueUpdate"));
		verify(venue).getId();
	}
	
	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"ADMINISTRATOR"})
	public void getUpdatingVenueNotFound() throws Exception{
		mvc.perform(get("/venues/update/1").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("venues/not_found")).andExpect(handler().methodName("getVenueUpdate"));
	}
//	
	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"ADMINISTRATOR"})
	public void updateVenueWithConnectedEvents() throws Exception {
		Venue B = new Venue("Venue B", "Highland Road", "S43 2EZ", 1000);
		B.setId(1);
		B.setLatitude(53.279748907167544);
		B.setLongitude(-1.4016698156695326);
		Event e = new Event("Event Alpha", B, LocalDate.of(2022, 7, 11), LocalTime.of(12, 30), "Event Alpha is the first of its kind…");
		ArgumentCaptor<Venue> newVenueArg = ArgumentCaptor.forClass(Venue.class);
		List<Event> events = new ArrayList<Event>();
		events.add(e);
		when(venueService.getVenueById(1)).thenReturn(B);
		when(eventService.findAll()).thenReturn((Iterable<Event>) events);
		mvc.perform(MockMvcRequestBuilders.post("/venues/update/1")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "Test Venue New")
				.param("roadName", "Oxford Rd")
				.param("postcode", "M13 9GP")
				.param("capacity", "5000")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isFound())
		.andExpect(view().name("redirect:/venues")).andExpect(model().hasNoErrors())
		.andExpect(handler().methodName("updateVenue")).andExpect(flash().attributeExists("ok_message"));
		verify(venueService).save(newVenueArg.capture());
	}
	
	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"ADMINISTRATOR"})
	public void updateVenueWithEventsNotConnected() throws Exception {
		Venue B = new Venue("Venue B", "Highland Road", "S43 2EZ", 1000);
		B.setId(1);
		B.setLatitude(53.279748907167544);
		B.setLongitude(-1.4016698156695326);
		Event e = new Event("Event Alpha", venue, LocalDate.of(2022, 7, 11), LocalTime.of(12, 30), "Event Alpha is the first of its kind…");
		ArgumentCaptor<Venue> newVenueArg = ArgumentCaptor.forClass(Venue.class);
		List<Event> events = new ArrayList<Event>();
		events.add(e);
		when(venueService.getVenueById(1)).thenReturn(B);
		when(eventService.findAll()).thenReturn((Iterable<Event>) events);
		mvc.perform(MockMvcRequestBuilders.post("/venues/update/1")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "Test Venue New")
				.param("roadName", "Oxford Rd")
				.param("postcode", "M13 9GP")
				.param("capacity", "5000")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isFound())
		.andExpect(view().name("redirect:/venues")).andExpect(model().hasNoErrors())
		.andExpect(handler().methodName("updateVenue"));
		verify(venueService).save(newVenueArg.capture());
	}
	
	
	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"ADMINISTRATOR"})
	public void updateVenueWithoutConnectedEvents() throws Exception {
		ArgumentCaptor<Venue> newVenueArg = ArgumentCaptor.forClass(Venue.class);
		when(venueService.getVenueById(1)).thenReturn(venue);
		mvc.perform(MockMvcRequestBuilders.post("/venues/update/1")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "Test Venue New")
				.param("roadName", "Oxford Rd")
				.param("postcode", "M13 9GP")
				.param("capacity", "5000")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isFound())
		.andExpect(view().name("redirect:/venues")).andExpect(model().hasNoErrors());
		verify(venueService).save(newVenueArg.capture());
	}
	
	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"ADMINISTRATOR"})
	public void postUpdateVenueNoName() throws Exception {
		ArgumentCaptor<Venue> newVenueArg = ArgumentCaptor.forClass(Venue.class);
		when(venueService.getVenueById(1)).thenReturn(venue);
		mvc.perform(MockMvcRequestBuilders.post("/venues/update/1")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "")
				.param("roadName", "Oxford Rd")
				.param("postcode", "M13 9GP")
				.param("capacity", "5000")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isOk())
		.andExpect(view().name("venues/update")).andExpect(model().hasErrors())
		.andExpect(handler().methodName("updateVenue"));
		verify(venueService, never()).save(newVenueArg.capture());
	}
	
	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"ADMINISTRATOR"})
	public void postUpdateVenueNoRoadName() throws Exception {
		ArgumentCaptor<Venue> newVenueArg = ArgumentCaptor.forClass(Venue.class);
		when(venueService.getVenueById(1)).thenReturn(venue);
		mvc.perform(MockMvcRequestBuilders.post("/venues/update/1")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "Test Venue New")
				.param("roadName", "")
				.param("postcode", "M13 9GP")
				.param("capacity", "5000")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isOk())
		.andExpect(view().name("venues/update")).andExpect(model().hasErrors())
		.andExpect(handler().methodName("updateVenue"));
		verify(venueService, never()).save(newVenueArg.capture());
	}
	

	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"USER"})
	public void postUpdateVenueUnauthorisedUser() throws Exception {
		ArgumentCaptor<Venue> newVenueArg = ArgumentCaptor.forClass(Venue.class);
		when(venueService.getVenueById(1)).thenReturn(venue);
		mvc.perform(MockMvcRequestBuilders.post("/venues/update/1")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "Test Venue New")
				.param("roadName", "Oxford Rd")
				.param("postcode", "M13 9GP")
				.param("capacity", "5000")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isForbidden());
	
		verify(venueService, never()).save(newVenueArg.capture());
	}
	
	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"ADMINISTRATOR"})
	public void postUpdateVenueBadCapacity() throws Exception {
		ArgumentCaptor<Venue> newVenueArg = ArgumentCaptor.forClass(Venue.class);
		when(venueService.getVenueById(1)).thenReturn(venue);
		mvc.perform(MockMvcRequestBuilders.post("/venues/update/1")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "Test Venue New")
				.param("roadName", "Oxford Rd")
				.param("postcode", "M13 9GP")
				.param("capacity", "-1")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isOk())
		.andExpect(view().name("venues/update")).andExpect(model().hasErrors())
		.andExpect(handler().methodName("updateVenue"));
		verify(venueService, never()).save(newVenueArg.capture());
	}
	
	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"ADMINISTRATOR"})
	public void postUpdateVenueEmptyPostcode() throws Exception {
		ArgumentCaptor<Venue> newVenueArg = ArgumentCaptor.forClass(Venue.class);
		when(venueService.getVenueById(1)).thenReturn(venue);
		mvc.perform(MockMvcRequestBuilders.post("/venues/update/1")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "Test Venue New")
				.param("roadName", "Oxford Rd")
				.param("postcode", "")
				.param("capacity", "5000")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isOk())
		.andExpect(view().name("venues/update")).andExpect(model().hasErrors())
		.andExpect(handler().methodName("updateVenue"));
		verify(venueService, never()).save(newVenueArg.capture());
	}
	
	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"USER"})
	public void deleteVenueByNameUnauthorisedUser() throws Exception {
		when(venueService.getVenueById(1)).thenReturn(venue);
	
		mvc.perform(MockMvcRequestBuilders.delete("/venues/delete/1").accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isForbidden());
	
		verify(venueService, never()).deleteById(1);
	}
	
	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"ADMINISTRATOR"})
	public void deleteVenueByNameNoCsrf() throws Exception {
		when(venueService.getVenueById(1)).thenReturn(venue);
	
		mvc.perform(MockMvcRequestBuilders.delete("/venues/delete/1").accept(MediaType.TEXT_HTML))
		.andExpect(status().isForbidden());
	
		verify(venueService, never()).deleteById(1);
	}
	
	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"ADMINISTRATOR"})
	public void newVenue() throws Exception {		
		mvc.perform(MockMvcRequestBuilders.get("/venues/newVenue")
		        .accept(MediaType.TEXT_HTML))
		    .andExpect(status().isOk()).andExpect(view().name("venues/newVenue"))
		    .andExpect(handler().methodName("newVenue"));
	}
	
	
	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"ADMINISTRATOR"})
	public void newVenueNoName() throws Exception {
		ArgumentCaptor<Venue> newVenueArg = ArgumentCaptor.forClass(Venue.class);
		when(venueService.getVenueById(1)).thenReturn(venue);
		mvc.perform(MockMvcRequestBuilders.post("/venues/newVenue")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "")
				.param("roadName", "Oxford Rd")
				.param("postcode", "M13 9GP")
				.param("capacity", "5000")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isOk())
		.andExpect(view().name("venues/newVenue")).andExpect(model().hasErrors())
		.andExpect(handler().methodName("createVenue"));
		verify(venueService, never()).save(newVenueArg.capture());
	}
	
	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"ADMINISTRATOR"})
	public void newVenueNoRoadName() throws Exception {
		ArgumentCaptor<Venue> newVenueArg = ArgumentCaptor.forClass(Venue.class);
		when(venueService.getVenueById(1)).thenReturn(venue);
		mvc.perform(MockMvcRequestBuilders.post("/venues/newVenue")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "Test Venue New")
				.param("roadName", "")
				.param("postcode", "M13 9GP")
				.param("capacity", "5000")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isOk())
		.andExpect(view().name("venues/newVenue")).andExpect(model().hasErrors())
		.andExpect(handler().methodName("createVenue"));
		verify(venueService, never()).save(newVenueArg.capture());
	}
	

	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"USER"})
	public void newVenueUnauthorisedUser() throws Exception {
		ArgumentCaptor<Venue> newVenueArg = ArgumentCaptor.forClass(Venue.class);
		when(venueService.getVenueById(1)).thenReturn(venue);
		mvc.perform(MockMvcRequestBuilders.post("/venues/newVenue")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "Test Venue New")
				.param("roadName", "Oxford Rd")
				.param("postcode", "M13 9GP")
				.param("capacity", "5000")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isForbidden());
	
		verify(venueService, never()).save(newVenueArg.capture());
	}
	
	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"ADMINISTRATOR"})
	public void newVenueBadCapacity() throws Exception {
		ArgumentCaptor<Venue> newVenueArg = ArgumentCaptor.forClass(Venue.class);
		when(venueService.getVenueById(1)).thenReturn(venue);
		mvc.perform(MockMvcRequestBuilders.post("/venues/newVenue")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "Test Venue New")
				.param("roadName", "Oxford Rd")
				.param("postcode", "M13 9GP")
				.param("capacity", "-1")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isOk())
		.andExpect(view().name("venues/newVenue")).andExpect(model().hasErrors())
		.andExpect(handler().methodName("createVenue"));
		verify(venueService, never()).save(newVenueArg.capture());
	}
	
	@Test
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"ADMINISTRATOR"})
	public void newVenueEmptyPostcode() throws Exception {
		ArgumentCaptor<Venue> newVenueArg = ArgumentCaptor.forClass(Venue.class);
		when(venueService.getVenueById(1)).thenReturn(venue);
		mvc.perform(MockMvcRequestBuilders.post("/venues/newVenue")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "Test Venue New")
				.param("roadName", "Oxford Rd")
				.param("postcode", "")
				.param("capacity", "5000")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isOk())
		.andExpect(view().name("venues/newVenue")).andExpect(model().hasErrors())
		.andExpect(handler().methodName("createVenue"));
		verify(venueService, never()).save(newVenueArg.capture());
	}



}
