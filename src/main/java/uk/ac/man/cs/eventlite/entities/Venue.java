package uk.ac.man.cs.eventlite.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="venues")
public class Venue {

	@Id
	@GeneratedValue
	private long id;

	private String name;
	
	private String address;

	private int capacity;
	
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
}
