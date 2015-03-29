package com.odegova.findfilm.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * film item 
 * 
 * @author Odegova Oxana
 */
public class FilmItem {
	
	private StringProperty name;
	private StringProperty englishName;
    private StringProperty year;
    private StringProperty imdb;
    private StringProperty genre;
    private StringProperty kinopoisk;
    private StringProperty location;
    private StringProperty comment;
    private BooleanProperty haveViewed;
    private BooleanProperty haveDownloaded;
    private StringProperty imageLocation;
	
    public FilmItem() {
    	this(null, null,  null, null, null, null, null, null, false, false, null);
    }
    
    public FilmItem(String name, String englishName, String year, String imdb, String genre, String kinopoisk, 
    		String location, String comment, Boolean haveViewed, Boolean haveDownloaded, String imageLocation) {
		this.name = new SimpleStringProperty(name);
		this.englishName = new SimpleStringProperty(englishName);
		this.year = new SimpleStringProperty(year);
		this.imdb = new SimpleStringProperty(imdb);
		this.genre = new SimpleStringProperty(genre);
		this.kinopoisk = new SimpleStringProperty(kinopoisk);
		this.location = new SimpleStringProperty(location);
		this.comment = new SimpleStringProperty(comment);
		this.haveViewed = new SimpleBooleanProperty(haveViewed);
		this.haveDownloaded = new SimpleBooleanProperty(haveDownloaded);
		this.imageLocation = new SimpleStringProperty(imageLocation);
	}
    
    
    
    public StringProperty imageLocation() {
		return imageLocation;
	}

    public String getImageLocation() {
		return imageLocation.get();
	}

	public void setImageLocation(String imageLocation) {
		this.imageLocation.set(imageLocation);
	}

	public BooleanProperty haveDownloaded() {
		return haveDownloaded;
	}

	public Boolean getHaveDownloaded() {
		return haveDownloaded.get();
	}

	public void setHaveDownloaded(Boolean haveDownloaded) {
		this.haveDownloaded.set(haveDownloaded);
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}
	
	public StringProperty nameProperty() {
		return name;
	}
	
	public String getEnglishName() {
		return englishName.get();
	}

	public void setEnglishName(String englishName) {
		this.englishName.set(englishName);
	}
	
	public StringProperty englishNameProperty() {
		return englishName;
	}

	public String getYear() {
		return year.get();
	}

	public void setYear(String year) {
		this.year.set(year);
	}
	
	public StringProperty yearProperty() {
		return year;
	}

	public String getImdb() {
		return imdb.get();
	}

	public void setImdb(String imdb) {
		this.imdb.set(imdb);
	}
	
	public StringProperty imdbProperty() {
		return imdb;
	}

	public String getGenre() {
		return genre.get();
	}

	public void setGenre(String genre) {
		this.genre.set(genre);
	}
	
	public StringProperty genreProperty() {
		return genre;
	}

	public String getKinopoisk() {
		return kinopoisk.get();
	}

	public void setKinopoisk(String kinopoisk) {
		this.kinopoisk.set(kinopoisk);
	}
	
	public StringProperty kinopoiskProperty() {
		return kinopoisk;
	}

	public String getLocation() {
		return location.get();
	}

	public void setLocation(String location) {
		this.location.set(location);
	}
	
	public StringProperty locationProperty() {
		return location;
	}

	public String getComment() {
		return comment.get();
	}

	public void setComment(String comment) {
		this.comment.set(comment);
	}
	
	public StringProperty commentProperty() {
		return comment;
	}

	public BooleanProperty haveViewedProperty() {
		return haveViewed;
	}

	public void setHaveViewed(Boolean haveViewed) {
		this.haveViewed.set(haveViewed);
	}
	
	public Boolean getHaveViewed() {
		return haveViewed.get();
	}

}
