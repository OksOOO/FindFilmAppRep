package com.odegova.findfilm.view;

import java.io.File;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.controlsfx.dialog.Dialogs;

import com.odegova.findfilm.MainApp;
import com.odegova.findfilm.model.FilmItem;

/**
 * Dialog to edit details of a film.
 * 
 * @author Odegova Oxana
 */
public class FilmEditDialogController {

    @FXML
    private TextField nameField;
    
    @FXML
    private TextField englishNameField;
    
    @FXML
    private TextField yearField;
    
    @FXML
    private TextField imdbField;
    
    @FXML
    private TextField genreField;
    
    @FXML
    private ChoiceBox<String> choiceBox;
    
    @FXML
    private TextField kinopoiskField;
    
    @FXML
    private TextField locationField;
    
    @FXML
    private TextArea commentField;
    
    @FXML
    private CheckBox haveViewedCheckBox;
    
    @FXML
    private CheckBox haveDownloadedCheckBox;
    
    @FXML
    private TextField imageLocationField;

    private Stage dialogStage;
    private FilmItem film;
    private boolean okClicked = false;
    
    // Reference to the main application
    private MainApp mainApp;
    
    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void initializeGenreList() {
    	// populate live data regions choicebox
    	choiceBox.setItems(mainApp.getGenreData());
    	choiceBox.getSelectionModel().selectFirst();
    	choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> setGenre(newValue));
    }
    
    /**
     * Sets the genre.
     * 
     * @param new genre
     */
    public void setGenre(String newGenre) {
        film.setGenre((film.getGenre() == null) ? newGenre : film.getGenre() + ", " + newGenre);
        genreField.setText(film.getGenre());
    }

    /**
     * Sets the stage of this dialog.
     * 
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Sets the person to be edited in the dialog.
     * 
     * @param person
     */
    public void setFilm(FilmItem film) {
        this.film = film;

        nameField.setText(film.getName());
        englishNameField.setText(film.getEnglishName());
        yearField.setText(film.getYear());
        imdbField.setText(film.getImdb());
        genreField.setText(film.getGenre());
        kinopoiskField.setText(film.getKinopoisk());
        locationField.setText(film.getLocation());
        commentField.setText(film.getComment());
        haveViewedCheckBox.setSelected(film.getHaveViewed());
        haveDownloadedCheckBox.setSelected(film.getHaveDownloaded());
        imageLocationField.setText(film.getImageLocation());
    }

    /**
     * Returns true if the user clicked OK, false otherwise.
     * 
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Called when the user clicks ok.
     */
    @FXML
    private void handleOk() {
        if (isInputValid()) {
        	film.setName(nameField.getText());
        	film.setEnglishName(englishNameField.getText());
        	film.setYear(yearField.getText());
        	film.setImdb(imdbField.getText());
        	film.setGenre(genreField.getText());
        	film.setKinopoisk(kinopoiskField.getText());
        	film.setLocation(locationField.getText());
        	film.setComment(commentField.getText());
        	film.setHaveViewed(haveViewedCheckBox.isSelected());
        	film.setHaveDownloaded(haveDownloadedCheckBox.isSelected());
        	film.setImageLocation(imageLocationField.getText());
        	
            okClicked = true;
            dialogStage.close();
        }
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    /**
     * Validates the user input in the text fields.
     * 
     * @return true if the input is valid
     */
    private boolean isInputValid() {
        String errorMessage = "";

        if (nameField.getText() == null || nameField.getText().length() == 0) {
            errorMessage += "No valid film name!\n"; 
        }
        if (yearField.getText() != null && yearField.getText().length() > 0) {
            // try to parse the year into an int.
            try {
                Integer.parseInt(yearField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "No valid year (must be an number)!\n"; 
            }
        }
        if (locationField.getText() == null || locationField.getText().length() == 0) {
            errorMessage += "No valid film location!\n"; 
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
        	Dialogs.create()
		        .title("Invalid Fields")
		        .masthead("Please correct invalid fields")
		        .message(errorMessage)
		        .showError();
            return false;
        }
    }
    
    /**
     * Opens a DirectoryChooser to let the user select an location of film.
     */
    @FXML
    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбор фильма");
        fileChooser.setInitialDirectory(
            new File((mainApp.getChoosedLocation() == null || mainApp.getChoosedLocation().isEmpty()) ? System.getProperty("user.home") : mainApp.getChoosedLocation())
        ); 

        // Show save file dialog
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

        if (file != null) {
        	String fileName = file.getName();
        	String[] tokens = fileName.split("\\.(?=[^\\.]+$)");
        	
        	film.setLocation(file.getPath());
        	if (film.getName() == null || film.getName().isEmpty()) {
				film.setName(tokens[0]);
			}
        	film.setHaveDownloaded(true);
        	mainApp.setChoosedLocation(file.getParent());
        	updateFilmDetails();
        }
    }
    
    /**
     * Fills all text fields to show details about the film.
     */
    private void updateFilmDetails() {
    	 nameField.setText(film.getName());
    	 englishNameField.setText(film.getEnglishName());
         yearField.setText(film.getYear());
         imdbField.setText(film.getImdb());
         genreField.setText(film.getGenre());
         kinopoiskField.setText(film.getKinopoisk());
         locationField.setText(film.getLocation());
         commentField.setText(film.getComment());
         haveViewedCheckBox.setSelected(film.getHaveViewed());
         haveDownloadedCheckBox.setSelected(film.getHaveDownloaded());
         imageLocationField.setText(film.getImageLocation());
    }
}