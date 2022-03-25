package uk.ac.man.cs.eventlite.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EventLiteErrorController implements ErrorController{
	
	@GetMapping(value = "/error")
	public String handleError() {
		return "error";
	}
	
}
