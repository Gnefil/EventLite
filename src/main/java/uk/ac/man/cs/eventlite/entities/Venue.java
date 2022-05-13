package uk.ac.man.cs.eventlite.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Entity
@Table(name="venues")
public class Venue {

	@Id
	@GeneratedValue
	private long id;

	@NotEmpty(message = "Name required")
	@NotBlank(message = "Name must not be blank")	
	@Size(max = 256, message = "Venue name must have 256 characters or less")
	@Size(min = 1, message = "Venue name required")
	private String name;
	
	private String address;

	@NotEmpty(message = "Road name required")
	@Size(max = 300, message = "Road name must have 300 characters or less")
	private String roadName;
	
	@NotEmpty(message = "Postcode required")
	@Size(max = 256, message = "Postcode must have 256 characters or less")
	private String postcode;

	@Min(value = 0, message = "Venue capacity must be a positive integer")
	private int capacity;
	
	private double latitude;
	private double longitude;	
	
//	@OneToMany
//	private Set<Event> events;

	public Venue() {
	}
	
	public Venue(String name, String address, int capacity) {
		this.name = name;
		this.capacity = capacity;
		this.address = address;
//		this.events = new HashSet<Event>();
	}
	
		public Venue(String name, String roadName, String postcode, int capacity) {
		this.name = name;
		this.capacity = capacity;
		this.roadName = roadName;
		this.postcode = postcode;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	public String getRoadName() {
		return roadName;
	}

	public void setRoadName(String roadName) {
		this.roadName = roadName;
	}
	
	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	
	public Double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	public Double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

}
