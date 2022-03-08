package uk.ac.man.cs.eventlite.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.man.cs.eventlite.entities.Event;

public interface EventRepository extends CrudRepository<Event, Long>{

	@Query(value="SELECT * FROM events WHERE LOWER(name) LIKE %:keyWords% ORDER BY date ASC, name ASC", nativeQuery=true)
	public Iterable<Event> search(@Param("keyWords") String keyWords);

}
