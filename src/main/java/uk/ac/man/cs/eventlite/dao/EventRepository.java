package uk.ac.man.cs.eventlite.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import uk.ac.man.cs.eventlite.entities.Event;

public interface EventRepository extends CrudRepository<Event, Long>{

	@Query(value="SELECT * FROM events WHERE name LIKE %:keyWords% ORDER BY date ASC, name ASC", nativeQuery=true)
	public Iterable<Event> search(String keyWords);

}
