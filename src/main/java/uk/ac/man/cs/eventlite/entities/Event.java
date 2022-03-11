package uk.ac.man.cs.eventlite.entities;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name="events")
public class Event {

	@Id
	@GeneratedValue
	private long id;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime time;
	private String summary;

	private String name;
	
	private String description;
	
	@ManyToOne
	@JoinColumn(name="venue")
	private Venue venue;

	public Event() {
	}
	
	// No time and description
	public Event(String name, Venue venue, LocalDate date) {
		this.name = name;
		this.venue = venue;
		this.date = date;
		this.summary = name + " | " + venue.getName() + " | " + date.toString();
	}
	
	// No time
	public Event(String name, Venue venue, LocalDate date, String description) {
		this.name = name;
		this.venue = venue;
		this.date = date;
		this.description = description;
		this.summary = name + " | " + venue.getName() + " | " + date.toString();
	}
	
	// No description
	public Event(String name, Venue venue, LocalDate date, LocalTime time) {
		this.name = name;
		this.venue = venue;
		this.date = date;
		this.time = time;
		this.description = description;
		this.summary = name + " | " + venue.getName() + " | " + date.toString();
	}
	
	// Full constructor
	public Event(String name, Venue venue, LocalDate date, LocalTime time, String description) {
		this.name = name;
		this.venue = venue;
		this.date = date;
		this.time = time;
		this.description = description;
		this.summary = name + " | " + venue.getName() + " | " + date.toString();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Venue getVenue() {
		return venue;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}
	
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
}
