package uk.ac.man.cs.eventlite.dao;

import java.util.List;

import twitter4j.Status;
import twitter4j.TwitterException;
import uk.ac.man.cs.eventlite.entities.Event;

public interface EventService {

	public long count();

	public Iterable<Event> findAll();
	
	public Event save(Event event);

	public Iterable<Event> findAllAndSort();

	public Iterable<Event> search(String keyWords);
	
	public Event getEventById(long id);

	public void deleteById(long id);

	public void shareTweet(String tweet) throws TwitterException;
	
	List<Status> getLastFiveTweetsFromTimeline() throws TwitterException;
}
