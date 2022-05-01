package uk.ac.man.cs.eventlite.controllers;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

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
	@WithMockUser(username = "Mustafa", password = "Mustafa", roles= {"ADMINISTRATOR"})
	public void updateVenue() throws Exception {		
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
		.andExpect(view().name("redirect:/venues")).andExpect(model().hasNoErrors())
		.andExpect(handler().methodName("updateVenue"));
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


}
