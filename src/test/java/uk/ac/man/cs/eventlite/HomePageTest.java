package uk.ac.man.cs.eventlite;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = EventLite.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class HomePageTest {

	private MockMvc mvc;
	
	@MockBean
	private EventService eventService;
	
	@MockBean
	private VenueService venueService;

	@Mock
	private Event event;
	
	@Mock
	private Venue venue;
	
	@Autowired
	private WebApplicationContext context;

	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@Test
	public void getRoot() throws Exception {
		mvc.perform(get("/").accept(MediaType.TEXT_HTML)).andExpect(status().isOk());
	}

	/*
	@Test
	public void getJsonRoot() throws Exception {
		mvc.perform(get("/").accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}*/

	
	@Test
	public void getApiRoot() throws Exception {
		mvc.perform(get("/api").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
	
	@Test
	public void checkGetEventsAndVenuesForHomePage() throws Exception{	
		mvc.perform(get("/").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
			.andExpect(handler().methodName("getEventsAndVenuesForHomePage"));
		
		verify(eventService).findAll();
		verify(venueService).findThreeVenuesWithMostEvents();
	}
}
