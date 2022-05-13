package uk.ac.man.cs.eventlite.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import uk.ac.man.cs.eventlite.entities.Event;

@Service
public class EventServiceImpl implements EventService{

	private final static Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

	private final static String DATA = "data/events.json";

	@Autowired
	private EventRepository eventRepository;
	
	private Twitter getTwitterInstance(){
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey("mKApDPniO5QWZi22nl3z5jr08")
		.setOAuthConsumerSecret("sR3kRsjHRTNFUrgTLeQ1b1d3sgfLzkIS2Pt1TPU7Gz00pWMX1S")
		.setOAuthAccessToken("1509910249016336400-LKKOw6E19ASF19iRrNmJvnMEvfZ7Py")
		.setOAuthAccessTokenSecret("zxyVBvQUxOvp8qJ48bm47nD1WSbEbGYU5g2rEF5SeIfBc");
		TwitterFactory tf = new TwitterFactory(cb.build());
		return tf.getInstance();
	}
	
	@Override
	public long count() {
		return eventRepository.count();
	}

	@Override
	public Iterable<Event> findAll() {
		return eventRepository.findAll();
	}
	
    @Override
	public Iterable<Event> findAllAndSort() {
		List<Event> events1 = (List<Event>) eventRepository.findAll();
		Collections.sort(events1,new Comparator<Event>(){
            public int compare(Event e1, Event e2){
            	if(e1.getDate().compareTo(e2.getDate()) != 0) {
            		return e1.getDate().compareTo(e2.getDate());
            	}
            	return e1.getName().compareTo(e2.getName());
            }});
		return events1;
	}
	
	@Override
	public Event getEventById(long id) {
		return eventRepository.findById(id).orElse(null);
	}
	
	@Override
	public Event save(Event event) {
		event.setSummary(event.getName() + " | " + event.getVenue().getName()
				+ " | " + event.getDate().toString());
		return eventRepository.save(event);
	}
	
	@Override
	public Iterable<Event> search(String keyWords) {
		
		// Format the key words input
		String lowerCaseWords = keyWords.toLowerCase().trim();
		
		return eventRepository.search(lowerCaseWords);
	}
	
	@Override
	public void deleteById(long id) {
		eventRepository.deleteById(id);
	}
	
	@Override
	public void shareTweet(String tweet) throws TwitterException {
		Twitter twitter = getTwitterInstance();
		twitter.updateStatus(tweet);
	}
	
	@Override
	public List<Status> getLastFiveTweetsFromTimeline() throws TwitterException, ArrayIndexOutOfBoundsException {
		Twitter twitter = getTwitterInstance();
		List<Status> tweets = twitter.getHomeTimeline();
	    List<Status> lastFiveTweets = new ArrayList<Status>();

	    for (int i = 0; i < 5; i++) {
	    	lastFiveTweets.add(tweets.get(i));     	
	    }
	    
	    return lastFiveTweets;

	}

}
