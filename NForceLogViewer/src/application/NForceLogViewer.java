package application;
	

import java.io.File;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;


public class NForceLogViewer extends Application {
	private Log currLog;
	private String faultAnaSel;
	private String faultAnaData;
	@Override
	public void start(Stage primaryStage) {
		try {
			//Prompt user to select log file
			FileChooser chooseLog = new FileChooser();
			File setInit = new File("C:\\");
			chooseLog.setInitialDirectory(setInit);
			//Create VBox to put button and label in
			VBox mainVBox = new VBox();
			//Create label for main screen
			Label mainLab = new Label("Welcome To NForceLogViewer");
			//Create button for user to click on home page to start filechooser
			Button chooseLogBtn = new Button("Choose Log To View");
			chooseLogBtn.setMinSize(100, 100);
			chooseLogBtn.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						//Let user select desired log file to read
							File log = chooseLog.showOpenDialog(primaryStage);
						//Check to make sure user selected a file
							if (log != null) {
							//Create instance of Log class with selected log file
								currLog = new Log(log);
							viewLog();
							primaryStage.close();
						}
					}	
			});
			//Load label and button into vbox
			mainVBox.getChildren().addAll(mainLab,chooseLogBtn);
			mainVBox.setAlignment(Pos.TOP_CENTER);
			//Create pane and scene for main page
			StackPane pane = new StackPane();
			pane.getChildren().add(mainVBox);
			Scene scene = new Scene(pane,1200,600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setMaximized(true);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	//Set up main tabs and screen
	public void viewLog() {
		//Create stage to hold pane and root borderPane
		Stage logStage = new Stage();
		logStage.setMaximized(true);
		BorderPane root = new BorderPane();
		Scene logScene = new Scene(root,1500,600);
		logStage.setTitle("Log Viewer");
		//Create tabPane to hold tabs
		TabPane tabs = new TabPane();
		//Create tabs and set titles
		Tab tab1 = new Tab();
		tab1.setText("Fault Overview");
		Tab tab2 = new Tab();
		tab2.setText("Fault Details");
		//Get contect for tabs
		tab1.setContent(getFaultLog());
		tab2.setContent(getDataScreen());
		//Load tabs into tabPane
		tabs.getTabs().addAll(tab1, tab2);
		root.setCenter(tabs);
		//Create buttons for navigation
		Button backBtn = new Button("Choose New Log File");
		Button quitBtn = new Button("Quit");
		//Set back button for fault screens
		HBox lBox = new HBox();
		lBox.setPadding(new Insets(7,7,10,10));
		HBox.setHgrow(backBtn, Priority.ALWAYS);
		HBox.setHgrow(quitBtn, Priority.ALWAYS);
		backBtn.setMaxWidth(Double.MAX_VALUE);
		quitBtn.setMaxWidth(Double.MAX_VALUE);
		lBox.getChildren().addAll(backBtn,quitBtn);
		root.setTop(lBox);
		//Set action for back button
		backBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				start(new Stage());
				logStage.close();
			}	
		});
		//Set action for quit button
		quitBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				logStage.close();
			}	
		});
		//Set scene and display
		logStage.setScene(logScene);
		logStage.show();
	}
	
	//Set up and return pane containing list of faults
	public BorderPane getFaultLog() {
		//Create parent pane to hold all nodes
			BorderPane bPane = new BorderPane();
		//Set header for fault screen
			Label topLabel = new Label("Overview of Fault List");
			topLabel.setFont(new Font("Arial", 20));
			topLabel.setAlignment(Pos.CENTER);
			topLabel.prefWidthProperty().bind(bPane.widthProperty());
			bPane.setTop(topLabel);
		//Set fault list for fault screen
			bPane.setCenter(currLog.getFaultTable());
		return bPane;
		}
	//Get screen for viewing data analysis
	public BorderPane getDataScreen() {
		BorderPane root = new BorderPane();
		root.getStylesheets().add(NForceLogViewer.class.getResource("dataapplication.css").toExternalForm());
		//Create selectors for type of fault selection
		root.setTop(getQuerySelectors(root));
		//Get selectors for type of data to show
		root.setRight(getDataSelector(root));
		return root;
	}
	//Get selectors for type of fault query to make on analysis screen
	private HBox getQuerySelectors(BorderPane root) {
		ToggleGroup querySelectors = new ToggleGroup();
		RadioButton faultSelector = new RadioButton("Fault Description");
		faultSelector.setUserData("Fault Description");
		faultSelector.setToggleGroup(querySelectors);
		faultSelector.setPadding(new Insets(5,5,5,5));
		RadioButton freqSelector = new RadioButton("Fault Frequency");
		freqSelector.setUserData("Fault Frequency");
		freqSelector.setToggleGroup(querySelectors);
		freqSelector.setPadding(new Insets(5,5,5,5));
		HBox faultSelectorBox = new HBox(faultSelector, freqSelector);
		querySelectors.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
			public void changed(ObservableValue<? extends Toggle> arg0, Toggle arg1, Toggle arg2) {
				if (querySelectors.getSelectedToggle() != null) {
					if (querySelectors.getSelectedToggle().getUserData().toString().equals("Fault Description")) {
						HBox faultBoxWithDesc = new HBox();
						faultBoxWithDesc.getChildren().addAll(faultSelectorBox, getFaultDescSel(root));
						root.setTop(faultBoxWithDesc);
					} else if (querySelectors.getSelectedToggle().getUserData().toString().equals("Fault Frequency")) {
						HBox faultBoxWithDesc = new HBox();
						faultBoxWithDesc.getChildren().addAll(faultSelectorBox, getFaultFreqSel(root));
						root.setTop(faultBoxWithDesc);
					}
				}
			}
		});
		return faultSelectorBox;
	}
	//Get dropdown box for selecting faults by description
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private HBox getFaultDescSel(BorderPane root) {
		//Create HBox to hold fault selector info
		HBox faultSelectorBox = new HBox();
		ComboBox<String> faultDD = currLog.getFaultDropDown();
		faultDD.setPadding(new Insets(5,5,5,5));
		faultDD.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue arg0, Object arg1, Object arg2) {
				faultAnaSel = (String) arg0.getValue();
				//find fault description
				String[] tempSplit = faultAnaSel.split("[(]");
				faultAnaSel = tempSplit[0].trim();
				root.setCenter(getDataTable());
			}
		});
		faultSelectorBox.getChildren().add(faultDD);
		return faultSelectorBox;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private HBox getFaultFreqSel(BorderPane root) {
		//Create HBox to hold fault freq selector info
		HBox freqSelectorBox = new HBox();
		ComboBox<String> freqDD = currLog.getFreqDropDown();
		freqDD.setPadding(new Insets(5,5,5,5));
		freqDD.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue arg0, Object arg1, Object arg2) {
				faultAnaSel = (String) arg0.getValue();
				//find fault description
				String[] tempSplit = faultAnaSel.trim().split("[(]");
				tempSplit = tempSplit[1].trim().split("[)]");
				faultAnaSel = tempSplit[1].trim();
				root.setCenter(getDataTable());
			}
		});
		freqSelectorBox.getChildren().add(freqDD);
		return freqSelectorBox;
	}
	//Radio buttons for selecting what type of data to view
	private VBox getDataSelector(BorderPane root) {
		//Create VBox to hold data selectors
		VBox dataSelectors = new VBox();
		dataSelectors.setMinWidth(200);
		dataSelectors.setPadding(new Insets(20,20,20,20));
		//Create toggle group to hold radio buttons
		ToggleGroup dataSelectorGroup = new ToggleGroup();
		//Create and configure radio buttons for data selectors
		RadioButton battBtn = new RadioButton("Battery Data");
		battBtn.setPadding(new Insets(5,5,5,5));
		battBtn.setUserData("Battery Data");
		battBtn.setToggleGroup(dataSelectorGroup);
		RadioButton loadBtn = new RadioButton("Loading Data");
		loadBtn.setPadding(new Insets(5,5,5,5));
		loadBtn.setUserData("Loading Data");
		loadBtn.setToggleGroup(dataSelectorGroup);
		RadioButton tractBtn = new RadioButton("Traction Motor Data");
		tractBtn.setPadding(new Insets(5,5,5,5));
		tractBtn.setUserData("Traction Motor Data");
		tractBtn.setToggleGroup(dataSelectorGroup);
		RadioButton gs1Btn = new RadioButton("Genset 1 Data");
		gs1Btn.setPadding(new Insets(5,5,5,5));
		gs1Btn.setUserData("Genset 1 Data");
		gs1Btn.setToggleGroup(dataSelectorGroup);
		RadioButton gs2Btn = new RadioButton("Genset 2 Data");
		gs2Btn.setPadding(new Insets(5,5,5,5));
		gs2Btn.setUserData("Genset 2 Data");
		gs2Btn.setToggleGroup(dataSelectorGroup);
		RadioButton gs3Btn = new RadioButton("Genset 3 Data");
		gs3Btn.setPadding(new Insets(5,5,5,5));
		gs3Btn.setUserData("Genset 3 Data");
		gs3Btn.setToggleGroup(dataSelectorGroup);
		RadioButton allGSBtn = new RadioButton("All Genset Data");
		allGSBtn.setPadding(new Insets(5,5,5,5));
		allGSBtn.setUserData("All Genset Data");
		allGSBtn.setToggleGroup(dataSelectorGroup);
		RadioButton auxBtn = new RadioButton("Auxiliary System Data");
		auxBtn.setPadding(new Insets(5,5,5,5));
		auxBtn.setUserData("Auxiliary System Data");
		auxBtn.setToggleGroup(dataSelectorGroup);
		RadioButton compBtn = new RadioButton("Compressor Data");
		compBtn.setPadding(new Insets(5,5,5,5));
		compBtn.setUserData("Compressor Data");
		compBtn.setToggleGroup(dataSelectorGroup);
		RadioButton miscBtn = new RadioButton("Miscilanious Data");
		miscBtn.setPadding(new Insets(5,5,5,5));
		miscBtn.setUserData("Miscilanious Data");
		miscBtn.setToggleGroup(dataSelectorGroup);
		dataSelectorGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
			public void changed(ObservableValue<? extends Toggle> arg0, Toggle arg1, Toggle arg2) {
				if (dataSelectorGroup.getSelectedToggle() != null) {
					faultAnaData = arg2.getUserData().toString();
					root.setCenter(getDataTable());
				}
			}
		});
		dataSelectors.getChildren().addAll(battBtn, loadBtn, tractBtn, gs1Btn, gs2Btn, gs3Btn, allGSBtn,
				auxBtn, compBtn, miscBtn);
		return dataSelectors;
	}
	//Using both fault desc selection and data type selection, get table of data
	public TableView<Data> getDataTable() {
		if (faultAnaSel != null && faultAnaData != null) {
			return currLog.getDataTable(faultAnaSel, faultAnaData);
		}
		return null;
	}
	
}