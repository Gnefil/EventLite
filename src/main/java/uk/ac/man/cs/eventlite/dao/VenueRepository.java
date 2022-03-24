package uk.ac.man.cs.eventlite.dao;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

public interface VenueRepository extends CrudRepository<Venue, Long>{
	
	@Query(value="SELECT * FROM venues WHERE LOWER(name) LIKE %:keyWords% ORDER BY name ASC", nativeQuery=true)
	public Iterable<Venue> search(@Param("keyWords") String keyWords);
	
}
