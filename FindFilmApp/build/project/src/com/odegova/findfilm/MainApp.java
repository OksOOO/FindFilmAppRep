package com.odegova.findfilm;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.controlsfx.dialog.Dialogs;

import com.odegova.findfilm.model.FilmItem;
import com.odegova.findfilm.model.FilmListWrapper;
import com.odegova.findfilm.view.FilmEditDialogController;
import com.odegova.findfilm.view.FilmOverviewController;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private Image appIcon = new Image("file:resources/images/film.png");
    private Image newIcon = new Image("file:resources/images/film_add.png");
    private Image editIcon = new Image("file:resources/images/film.png");
    private String choosedLocation;
    private Boolean filterByNotViewed = false;
    private Boolean filterByDownloaded = false;
    private Boolean filterByNotDownloaded = false;
    private String genreFilter;
    
    /**
     * The data as an observable list of Films and Genres.
     */
    private ObservableList<FilmItem> filmData = FXCollections.observableArrayList();
    private ObservableList<FilmItem> filteredData = FXCollections.observableArrayList();
    private ObservableList<String> genreData = FXCollections.observableArrayList("", "комедия", "фантастика", "мелодрама", "боевик", "триллер", "детектив", 
			"приключения", "мультфильм", "нуар", "ретро", "драма", "военный", "история", "документальный", "вестерн", "новогоднее");
    
    //http://www.kinopoisk.ru/index.php?first=no&what=&kp_query=%D0%B3%D0%BB%D0%B0%D0%B4%D0%B8%D0%B0%D1%82%D0%BE%D1%80
    //можно название вводить русскими буквами
    //если first=yes  - открывает сразу страницу с первым в списке фильмом
    //www.kinopoisk.ru/index.php?kp_query=%s
    //torrents.ru/forum/tracker.php?nm=%s
    /* <?PHP
    		$curl = curl_init();
    		curl_setopt($curl, CURLOPT_URL, 'http://www.kinopoisk.ru/film/341/');
    		curl_setopt($curl, CURLOPT_COOKIEJAR, 'cook.txt');//сохранить куки в файл
    		curl_setopt($curl, CURLOPT_COOKIEFILE, 'cook.txt');//считать куки из файла
    		curl_setopt($curl, CURLOPT_FAILONERROR, 1);
    		curl_setopt($curl, CURLOPT_USERAGENT, "Opera/10.00 (Windows NT 5.1; U; ru) Presto/2.2.0");
    		curl_setopt($curl, CURLOPT_REFERER, 'http://www.kinopoisk.ru/');
    		curl_setopt($curl, CURLOPT_TIMEOUT, 3);
    		curl_setopt($curl, CURLOPT_HEADER, 1);
    		curl_setopt ($curl, CURLOPT_SSL_VERIFYPEER, 0);// не проверять SSL сертификат
    		curl_setopt ($curl, CURLOPT_SSL_VERIFYHOST, 0);// не проверять Host SSL сертификата
    		curl_setopt($curl, CURLOPT_FOLLOWLOCATION, 1);// разрешаем редиректы
    		curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);
    		$html = curl_exec($curl);
    		print_r($html);
    		?> */
    //картинка с кинопоиска
    //<!DOCTYPE html> <html> <body> <img src="http://www.kinopoisk.ru/im/poster/1/9/7/kinopoisk.ru-Dredd-3D-1973620.jpg" alt="pic"/> </body> </html>

    
    
    public ObservableList<String> getGenreData() {
		return genreData;
	}

	public void setGenreData(ObservableList<String> genreData) {
		this.genreData = genreData;
	}

	public Boolean getFilterByNotDownloaded() {
		return filterByNotDownloaded;
	}

	public void setFilterByNotDownloaded(Boolean filterByNotDownloaded) {
		this.filterByNotDownloaded = filterByNotDownloaded;
		filterFilmList();
	}

	public Boolean getFilterByNotViewed() {
		return filterByNotViewed;
	}

	public void setFilterByNotViewed(Boolean filterByNotViewed) {
		this.filterByNotViewed = filterByNotViewed;
		filterFilmList();
	}

	public Boolean getFilterByDownloaded() {
		return filterByDownloaded;
	}

	public void setFilterByDownloaded(Boolean filterByDownloaded) {
		this.filterByDownloaded = filterByDownloaded;
		filterFilmList();
	}

	/**
     * Returns the data as an observable list of Films. 
     * @return
     */
    public ObservableList<FilmItem> getFilmData() {
        return filmData;
    }
    
    /**
     * Returns the data as an filtered observable list of Films. 
     * @return
     */
    public ObservableList<FilmItem> getFilteredData() {
        return filteredData;
    }

	@Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("FindFilmApp");
        
        // Set the application icon.
        this.primaryStage.getIcons().add(appIcon);

        initRootLayout();

        showFilmOverview();
    }
    
    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
    	
    	rootLayout = new BorderPane();
        
        // Show the scene containing the root layout.
        Scene scene = new Scene(rootLayout, 900, 600, Color.WHITE);
        primaryStage.setScene(scene);
        
        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        rootLayout.setTop(menuBar);

        // File menu - new, open, save, save as, exit
        Menu fileMenu = new Menu("Файл");
        MenuItem newMenuItem = new MenuItem("Создать");
        newMenuItem.setOnAction(actionEvent -> newFile());
        MenuItem openMenuItem = new MenuItem("Открыть...");
        openMenuItem.setOnAction(actionEvent -> open());
        MenuItem saveMenuItem = new MenuItem("Сохранить");
        saveMenuItem.setOnAction(actionEvent -> save());
        MenuItem saveAsMenuItem = new MenuItem("Сохранить как...");
        saveAsMenuItem.setOnAction(actionEvent -> saveAs());
        MenuItem exitMenuItem = new MenuItem("Выход");
        exitMenuItem.setOnAction(actionEvent -> Platform.exit());

        fileMenu.getItems().addAll(newMenuItem, openMenuItem, saveMenuItem, saveAsMenuItem, exitMenuItem);

        // View menu - not viewed, downloaded, not downloaded
        Menu viewMenu = new Menu("Вид");
        CheckMenuItem notViewedMenuItem = new CheckMenuItem("Непросмотренные");
        notViewedMenuItem.setSelected(false);
        notViewedMenuItem.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue ov, Boolean old_val, Boolean new_val) {
            	setFilterByNotViewed(new_val);
            }
        });
        CheckMenuItem downloadedMenuItem = new CheckMenuItem("Загруженные");
        downloadedMenuItem.setSelected(false);
        downloadedMenuItem.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue ov, Boolean old_val, Boolean new_val) {
            	setFilterByDownloaded(new_val);
            }
        });
        CheckMenuItem notDownloadedMenuItem = new CheckMenuItem("Незагруженные");
        notDownloadedMenuItem.setSelected(false);
        notDownloadedMenuItem.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue ov, Boolean old_val, Boolean new_val) {
            	setFilterByNotDownloaded(new_val);
            }
        });
        Menu menuGenre = new Menu("Жанры");
        ToggleGroup groupEffect = new ToggleGroup();
        for (String genre : genreData) {
        	RadioMenuItem  genreMenuItem = new RadioMenuItem(genre);
        	genreMenuItem.setUserData(genre);
        	genreMenuItem.setToggleGroup(groupEffect);
        	menuGenre.getItems().add(genreMenuItem);
        }
        groupEffect.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
                if (groupEffect.getSelectedToggle() != null) {
                    genreFilter = (String) groupEffect.getSelectedToggle().getUserData();
                    filterFilmList();
                }
            }
        });
        
        viewMenu.getItems().addAll(notViewedMenuItem, downloadedMenuItem, notDownloadedMenuItem, menuGenre);
        
        // Help menu - about
        Menu helpMenu = new Menu("Прочее");
        
        MenuItem aboutMenuItem = new MenuItem("О программе");
        aboutMenuItem.setOnAction(actionEvent -> Dialogs.create()
    	        .title("FindFilmApp")
    	        .masthead("О программе")
    	        .message("Автор: Одегова Оксана\nWebsite: http://odegova.com")
    	        .showInformation());
        helpMenu.getItems().add(aboutMenuItem);

        menuBar.getMenus().addAll(fileMenu, viewMenu, helpMenu);
        
        primaryStage.show();
        
        // Try to load last opened film file.
        File file = getFilmFilePath();
        if (file != null) {
            loadFilmDataFromFile(file);
        }
    }
    
    private void filterFilmList() {
    	filteredData.clear();
    	for (FilmItem filmItem : filmData) {
            if ((!filterByNotViewed || (filterByNotViewed && !filmItem.getHaveViewed())) 
            		&& (!filterByDownloaded || (filterByDownloaded && filmItem.getHaveDownloaded()))
            		&& (!filterByNotDownloaded || (filterByNotDownloaded && !filmItem.getHaveDownloaded()))
            		&& (genreFilter == null || genreFilter.isEmpty() || filmItem.getGenre().contains(genreFilter))) {
            	filteredData.add(filmItem);
            } 
        }
	}

    /**
     * Shows the film overview inside the root layout.
     */
    public void showFilmOverview() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/FilmOverview.fxml"));
            AnchorPane filmOverview = (AnchorPane) loader.load();
            
            // Set person overview into the center of root layout.
            rootLayout.setCenter(filmOverview);
            
            // Give the controller access to the main app.
            FilmOverviewController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
	/**
	 * Returns the main stage.
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	/**
	 * Opens a dialog to edit details for the specified film. If the user
	 * clicks OK, the changes are saved into the provided film object and true
	 * is returned.
	 * 
	 * @param person the film object to be edited
	 * @return true if the user clicked OK, false otherwise.
	 */
	public boolean showFilmEditDialog(FilmItem film, Boolean isNew) {
	    try {
	        // Load the fxml file and create a new stage for the popup dialog.
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(MainApp.class.getResource("view/FilmEditDialog.fxml"));
	        AnchorPane page = (AnchorPane) loader.load();

	        // Create the dialog Stage.
	        Stage dialogStage = new Stage();
	        dialogStage.setTitle(isNew ? "New Film" : "Edit Film");
	        dialogStage.getIcons().add(isNew ? newIcon : editIcon);
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(primaryStage);
	        Scene scene = new Scene(page);
	        dialogStage.setScene(scene);

	        // Set the film into the controller.
	        FilmEditDialogController controller = loader.getController();
	        controller.setDialogStage(dialogStage);
	        controller.setFilm(film);
	        controller.setMainApp(this);
	        controller.initializeGenreList();

	        // Show the dialog and wait until the user closes it
	        dialogStage.showAndWait();

	        return controller.isOkClicked();
	    } catch (IOException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	/**
	 * Returns the film file preference, i.e. the file that was last opened.
	 * The preference is read from the OS specific registry. If no such
	 * preference can be found, null is returned.
	 * 
	 * @return
	 */
	public File getFilmFilePath() {
	    Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	    String filePath = prefs.get("filePath", null);
	    if (filePath != null) {
	        return new File(filePath);
	    } else {
	        return null;
	    }
	}

	/**
	 * Sets the file path of the currently loaded file. The path is persisted in
	 * the OS specific registry.
	 * 
	 * @param file the file or null to remove the path
	 */
	public void setFilmFilePath(File file) {
	    Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	    if (file != null) {
	        prefs.put("filePath", file.getPath());

	        // Update the stage title.
	        primaryStage.setTitle("FinFilmApp - " + file.getName());
	    } else {
	        prefs.remove("filePath");

	        // Update the stage title.
	        primaryStage.setTitle("FinFilmApp");
	    }
	}
	
	/**
	 * Loads film data from the specified file. The current film data will
	 * be replaced.
	 * 
	 * @param file
	 */
	public void loadFilmDataFromFile(File file) {
	    try {
	        JAXBContext context = JAXBContext
	                .newInstance(FilmListWrapper.class);
	        Unmarshaller um = context.createUnmarshaller();

	        // Reading XML from the file and unmarshalling.
	        FilmListWrapper wrapper = (FilmListWrapper) um.unmarshal(file);

	        filmData.clear();
	        filmData.addAll(wrapper.getFilms());
	        
	        filteredData.clear();
	        filteredData.addAll(wrapper.getFilms());

	        // Save the file path to the registry.
	        setFilmFilePath(file);

	    } catch (Exception e) { // catches ANY exception
	        Dialogs.create()
	                .title("Error")
	                .masthead("Could not load data from file:\n" + file.getPath())
	                .showException(e);
	    }
	}

	/**
	 * Saves the current person data to the specified file.
	 * 
	 * @param file
	 */
	public void saveFilmDataToFile(File file) {
	    try {
	        JAXBContext context = JAXBContext
	                .newInstance(FilmListWrapper.class);
	        Marshaller m = context.createMarshaller();
	        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

	        // Wrapping our person data.
	        FilmListWrapper wrapper = new FilmListWrapper();
	        wrapper.setFilms(filmData);

	        // Marshalling and saving XML to the file.
	        m.marshal(wrapper, file);

	        // Save the file path to the registry.
	        setFilmFilePath(file);
	    } catch (Exception e) { // catches ANY exception
	        Dialogs.create().title("Error")
	                .masthead("Could not save data to file:\n" + file.getPath())
	                .showException(e);
	    }
	}

    public String getChoosedLocation() {
		return choosedLocation;
	}

	public void setChoosedLocation(String choosedLocation) {
		this.choosedLocation = choosedLocation;
	}
	
	public void saveAs() {
		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
				"XML files (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);

		// Show save file dialog
		File file = fileChooser.showSaveDialog(primaryStage);

		if (file != null) {
			// Make sure it has the correct extension
			if (!file.getPath().endsWith(".xml")) {
				file = new File(file.getPath() + ".xml");
			}
			saveFilmDataToFile(file);
		}
	}
	
	public void save() {
        File personFile = getFilmFilePath();
        if (personFile != null) {
            saveFilmDataToFile(personFile);
        } else {
            saveAs();
        }
    }
	
	public void open() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File file = fileChooser.showOpenDialog(getPrimaryStage());

        if (file != null) {
            loadFilmDataFromFile(file);
        }
    }

	public void newFile() {
        getFilmData().clear();
        setFilmFilePath(null);
    }

	public static void main(String[] args) {
        launch(args);
    }
}