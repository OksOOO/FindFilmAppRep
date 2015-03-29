package com.odegova.findfilm.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Helper class to wrap a list of films. This is used for saving the
 * list of films to XML.
 * 
 * @author Odegova Oxana
 */
@XmlRootElement(name = "films")
public class FilmListWrapper {

	private List<FilmItem> films;

	@XmlElement(name = "film")
	public List<FilmItem> getFilms() {
		return films;
	}

	public void setFilms(List<FilmItem> films) {
		this.films = films;
	}
}
