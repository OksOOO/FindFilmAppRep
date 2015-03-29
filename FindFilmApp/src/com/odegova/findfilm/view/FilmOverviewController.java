package com.odegova.findfilm.view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.controlsfx.dialog.Dialogs;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import com.odegova.findfilm.MainApp;
import com.odegova.findfilm.model.FilmItem;

/**
 * Dialog to view list and details of a film.
 * 
 * @author Odegova Oxana
 */
public class FilmOverviewController {
    
	@FXML
    private TableView<FilmItem> filmTable;
    
    @FXML
    private TableColumn<FilmItem, String> nameColumn;
    
    @FXML
    private TableColumn<FilmItem, String> imdbColumn;

    @FXML
    private Label nameLabel;
    
    @FXML
    private Label yearLabel;
    
    @FXML
    private Label genreLabel;
    
    @FXML
    private Hyperlink kinopoiskLabel;
    
    @FXML
    private Label commentLabel;
    
    @FXML
    private Label haveViewedLabel;
    
    @FXML
    private TextField filterField;
    
    @FXML
    private ImageView filmImageView;

    // Reference to the main application.
    private MainApp mainApp;
    
    private Desktop desktop = Desktop.getDesktop();

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public FilmOverviewController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	// Initialize the person table with the two columns.
    	nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    	imdbColumn.setCellValueFactory(cellData -> cellData.getValue().imdbProperty());
    	
    	// Clear film details.
        showFilmDetails(null);

        // Listen for selection changes and show the film details when changed.
        filmTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showFilmDetails(newValue));
        
        // Listen for text changes in the filter text field
        filterField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {
                updateFilteredData();
            }
        });

    }
    
    /**
     * Updates the filteredData to contain all data from the masterData that
     * matches the current filter.
     */
    private void updateFilteredData() {
        mainApp.getFilteredData().clear();

        for (FilmItem p : mainApp.getFilmData()) {
            if (matchesFilter(p)) {
            	mainApp.getFilteredData().add(p);
            }
        }
    }
    
    /**
     * Returns true if the person matches the current filter. Lower/Upper case
     * is ignored.
     * 
     * @param person
     * @return
     */
    private boolean matchesFilter(FilmItem filmItem) {
        String filterString = filterField.getText();
        if (filterString == null || filterString.isEmpty()) {
            // No filter --> Add all.
            return true;
        }

        String lowerCaseFilterString = filterString.toLowerCase();

        if (filmItem.getName() != null && filmItem.getName().toLowerCase().indexOf(lowerCaseFilterString) != -1) {
            return true;
        } else if (filmItem.getEnglishName() != null && filmItem.getEnglishName().toLowerCase().indexOf(lowerCaseFilterString) != -1) {
            return true;
        } else if (filmItem.getGenre() != null && filmItem.getGenre().toLowerCase().indexOf(lowerCaseFilterString) != -1) {
            return true;
        }

        return false; // Does not match
    }
    
    /**
     * Called when the user clicks on the kinopoisk button.
     */
    @FXML
    public void handleHyperText() {
        try {
            desktop.browse(new URI(filmTable.getSelectionModel().getSelectedItem().getKinopoisk()));
        } catch (Exception e1) {
            e1.printStackTrace();
        } 
    }
    
    /**
     * Called when the user clicks on the location button.
     */
    @FXML
    private void openFile() {
        try {
        	File file = new File(filmTable.getSelectionModel().getSelectedItem().getLocation());
            desktop.open(file);
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
    }

    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        // Add observable list data to the table
        filmTable.setItems(mainApp.getFilteredData());
    }
    
    /**
     * Fills all text fields to show details about the film.
     * If the specified film is null, all text fields are cleared.
     * 
     * @param film the film or null
     */
    private void showFilmDetails(FilmItem filmItem) {
        if (filmItem != null) {
            // Fill the labels with info from the film object.
        	String englishName = (filmItem.getEnglishName() == null || filmItem.getEnglishName().isEmpty()) ? "" : " (" + filmItem.getEnglishName() + ")";
            nameLabel.setText(filmItem.getName() + englishName);
            yearLabel.setText(filmItem.getYear());
            genreLabel.setText(filmItem.getGenre());
            commentLabel.setText(filmItem.getComment());
            haveViewedLabel.setText(filmItem.getHaveViewed() ? "Просмотрен" : "");
            if (filmItem.getImageLocation() != null && !filmItem.getImageLocation().isEmpty()) {
				//Image img = new Image("file://" + filmItem.getImageLocation());
            	Image img = new Image(filmItem.getImageLocation());
				filmImageView.setImage(img);
			} else {
				filmImageView.setImage(null);
			}
        } else {
            // Film is null, remove all the text.
        	nameLabel.setText("");
            yearLabel.setText("");
            genreLabel.setText("");
            commentLabel.setText("");
            haveViewedLabel.setText("");
            filmImageView.setImage(null);
        }
    }
    
    /**
     * Called when the user clicks on the delete button.
     */
    @FXML
    private void handleDeleteFilm() {
        int selectedIndex = filmTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
        	filmTable.getItems().remove(selectedIndex);
        	save();
        } else {
            // Nothing selected.
            Dialogs.create()
                .title("No Selection")
                .masthead("No Person Selected")
                .message("Please select a person in the table.")
                .showWarning();
        }
    }
    
    /**
     * Called when the user clicks the new button. Opens a dialog to edit
     * details for a new film.
     */
    @FXML
    private void handleNewFilm() {
        FilmItem tempFilm = new FilmItem();
        boolean okClicked = mainApp.showFilmEditDialog(tempFilm, true);
        if (okClicked) {
            mainApp.getFilmData().add(tempFilm);
            if (matchesFilter(tempFilm)) {
            	mainApp.getFilteredData().add(tempFilm);
            }
            save();
        }
    }
    
    /**
     * Saves the file to the person file that is currently open. If there is no
     * open file, the "save as" dialog is shown.
     */
    private void save() {
        File personFile = mainApp.getFilmFilePath();
        if (personFile != null) {
            mainApp.saveFilmDataToFile(personFile);
        } else {
            saveAs();
        }
    }

    /**
     * Opens a FileChooser to let the user select a file to save to.
     */
    private void saveAs() {
		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
				"XML files (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);

		// Show save file dialog
		File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

		if (file != null) {
			// Make sure it has the correct extension
			if (!file.getPath().endsWith(".xml")) {
				file = new File(file.getPath() + ".xml");
			}
			mainApp.saveFilmDataToFile(file);
		}
	}

    /**
     * Called when the user clicks the edit button. Opens a dialog to edit
     * details for the selected film.
     */
    @FXML
    private void handleEditFilm() {
    	FilmItem selectedFilm = filmTable.getSelectionModel().getSelectedItem();
        if (selectedFilm != null) {
            boolean okClicked = mainApp.showFilmEditDialog(selectedFilm, false);
            if (okClicked) {
                showFilmDetails(selectedFilm);
            }

        } else {
            // Nothing selected.
            Dialogs.create()
                .title("No Selection")
                .masthead("No Film Selected")
                .message("Please select a film in the table.")
                .showWarning();
        }
    }

}