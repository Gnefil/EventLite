package uk.ac.man.cs.eventlite.dao;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@Service
public class VenueServiceImpl implements VenueService {

	private final static Logger log = LoggerFactory.getLogger(VenueServiceImpl.class);

	private final static String DATA = "data/venues.json";

	@Autowired
	private VenueRepository venueRepository;
	
	@Autowired
	private EventRepository eventRepository;
	
	@Override
	public long count() {
		
		return venueRepository.count();
	}

	@Override
	public Iterable<Venue> findAll() {
		
		return venueRepository.findAll();
	}
	
	@Override
	public Iterable<Venue> findAllAndSort() {
		
		List<Venue> venues = (List<Venue>) venueRepository.findAll();
		Collections.sort(venues,new Comparator<Venue>(){
            public int compare(Venue v1, Venue v2){
            	return v1.getName().compareTo(v2.getName());
            }});
		return venues;
	}
	
	@Override
	public Venue save(Venue venue) {
		return venueRepository.save(venue);
	}
	
	@Override
	public Venue getVenueById(long id) {
		return venueRepository.findById(id).orElse(null);
	}

	@Override
	public void deleteById(long id) {
		venueRepository.deleteById(id);
	}

	@Override
	public Iterable<Venue> search(String keyWords) {
		// Format the key words input
		String lowerCaseWords = keyWords.toLowerCase().trim();
		
		return venueRepository.search(lowerCaseWords);
	}

	@Override
	// Turn an address into a geological coordinates (latitude and longitude) with MapBox API
	public Venue geocode(Venue venue, String token) {
		MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder()
				.accessToken(token)
				.query(venue.getRoadName()+" "+venue.getPostcode())
				.build();
		
		mapboxGeocoding.enqueueCall(new Callback<GeocodingResponse>() {
			@Override
			public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
		 
				List<CarmenFeature> results = response.body().features();
		 
				if (results.size() > 0) {
		 
					// Set the longitude and latitude of the venue based on the result
					Point firstResultPoint = results.get(0).center();
					venue.setLatitude(firstResultPoint.latitude());
					venue.setLongitude(firstResultPoint.longitude());
		 
				} else {
		 
					// No result for your request were found.
					System.out.println("MapBox API onResponse: No result found");
		 
				}
			}
		 
			@Override
			public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
				throwable.printStackTrace();
			}
		});
		
		// Wait for the response during 1 second time
		try {
			Thread.sleep(1000L);
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("Interrupted sleep in geocode.");
		}
		
		return venue;
	}

	@Override
	public List<Event> getEventsFromVenue(long venueId) {

		Iterable<Event> allEvents= eventRepository.findAll();
		
		List<Event> eventsInVenue = new ArrayList();
		
		for (Event event: allEvents) {
			if (event.getVenue().getId() == venueId) {
				eventsInVenue.add(event);
			}
		}
		
		return eventsInVenue;
	}

	@Override
	public List<Event> getNext3EventsFromVenue(long venueId) {
		
		int n = 0;
		List<Event> eventsInVenue = getEventsFromVenue(venueId);
		List<Event> eventsUpcoming = new ArrayList();
		List<Event> next3events = new ArrayList();
		
		for (Event event: eventsInVenue) {
			if (event.getDate().isAfter(LocalDate.now()) || (event.getDate().isEqual(LocalDate.now()) && event.getTime().isAfter(LocalTime.now())))
				eventsUpcoming.add(event);
		}
		
		
		Collections.sort(eventsUpcoming, (a, b)-> (a.getDate().isEqual(b.getDate()))
				? (a.getTime().compareTo(b.getTime()))
				: (a.getDate().compareTo(b.getDate()))
				);
		
		for (Event event: eventsUpcoming) {
			if (n++ < 3)
				next3events.add(event);
		}
		
		
		return next3events;
	}


}
