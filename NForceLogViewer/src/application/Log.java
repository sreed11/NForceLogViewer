package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class Log {
	private File logFile;
	private List<Fault> faults = new ArrayList<Fault>();
	private FileReader logFileReader;
	private String logLine;
	private String faultLinePrev = "data";
	private String[] logLineSplit;
	private ObservableList<Fault> logObsList = FXCollections.observableArrayList();
	private Map<String, Integer> faultFreqMap = new HashMap<String,Integer>();
	private int version = 51;
	
	public Log(File logName) {
		this.logFile = logName;
	}
	//Produce list of Faults
	public void getFaultList() {
		try {
			//Read in selected file
			this.logFileReader = new FileReader(logFile);
			Scanner logScan = new Scanner(logFileReader);
			while (logScan.hasNextLine()) {
				this.logLine = logScan.nextLine();
				//trim whitespace from line of data
				this.logLine = logLine.replaceAll("\\s+","");
				//split variables into array
				this.logLineSplit = logLine.trim().split("[,]");
				if (this.logLineSplit[0].equals("DATE")) {
					if (logLineSplit.length == 172) {
						version = 53;
					}
				}
				//Ignore first line and anything less than 3 columns
				if (this.logLineSplit.length > 2 && !this.logLineSplit[0].equalsIgnoreCase("date")) {
					if(!this.logLineSplit[0].equals("data") && !this.logLineSplit[1].equals("data") &&
							!this.logLineSplit[2].equals("data") && this.faultLinePrev.equals("data")
							&& !this.logLineSplit[2].equals("none") && !(this.logLineSplit[2].length() < 4)) {
						this.faults.add(new Fault(this.logLineSplit));
						this.faults.get(faults.size()-1).addData(logLineSplit, version);
					} else if(!this.logLineSplit[0].equals("data") && !this.logLineSplit[1].equals("data") &&
							!this.logLineSplit[2].equals("data") && !this.logLineSplit[2].equals("none") 
							&& !(this.logLineSplit[2].length() < 4) && !faultLinePrev.equals("data")) {
						this.faults.get(faults.size()-1).addFault(logLineSplit[2]);
						this.faults.get(faults.size()-1).addData(logLineSplit, version);
					} else if(logLineSplit[2].equals("data") && !faults.isEmpty() && logLineSplit.length > 3) {
						this.faults.get(faults.size()-1).addData(logLineSplit, version);
					}
					this.faultLinePrev = logLineSplit[2];
				}
			}
			logScan.close();
			//Get fault frequency(number of times tripped) and load faults into ObservableList
			Set<String> tempSet;
			String temp;
			int x = 0;
			for (Fault f : faults) {
				logObsList.add(f);
				tempSet = faultFreqMap.keySet();
				temp = f.getFaultsSimp();
				if (tempSet.contains(temp)) {
					x = faultFreqMap.get(temp);
					x++;
					faultFreqMap.remove(temp);
					faultFreqMap.put(temp, x);
					} else {
						faultFreqMap.put(temp, 1);
					}
				}
			updateFaultFreqs();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	//Create fault table for fault summary
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public TableView<Fault> getFaultTable() {
		//Call method to fill list of Faults
		getFaultList();
		//Create table to populate fault table
		TableView<Fault> faultTable = new TableView<Fault>();
		faultTable.setEditable(true);
			//Create column for date of fault
			TableColumn dateCol = new TableColumn("Date");
			dateCol.setMinWidth(40);
			dateCol.setCellValueFactory(
					new PropertyValueFactory<Fault, String>("date")
					);
			//Create column for time of fault
			TableColumn timeCol = new TableColumn("Time");
			timeCol.setMinWidth(30);
			timeCol.setCellValueFactory(
					new PropertyValueFactory<Fault, String>("time")
					);
			//Create column for fault description
			TableColumn faultCol = new TableColumn("Fault Description");
			faultCol.setMinWidth(500);
			faultCol.setCellValueFactory(
					new PropertyValueFactory<Fault, String>("faultsSimp")
					);
			//Create column for fault frequency
			TableColumn freqCol = new TableColumn("Number Of Times Occured");
			freqCol.setMinWidth(30);
			freqCol.setCellValueFactory(
					new PropertyValueFactory<Fault, Integer>("faultFreq")
					);
		faultTable.setItems(logObsList);
		faultTable.getColumns().addAll(dateCol, timeCol, faultCol, freqCol);
		faultTable.setPadding(new Insets(10,10,10,10));
		faultTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		return faultTable;
	}
	//Create combobox for viewing analysis of fault by description
	public ComboBox<String> getFaultDropDown() {
		ObservableList<String> faultObsList = FXCollections.observableArrayList();
		for (Fault f : logObsList) {
			if (!faultObsList.contains(f.getFaultsSimp() + " (" + f.getFaultFreq() + ")")) {
				faultObsList.add(f.getFaultsSimp() + " (" + f.getFaultFreq() + ")");
			}
		}
		FXCollections.sort(faultObsList);
		final ComboBox<String>  faultDD = new ComboBox<String>(faultObsList);
		return faultDD;
	}
	//Create combobox for viewing analysis of fault by frequency
	public ComboBox<String> getFreqDropDown() {
		ObservableList<String> freqObsList = FXCollections.observableArrayList();
		for (Fault f : logObsList) {
			if (!freqObsList.contains("(" + f.getFaultFreq() + ") " + f.getFaultsSimp())) {
				freqObsList.add("(" + f.getFaultFreq() + ") " + f.getFaultsSimp());
			}
		}
		FXCollections.sort(freqObsList);
		FXCollections.reverse(freqObsList);
		final ComboBox<String>  freqDD = new ComboBox<String>(freqObsList);
		return freqDD;
	}
	//Method call to get frequency of each fault
	public void updateFaultFreqs() {
		for (Fault f : logObsList) {
			f.setFaultFreq(faultFreqMap.get(f.getFaultsSimp()));
		}
	}
	//Create table for viewing data for fault based on selections
	public TableView<Data> getDataTable(String fault, String dataSel) {
		ObservableList<Fault> dataObsList = FXCollections.observableArrayList();
		ObservableList<Data> dataObsFullList = FXCollections.observableArrayList();
		List<Data> dataListFull = new ArrayList<Data>();
		//Get observableList of Fault instances that contain selected fault
		for (Fault f : faults) {
			if (f.getFaultsSimp().equals(fault)) {
				dataObsList.add(f);
				//Fill observableList dataObsFullList with data from Faults
				dataListFull = f.getData();
				for (Data d : dataListFull) {
					dataObsFullList.add(d);
				}
			}
		}
		//Set up table with correct data variables
		TableView<Data> faultDataTable = new TableView<Data>();
		faultDataTable.setEditable(true);
		if (dataSel.equals("Compressor Data")) {
			faultDataTable = getCompTable(dataObsFullList);
		} else if (dataSel.equals("Loading Data")) {
			faultDataTable = getLoadTable(dataObsFullList);
		} else if (dataSel.equals("Traction Motor Data")) {
			faultDataTable = getTracTable(dataObsFullList);
		} else if (dataSel.equals("Genset 1 Data")) {
			faultDataTable = getGS1Table(dataObsFullList);
		} else if (dataSel.equals("Genset 2 Data")) {
			faultDataTable = getGS2Table(dataObsFullList);
		} else if (dataSel.equals("Genset 3 Data")) {
			faultDataTable = getGS3Table(dataObsFullList);
		} else if (dataSel.equals("All Genset Data")) {
			faultDataTable = getAllGSTable(dataObsFullList);
		} else if (dataSel.equals("Battery Data")) {
			faultDataTable = getBattTable(dataObsFullList);
		} else if (dataSel.equals("Auxiliary System Data")) {
			faultDataTable = getAuxTable(dataObsFullList);
		} else if (dataSel.equals("Miscilanious Data")) {
			faultDataTable = getMiscTable(dataObsFullList);
		}
		faultDataTable.setId("tables");
		return faultDataTable;
	}
	
	//Get table for viewing compressor data
	@SuppressWarnings("unchecked")
	private TableView<Data> getCompTable(ObservableList<Data> obsList) {
		TableView<Data> compTable = new TableView<Data>();
		compTable.setEditable(true);
		//Create general Columns for date and time
		//Create column for date of data
			TableColumn<Data,String> dateCol = new TableColumn<Data,String>("Date");
			dateCol.setMinWidth(40);
			dateCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("date")
					);
		//Create column for time of data
			TableColumn<Data,String> timeCol = new TableColumn<Data,String>("Time");
			timeCol.setMinWidth(30);
			timeCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("time")
					);
		
		//Create Columns for air compressor info
			//		Create column for Data BC (Brake Cylinder Pressure)
				TableColumn<Data,String> bcCol = 
						new TableColumn<Data,String>("Brake Cylinder Pressure (BC)");
				bcCol.setMinWidth(30);
				bcCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("bc")
						);
			//		Create column for Data MR (Main Res Pressure)
				TableColumn<Data,String> mrCol = 
						new TableColumn<Data,String>("Main Res Cylinder Pressure (MR)");
				mrCol.setMinWidth(30);
				mrCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("mr")
						);
			//		Create column for Data CTA (Air Compressor Input Amps)
				TableColumn<Data,String> ctaCol = 
						new TableColumn<Data,String>("Air Compressor Input Amps (CTA)");
				ctaCol.setMinWidth(30);
				ctaCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("cta")
						);
			//		Create column for Data CC1 (Compressor Control Contactor 1 Commanded)
				TableColumn<Data,String> cc1cCol = 
						new TableColumn<Data,String>("Compressor Control Contactor 1 Commanded (CC1)");
				cc1cCol.setMinWidth(30);
				cc1cCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("cc1c")
						);
			//		Create column for Data CC2 (Compressor Control Contactor 2 Commanded)
				TableColumn<Data,String> cc2cCol = 
						new TableColumn<Data,String>("Compressor Control Contactor 2 Commanded (CC2)");
				cc2cCol.setMinWidth(30);
				cc2cCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("cc2c")
						);
			//		Create column for Data CC3 (Compressor Control Contactor 3 Commanded)
				TableColumn<Data,String> cc3cCol = 
						new TableColumn<Data,String>("Compressor Control Contactor 3 Commanded (CC3)");
				cc3cCol.setMinWidth(30);
				cc3cCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("cc3c")
						);
			//		Create column for Data CC1 (Compressor Control Contactor 1 Status)
				TableColumn<Data,String> cc1sCol = 
						new TableColumn<Data,String>("Compressor Control Contactor 1 Status (CC1)");
				cc1sCol.setMinWidth(30);
				cc1sCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("cc1s")
						);
			//		Create column for Data CC2 (Compressor Control Contactor 2 Status)
				TableColumn<Data,String> cc2sCol = 
						new TableColumn<Data,String>("Compressor Control Contactor 2 Status (CC2)");
				cc2sCol.setMinWidth(30);
				cc2sCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("cc2s")
						);
			//		Create column for Data CC3 (Compressor Control Contactor 3 Status)
				TableColumn<Data,String> cc3sCol = 
						new TableColumn<Data,String>("Compressor Control Contactor 3 Status (CC3)");
				cc3sCol.setMinWidth(30);
				cc3sCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("cc3s")
						);
			//		Create column for Data MVCC (Compressor Control Mag Valve)
				TableColumn<Data,String> mvccCol = 
						new TableColumn<Data,String>("Compressor Control Mag Valve (MVCC)");
				mvccCol.setMinWidth(30);
				mvccCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("mvcc")
						);
			//		Create column for Data CRL  (Compressor Relay- Controls 22t)
				TableColumn<Data,String> crlCol = 
						new TableColumn<Data,String>("Compressor Relay- Controls 22t (CRL)");
				crlCol.setMinWidth(30);
				crlCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("crl")
						);
			//		Create column for Data PCR (PCS Reset Relay)
				TableColumn<Data,String> pcrCol = 
						new TableColumn<Data,String>("PCS Reset Relay (PCR)");
				pcrCol.setMinWidth(30);
				pcrCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("pcr")
						);
			//		Create column for Data 22T (Compressor Sync)
				TableColumn<Data,String> compSynCol = 
						new TableColumn<Data,String>("Compressor Trainline Sync (22T)");
				compSynCol.setMinWidth(30);
				compSynCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("compSyn")
						);
			//Populate compTable
				compTable.setItems(obsList);
				compTable.getColumns().addAll(dateCol, timeCol, bcCol, mrCol, ctaCol, cc1cCol, cc1sCol,
						cc2cCol, cc2sCol, cc3cCol, cc3sCol, mvccCol, crlCol, pcrCol, compSynCol);
		return compTable;
	}

	//Get table for viewing battery data
	@SuppressWarnings("unchecked")
	private TableView<Data> getBattTable(ObservableList<Data> obsList) {
		TableView<Data> battTable = new TableView<Data>();
		battTable.setEditable(true);
		//Create general Columns for date and time
		//Create column for date of data
			TableColumn<Data,String> dateCol = new TableColumn<Data,String>("Date");
			dateCol.setMinWidth(40);
			dateCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("date")
					);
		//Create column for time of data
			TableColumn<Data,String> timeCol = new TableColumn<Data,String>("Time");
			timeCol.setMinWidth(30);
			timeCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("time")
					);
		//Create Columns for battery info
			//		Create column for Data BATV (Main Battery Voltage)
				TableColumn<Data,String> batvCol = 
						new TableColumn<Data,String>("Main Battery Voltage (BATV)");
				batvCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("batv")
						);
			//		Create column for Data CTE (24v Battery Charging Amps)
				TableColumn<Data,String> cteCol = 
						new TableColumn<Data,String>("24v Battery Charging Amps (CTE)");
				cteCol.setMinWidth(40);
				cteCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("cte")
						);
			//		Create column for Data 24V (24v Voltage Present)
				TableColumn<Data,String> sbatvCol = 
						new TableColumn<Data,String>("24v Voltage Present (24V)");
				sbatvCol.setMinWidth(40);
				sbatvCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("sbatv")
						);
			//		Create column for Data LSC (Air Cond. Load Shed Contactor)
				TableColumn<Data,String> lscCol = 
						new TableColumn<Data,String>("Air Cond. Load Shed Contactor (LSC)");
				lscCol.setMinWidth(40);
				lscCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("lsc")
						);
			//		Create column for Data CTD (Main Battery Charging Amps)
				TableColumn<Data,String> ctdCol = 
						new TableColumn<Data,String>("Main Battery Charging Amps (CTD)");
				ctdCol.setMinWidth(40);
				ctdCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("ctd")
						);
			//		Create column for Data CTC (LVPS Input Amps)
				TableColumn<Data,String> ctcCol = 
						new TableColumn<Data,String>("LVPS Input Amps (CTC)");
				ctcCol.setMinWidth(40);
				ctcCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("ctc")
						);
			//Populate table
				battTable.setItems(obsList);
				battTable.getColumns().addAll(dateCol, timeCol, batvCol, ctcCol, ctdCol,cteCol, 
						sbatvCol, lscCol);
		return battTable;
	}
	
	//Get table for viewing loading data
	@SuppressWarnings("unchecked")
	private TableView<Data> getLoadTable(ObservableList<Data> obsList) {
		TableView<Data> loadTable = new TableView<Data>();
		loadTable.setEditable(true);
		//Create general Columns for date and time
		//Create column for date of data
			TableColumn<Data,String> dateCol = new TableColumn<Data,String>("Date");
			dateCol.setMinWidth(40);
			dateCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("date")
					);
		//Create column for time of data
			TableColumn<Data,String> timeCol = new TableColumn<Data,String>("Time");
			timeCol.setMinWidth(30);
			timeCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("time")
					);
		//Create Columns for loading info
			//		Create column for Data THROTTLE (Throttle Position- Notch)
				TableColumn<Data,String> throttleCol = 
						new TableColumn<Data,String>("Throttle Position- Notch (THROTTLE)");
				throttleCol.setMinWidth(40);
				throttleCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("throttle")
						);
			//		Create column for Data TMI (Actual Traction Motor Current- Average)
				TableColumn<Data,String> tmiCol = 
						new TableColumn<Data,String>("Actual Traction Motor Current- Average (TMI)");
				tmiCol.setMinWidth(40);
				tmiCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("tmi")
						);
			//Create column for Data CTF (Main DC Bus Amps)
				TableColumn<Data,String> ctfCol = 
						new TableColumn<Data,String>("Main DC Bus Amps (CTF)");
				ctfCol.setMinWidth(40);
				ctfCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("ctf")
						);
			//Create column for Data DCV (Main DC Bus Voltage)
				TableColumn<Data,String> dcvCol = 
						new TableColumn<Data,String>("Main DC Bus Voltage (DCV)");
				dcvCol.setMinWidth(40);
				dcvCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("dcv")
						);
			//Create column for Data RV-F (Reversor Forward)
				TableColumn<Data,String> fwdCol = 
						new TableColumn<Data,String>("Reversor Forward (RV-F)");
				fwdCol.setMinWidth(40);
				fwdCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("fwd")
						);
			//		Create column for Data 8T (Loco Direction Forward)
				TableColumn<Data,String> eighttCol = 
						new TableColumn<Data,String>("Loco Direction Forward (8T)");
				eighttCol.setMinWidth(40);
				eighttCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("eightt")
						);
			//		Create column for Data RV-R (Reversor Reverse)
				TableColumn<Data,String> revCol = 
						new TableColumn<Data,String>("Reversor Reverse (RV-R)");
				revCol.setMinWidth(40);
				revCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("rev")
						);			
			//		Create column for Data 9T (Loco Direction Reverse)
				TableColumn<Data,String> ninetCol = 
						new TableColumn<Data,String>("Loco Direction Reverse (9T)");
				ninetCol.setMinWidth(40);
				ninetCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("ninet")
						);
			//		Create column for Data SPX10 (Speed Times 10)
				TableColumn<Data,String> spdCol = 
						new TableColumn<Data,String>("Locomotive Speed(SPX10)");
				spdCol.setMinWidth(40);
				spdCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("spd")
						);
			//		Create column for Data IS (Isolation Switch)
				TableColumn<Data,String> isCol = 
						new TableColumn<Data,String>("Isolation Switch (IS)");
				isCol.setMinWidth(40);
				isCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("is")
						);
			//		Create column for Data 6T (Generator Field Request)
				TableColumn<Data,String> sixtCol = 
						new TableColumn<Data,String>("Generator Field Request (6T)");
				sixtCol.setMinWidth(40);
				sixtCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("sixt")
						);
			//		Create column for Data 16T(Engine Run Switch)
				TableColumn<Data,String> sixteentCol = 
						new TableColumn<Data,String>("Engine Run Switch (16T)");
				sixteentCol.setMinWidth(40);
				sixteentCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("sixteenT")
						);
			//		Create column for Data 10T(Wheel Slip)
				TableColumn<Data,String> tentCol = 
						new TableColumn<Data,String>("Wheel Slip (10T)");
				tentCol.setMinWidth(40);
				tentCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("tent")
						);
			//		Create column for Data 5T (Emergency Sand)
				TableColumn<Data,String> fivetCol = 
						new TableColumn<Data,String>("Emergency Sand (5T)");
				fivetCol.setMinWidth(40);
				fivetCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("fivet")
						);
			//		Create column for Data NSR (No Speed Relay Command)
				TableColumn<Data,String> nsrcCol = 
						new TableColumn<Data,String>("No Speed Relay Command (NSR)");
				nsrcCol.setMinWidth(40);
				nsrcCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("nsrc")
						);
			//		Create column for Data NSR (No Speed Relay Status)
				TableColumn<Data,String> nsrsCol = 
						new TableColumn<Data,String>("No Speed Relay Status (NSR)");
				nsrsCol.setMinWidth(40);
				nsrsCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("nsrs")
						);
			//		Create column for Data 23T(Manual Sand Control)
				TableColumn<Data,String> twentthreetCol = 
						new TableColumn<Data,String>("Manual Sand Control (23T)");
				twentthreetCol.setMinWidth(40);
				twentthreetCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("twentthreet")
						);
			//		Create column for Data RVR (Reversor Motor)
				TableColumn<Data,String> rvrCol = 
						new TableColumn<Data,String>("Reversor Motor (RVR)");
				rvrCol.setMinWidth(40);
				rvrCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("rvr")
						);
			//		Create column for Data ACCEL (Acceleration)
				TableColumn<Data,String> accelCol = 
						new TableColumn<Data,String>("Acceleration (ACCEL)");
				accelCol.setMinWidth(40);
				accelCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("accel")
						);
			//		Create column for Data WL (Wheel Slip Light)
				TableColumn<Data,String> wlCol = 
						new TableColumn<Data,String>("Wheel Slip Light (WL)");
				wlCol.setMinWidth(40);
				wlCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("wl")
						);
			//		Create column for Data MV2SR (Rear Sanding Mag Valve)
				TableColumn<Data,String> mvrCol = 
						new TableColumn<Data,String>("Rear Sanding Mag Valve (MV2SR)");
				mvrCol.setMinWidth(40);
				mvrCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("mvr")
						);
			//		Create column for Data MV1SF (Front Sanding Mag Valve)
				TableColumn<Data,String> mvfCol = 
						new TableColumn<Data,String>("Front Sanding Mag Valve (MV1SF)");
				mvfCol.setMinWidth(40);
				mvfCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("mvf")
						);
			//		Create column for Data AHP (Available Horsepower)
				TableColumn<Data,String> ahpCol = 
						new TableColumn<Data,String>("Available Horsepower (AHP)");
				ahpCol.setMinWidth(40);
				ahpCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("ahp")
						);
			//		Create column for Data BHP (Break Horsepower)
				TableColumn<Data,String> bhpCol = 
						new TableColumn<Data,String>("Break Horsepower (BHP)");
				bhpCol.setMinWidth(40);
				bhpCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("bhp")
						);
			//		Create column for Data IDEAL (Ideal Traction Motor Current)
				TableColumn<Data,String> idealCol = 
						new TableColumn<Data,String>("Ideal Traction Motor Current (IDEAL)");
				idealCol.setMinWidth(40);
				idealCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("ideal")
						);
			//		Create column for Data THP (Tractive Horsepower)
				TableColumn<Data,String> thpCol = 
						new TableColumn<Data,String>("Tractive Horsepower (THP)");
				thpCol.setMinWidth(40);
				thpCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("thp")
						);
			//		Create column for Data GR (Ground Relay Tripped)
				TableColumn<Data,String> grCol = 
						new TableColumn<Data,String>("Ground Relay Tripped (gr)");
				grCol.setMinWidth(40);
				grCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("gr")
						);
			//		Create column for Data GRCO (Ground Relay Cutout)
				TableColumn<Data,String> grcoCol = 
						new TableColumn<Data,String>("Ground Relay Cutout (GRCO)");
				grcoCol.setMinWidth(40);
				grcoCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("grco")
						);
			//		Create column for Data GR (Ground Relay Reset)
				TableColumn<Data,String> grresCol = 
						new TableColumn<Data,String>("Ground Relay Reset (GR)");
				grresCol.setMinWidth(40);
				grresCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("grres")
						);
			//		Create column for Data LDSND (Lead Sand Switch)
				TableColumn<Data,String> ldsndCol = 
						new TableColumn<Data,String>("Lead Sand Switch (LDSND)");
				ldsndCol.setMinWidth(40);
				ldsndCol.setCellValueFactory(
						new PropertyValueFactory<Data,String>("ldsnd")
						);
			//Populate table
				loadTable.setItems(obsList);
				loadTable.getColumns().addAll(dateCol,timeCol,throttleCol,tmiCol,ctfCol,
						dcvCol,fwdCol,eighttCol,revCol,ninetCol,spdCol,isCol,sixtCol,
						sixteentCol,tentCol,fivetCol,nsrcCol,nsrsCol,twentthreetCol,rvrCol,
						accelCol,wlCol,mvrCol,mvfCol,ahpCol,bhpCol,idealCol,thpCol,grCol,grcoCol,
						grresCol,ldsndCol);
				return loadTable;
	}
	
	//Get table for viewing traction motor data
	@SuppressWarnings("unchecked")
	private TableView<Data> getTracTable(ObservableList<Data> obsList) {
		TableView<Data> tracTable = new TableView<Data>();
		tracTable.setEditable(true);
		//Create general Columns for date and time
		//Create column for date of data
			TableColumn<Data,String> dateCol = new TableColumn<Data,String>("Date");
			dateCol.setMinWidth(40);
			dateCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("date")
					);
		//Create column for time of data
			TableColumn<Data,String> timeCol = new TableColumn<Data,String>("Time");
			timeCol.setMinWidth(30);
			timeCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("time")
					);
		//	Create Columns for Traction Motor Info
			//		Create column for Data TMI (Actual Traction Motor Current- Average)
				TableColumn<Data,String> tmiCol = 
						new TableColumn<Data,String>("Actual Traction Motor Current- Average (TMI)");
				tmiCol.setMinWidth(30);
				tmiCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("tmi")
						);
			//		Create column for Data THROTTLE (Throttle Position- Notch)
				TableColumn<Data,String> throttleCol = 
						new TableColumn<Data,String>("Throttle Position- Notch (THROTTLE)");
				throttleCol.setMinWidth(30);
				throttleCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("throttle")
						);
			//		Create column for Data EXC LIMIT (Excitation Limit Reason)
				TableColumn<Data,String> exclimCol = 
						new TableColumn<Data,String>("Excitation Limit Reason (EXC LIMIT)");
				exclimCol.setMinWidth(30);
				exclimCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("exclim")
						);
			//		Create column for Data MFR (Motor Data Relay)
				TableColumn<Data,String> mfrCol = 
						new TableColumn<Data,String>("Motor Fault Relay (MFR)");
				mfrCol.setMinWidth(30);
				mfrCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("mfr")
						);
			//		Create column for Data TMV1 (Traction Motor 1 Voltage)
				TableColumn<Data,String> tmvoneCol = 
						new TableColumn<Data,String>("Traction Motor 1 Voltage (TMV1)");
				tmvoneCol.setMinWidth(30);
				tmvoneCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("tmvone")
						);
			//		Create column for Data TMI1 (Traction Motor 1 Amps)
				TableColumn<Data,String> tmioneCol = 
						new TableColumn<Data,String>("Traction Motor 1 Amps (TMI1)");
				tmioneCol.setMinWidth(30);
				tmioneCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("tmione")
						);
			//		Create column for Data DUTY1 (Chopper 1 Duty Cycle)
				TableColumn<Data,String> dutyoneCol = 
						new TableColumn<Data,String>("Chopper 1 Duty Cycle (DUTY1)");
				dutyoneCol.setMinWidth(30);
				dutyoneCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("dutyone")
						);
			//		Create column for Data TMV2 (Traction Motor 2 Voltage)
				TableColumn<Data,String> tmvtwoCol = 
						new TableColumn<Data,String>("Traction Motor 2 Voltage (TMV2)");
				tmvtwoCol.setMinWidth(30);
				tmvtwoCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("tmvtwo")
						);
			//		Create column for Data TMI2 (Traction Motor 2 Amps)
				TableColumn<Data,String> tmitwoCol = 
						new TableColumn<Data,String>("Traction Motor 2 Amps (TMI2)");
				tmitwoCol.setMinWidth(30);
				tmitwoCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("tmitwo")
						);
			//		Create column for Data DUTY2 (Chopper 2 Duty Cycle)
				TableColumn<Data,String> dutytwoCol = 
						new TableColumn<Data,String>("Chopper 2 Duty Cycle (DUTY2)");
				dutytwoCol.setMinWidth(30);
				dutytwoCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("dutytwo")
						);
			//		Create column for TMV3 (Traction Motor 3 Voltage)
				TableColumn<Data,String> tmvthreeCol = 
						new TableColumn<Data,String>("Traction Motor 3 Voltage (TMV3)");
				tmvthreeCol.setMinWidth(30);
				tmvthreeCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("tmvthree")
						);
			//		Create column for Data TMI3 (Traction Motor 3 Amps)
				TableColumn<Data,String> tmithreeCol = 
						new TableColumn<Data,String>("Traction Motor 3 Amps (TMI3)");
				tmithreeCol.setMinWidth(30);
				tmithreeCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("tmithree")
						);
			//		Create column for Data DUTY3 (Chopper 3 Duty Cycle)
				TableColumn<Data,String> dutythreeCol = 
						new TableColumn<Data,String>("Chopper 3 Duty Cycle (DUTY3)");
				dutythreeCol.setMinWidth(30);
				dutythreeCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("dutythree")
						);
			//		Create column for Data TMV4 (Traction Motor 4 Voltage)
				TableColumn<Data,String> tmvfourCol = 
						new TableColumn<Data,String>("Traction Motor 4 Voltage (TMV4)");
				tmvfourCol.setMinWidth(30);
				tmvfourCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("tmvfour")
						);
			//		Create column for Data TMI4 (Traction Motor 4 Amps)
				TableColumn<Data,String> tmifourCol = 
						new TableColumn<Data,String>("Traction Motor 4 Amps (TMI4)");
				tmifourCol.setMinWidth(30);
				tmifourCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("tmifour")
						);
			//		Create column for Data DUTY4 (Chopper 4 Duty Cycle)
				TableColumn<Data,String> dutyfourCol = 
						new TableColumn<Data,String>("Chopper 4 Duty Cycle (DUTY4)");
				dutyfourCol.setMinWidth(30);
				dutyfourCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("dutyfour")
						);
			//		Create column for Data M (Motoring Contactors Commanded)
				TableColumn<Data,String> mCol = 
						new TableColumn<Data,String>("Motoring Contactors Commanded (M)");
				mCol.setMinWidth(30);
				mCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("m")
						);
			//		Create column for Data MTR (MTR Relay Command)
				TableColumn<Data,String> mtrCol = 
						new TableColumn<Data,String>("MTR Relay Command (MTR)");
				mtrCol.setMinWidth(30);
				mtrCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("mtr")
						);
			//		Create column for Data P1 (Power Contactor 1 Commanded)
				TableColumn<Data,String> ponecCol = 
						new TableColumn<Data,String>("Power Contactor 1 Commanded (P1)");
				ponecCol.setMinWidth(30);
				ponecCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("ponec")
						);
			//		Create column for Data P2 (Power Contactor 2 Commanded)
				TableColumn<Data,String> ptwocCol = 
						new TableColumn<Data,String>("Power Contactor 2 Commanded (P2)");
				ptwocCol.setMinWidth(30);
				ptwocCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("ptwoc")
						);
			//		Create column for Data P3 (Power Contactor 3 Commanded)
				TableColumn<Data,String> pthreecCol = 
						new TableColumn<Data,String>("Power Contactor 3 Commanded (P3)");
				pthreecCol.setMinWidth(30);
				pthreecCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("pthreec")
						);
			//		Create column for Data P4 (Power Contactor 4 Commanded)
				TableColumn<Data,String> pfourcCol = 
						new TableColumn<Data,String>("Power Contactor 4 Commanded (P4)");
				pfourcCol.setMinWidth(30);
				pfourcCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("pfourc")
						);
			//		Create column for Data P1 (Power Contactor 1 Status)
				TableColumn<Data,String> ponesCol = 
						new TableColumn<Data,String>("Power Contactor 1 Status (P1)");
				ponesCol.setMinWidth(30);
				ponesCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("pones")
						);
			//		Create column for Data P2 (Power Contactor 2 Status)
				TableColumn<Data,String> ptwosCol = 
						new TableColumn<Data,String>("Power Contactor 2 Status (P2)");
				ptwosCol.setMinWidth(30);
				ptwosCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("ptwos")
						);
			//		Create column for Data P3 (Power Contactor 3 Status)
				TableColumn<Data,String> pthreesCol = 
						new TableColumn<Data,String>("Power Contactor 3 Status (P3)");
				pthreesCol.setMinWidth(30);
				pthreesCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("pthrees")
						);
			//		Create column for Data P4 (Power Contactor 4 Status)
				TableColumn<Data,String> pfoursCol = 
						new TableColumn<Data,String>("Power Contactor 4 Status (P4)");
				pfoursCol.setMinWidth(30);
				pfoursCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("pfours")
						);
			//		Create column for Data MCOS1 (Traction Motor 1 Cutout Switch)
				TableColumn<Data,String> mcosoneCol = 
						new TableColumn<Data,String>("Traction Motor 1 Cutout Switch (MCOS1)");
				mcosoneCol.setMinWidth(30);
				mcosoneCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("mcosone")
						);
			//		Create column for Data MCOS2 (Traction Motor 2 Cutout Switch)
				TableColumn<Data,String> mcostwoCol = 
						new TableColumn<Data,String>("Traction Motor 2 Cutout Switch (MCOS2)");
				mcostwoCol.setMinWidth(30);
				mcostwoCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("mcostwo")
						);
			//		Create column for Data MCOS3 (Traction Motor 3 Cutout Switch)
				TableColumn<Data,String> mcosthreeCol = 
						new TableColumn<Data,String>("Traction Motor 3 Cutout Switch (MCOS3)");
				mcosthreeCol.setMinWidth(30);
				mcosthreeCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("mcosthree")
						);
			//		Create column for Data MCOS4 (Traction Motor 4 Cutout Switch)
				TableColumn<Data,String> mcosfourCol = 
						new TableColumn<Data,String>("Traction Motor 4 Cutout Switch (MCOS4)");
				mcosfourCol.setMinWidth(30);
				mcosfourCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("mcosfour")
						);
			//		Create column for Data MCO1 (Traction Motor 1 Cutout Command)
				TableColumn<Data,String> mcoonecCol = 
						new TableColumn<Data,String>("Traction Motor 1 Cutout Command (MCO1)");
				mcoonecCol.setMinWidth(30);
				mcoonecCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("mcoonec")
						);
			//		Create column for Data MCO2 (Traction Motor 2 Cutout Command)
				TableColumn<Data,String> mcotwocCol = 
						new TableColumn<Data,String>("Traction Motor 2 Cutout Command (MCO2)");
				mcotwocCol.setMinWidth(30);
				mcotwocCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("mcotwoc")
						);
			//		Create column for Data MCO3 (Traction Motor 3 Cutout Command)
				TableColumn<Data,String> mcothreecCol = 
						new TableColumn<Data,String>("Traction Motor 3 Cutout Command (MCO3)");
				mcothreecCol.setMinWidth(30);
				mcothreecCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("mcothreec")
						);
			//		Create column for Data MCO4 (Traction Motor 4 Cutout Command)
				TableColumn<Data,String> mcofourcCol = 
						new TableColumn<Data,String>("Traction Motor 4 Cutout Command (MCO4)");
				mcofourcCol.setMinWidth(30);
				mcofourcCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("mcofourc")
						);
			//		Create column for Data MCO1 (Traction Motor 1 Cutout)
				TableColumn<Data,String> mcoonesCol = 
						new TableColumn<Data,String>("Traction Motor 1 Cutout Status (MCO1)");
				mcoonesCol.setMinWidth(30);
				mcoonesCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("mcoones")
						);
			//		Create column for Data MCO2 (Traction Motor 2 Cutout)
				TableColumn<Data,String> mcotwosCol = 
						new TableColumn<Data,String>("Traction Motor 2 Cutout Status (MCO2)");
				mcotwosCol.setMinWidth(30);
				mcotwosCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("mcotwos")
						);
			//		Create column for Data MCO3 (Traction Motor 3 Cutout)
				TableColumn<Data,String> mcothreesCol = 
						new TableColumn<Data,String>("Traction Motor 3 Cutout Status (MCO3)");
				mcothreesCol.setMinWidth(30);
				mcothreesCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("mcothrees")
						);
			//		Create column for Data MCO4 (Traction Motor 4 Cutout)
				TableColumn<Data,String> mcofoursCol = 
						new TableColumn<Data,String>("Traction Motor 4 Cutout Status (MCO4)");
				mcofoursCol.setMinWidth(30);
				mcofoursCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("mcofours")
						);
			//		Create column for Data C1FAI (Chopper 1 Failed)
				TableColumn<Data,String> conefaiCol = 
						new TableColumn<Data,String>("Chopper 1 Failed (C1FAI)");
				conefaiCol.setMinWidth(30);
				conefaiCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("conefai")
						);
			//		Create column for Data C1FLT (Chopper 1 Fault)
				TableColumn<Data,String> conefltCol = 
						new TableColumn<Data,String>("Chopper 1 Fault (C1FLT)");
				conefltCol.setMinWidth(30);
				conefltCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("coneflt")
						);
			//		Create column for Data C2FAI (Chopper 2 Failed)
				TableColumn<Data,String> ctwofaiCol = 
						new TableColumn<Data,String>("Chopper 2 Failed (C2FAI)");
				ctwofaiCol.setMinWidth(30);
				ctwofaiCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("ctwofai")
						);
			//		Create column for Data C2FLT (Chopper 2 Fault)
				TableColumn<Data,String> ctwofltCol = 
						new TableColumn<Data,String>("Chopper 2 Fault (C2FLT)");
				ctwofltCol.setMinWidth(30);
				ctwofltCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("ctwoflt")
						);
			//		Create column for fault C3FAI (Chopper 3 Failed)
				TableColumn<Data,String> cthreefaiCol = 
						new TableColumn<Data,String>("Chopper 3 Failed (C3FAI)");
				cthreefaiCol.setMinWidth(30);
				cthreefaiCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("cthreefai")
						);
			//		Create column for Data C3FLT (Chopper 3 Fault)
				TableColumn<Data,String> cthreefltCol = 
						new TableColumn<Data,String>("Chopper 3 Fault (C3FLT)");
				cthreefltCol.setMinWidth(30);
				cthreefltCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("cthreeflt")
						);
			//		Create column for Data C4FAI (Chopper 4 Failed)
				TableColumn<Data,String> cfourfaiCol = 
						new TableColumn<Data,String>("Chopper 4 Failed (C4FAI)");
				cfourfaiCol.setMinWidth(30);
				cfourfaiCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("cfourfai")
						);
			//		Create column for Data C4FLT (Chopper 4 Fault)
				TableColumn<Data,String> cfourfltCol = 
						new TableColumn<Data,String>("Chopper 4 Fault (C4FLT)");
				cfourfltCol.setMinWidth(30);
				cfourfltCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("cfourflt")
						);
			//		Create column for Data CPROG (Chopper Programming)
				TableColumn<Data,String> cprogCol = 
						new TableColumn<Data,String>("Chopper Programming (CPROG)");
				cprogCol.setMinWidth(30);
				cprogCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("cprog")
						);
			//		Create column for Data CHCB (Chopper Control Breaker)
				TableColumn<Data,String> chcbCol = 
						new TableColumn<Data,String>("Chopper Control Breaker (CHCB)");
				chcbCol.setMinWidth(30);
				chcbCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("chcb")
						);
				tracTable.setItems(obsList);
				tracTable.getColumns().addAll(dateCol,timeCol,tmiCol,throttleCol,exclimCol,
						mfrCol,tmvoneCol,tmioneCol,dutyoneCol,tmvtwoCol,tmitwoCol,dutytwoCol,
						tmvthreeCol,tmithreeCol,dutythreeCol,tmvfourCol,tmifourCol,dutyfourCol,
						mCol,mtrCol,ponecCol,ptwocCol,pthreecCol,pfourcCol,ponesCol,ptwosCol,
						pthreesCol,pfoursCol,mcosoneCol,mcostwoCol,mcosthreeCol,mcosfourCol,
						mcoonecCol,mcotwocCol,mcothreecCol,mcofourcCol,mcoonesCol,mcotwosCol,
						mcothreesCol,mcofoursCol,conefaiCol,conefltCol,ctwofaiCol,ctwofltCol,
						cthreefaiCol,cthreefltCol,cfourfaiCol,cfourfltCol,cprogCol,chcbCol);
		return tracTable;
	}
	
	//Get table for viewing GS1 data
	@SuppressWarnings("unchecked")
	private TableView<Data> getGS1Table(ObservableList<Data> obsList) {
			TableView<Data> gs1Table = new TableView<Data>();
			gs1Table.setEditable(true);
			//Create general Columns for date and time
			//Create column for date of data
				TableColumn<Data,String> dateCol = new TableColumn<Data,String>("Date");
				dateCol.setMinWidth(40);
				dateCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("date")
						);
			//Create column for time of data
				TableColumn<Data,String> timeCol = new TableColumn<Data,String>("Time");
				timeCol.setMinWidth(30);
				timeCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("time")
						);
		//Create Columns for GS1 info
			//		Create column for Data G1RPM (Genset 1 Engine RPM)
				TableColumn<Data,String> gsarpmCol = 
						new TableColumn<Data,String>("Genset 1 Engine RPM (G1RPM)");
				gsarpmCol.setMinWidth(30);
				gsarpmCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("gsarpm")
						);
			//		Create column for Data G1VRO (Genset 1 Voltage Regulator Enable Relay)
				TableColumn<Data,String> gsavroCol = 
						new TableColumn<Data,String>("Genset 1 Voltage Regulator Enable Relay (G1VRO");
				gsavroCol.setMinWidth(30);
				gsavroCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("gsavro")
						);
			//		Create column for Data G1FLT (Genset 1 Fault)
				TableColumn<Data,String> gsafltCol = 
						new TableColumn<Data,String>("Genset 1 Fault (G1FLT)");
				gsafltCol.setMinWidth(30);
				gsafltCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("gsaflt")
						);
			//		Create column for Data G1SRV (Genset 1 Service Engine)
				TableColumn<Data,String> gsasrvCol = 
						new TableColumn<Data,String>("Genset 1 Service Engine (G1SRV)");
				gsasrvCol.setMinWidth(30);
				gsasrvCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("gsasrv")
						);
			//		Create column for Data GS1BK (Genset 1 Main Breaker Status)
				TableColumn<Data,String> gsabrkCol = 
						new TableColumn<Data,String>("Genset 1 Main Breaker Status (GS1BK)");
				gsabrkCol.setMinWidth(30);
				gsabrkCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("gsabrk")
						);
			//		Create column for Data G1EE (Genset 1 Engine Enable Relay)
				TableColumn<Data,String> gsaeeCol = 
						new TableColumn<Data,String>("Genset 1 Engine Enable Relay (G1EE)");
				gsaeeCol.setMinWidth(30);
				gsaeeCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("gsaee")
						);
			//		Create column for Data G1ST (Genset 1 Start Relay)
				TableColumn<Data,String> gsastCol = 
						new TableColumn<Data,String>("Genset 1 Start Relay (G1ST)");
				gsastCol.setMinWidth(30);
				gsastCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("gsast")
						);
			//		Create column for Data G1P1I (Genset 1 Phase 1 Output)
				TableColumn<Data,String> gsapoiCol = 
						new TableColumn<Data,String>("Genset 1 Phase 1 Output (G1P1I)");
				gsapoiCol.setMinWidth(30);
				gsapoiCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("gsapoi")
						);
			//		Create column for Data G1P3I (Genset 1 Phase 3 Output)
				TableColumn<Data,String> gsaptiCol = 
						new TableColumn<Data,String>("Genset 1 Phase 3 Output (G1P3I)");
				gsaptiCol.setMinWidth(30);
				gsaptiCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("gsapti")
						);
			//		Create column for Data G1OIL (Genset 1 Oil Pressure)
				TableColumn<Data,String> gsaoilCol = 
						new TableColumn<Data,String>("Genset 1 Oil Pressure (G1OIL)");
				gsaoilCol.setMinWidth(30);
				gsaoilCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("gsaoil")
						);
			//		Create column for Data G1H2O (Genset 1 Water Temperature)
				TableColumn<Data,String> gsawatCol = 
						new TableColumn<Data,String>("Genset 1 Water Temperature (G1H20)");
				gsawatCol.setMinWidth(30);
				gsawatCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("gsawat")
						);
				gs1Table.setItems(obsList);
				gs1Table.getColumns().addAll(dateCol,timeCol,gsarpmCol,gsavroCol,gsafltCol,
						gsasrvCol,gsabrkCol,gsaeeCol,gsastCol,gsapoiCol,gsaptiCol,gsaoilCol,
						gsawatCol);
				return gs1Table;
	}
	
	//Get table for viewing GS2 data
	@SuppressWarnings("unchecked")
	private TableView<Data> getGS2Table(ObservableList<Data> obsList) {
		TableView<Data> gs2Table = new TableView<Data>();
		gs2Table.setEditable(true);
		//Create general Columns for date and time
		//Create column for date of data
			TableColumn<Data,String> dateCol = new TableColumn<Data,String>("Date");
			dateCol.setMinWidth(40);
			dateCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("date")
					);
		//Create column for time of data
			TableColumn<Data,String> timeCol = new TableColumn<Data,String>("Time");
			timeCol.setMinWidth(30);
			timeCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("time")
					);
	//Create Columns for GS2 info
		//		Create column for Data G2RPM (Genset 2 Engine RPM)
			TableColumn<Data,String> gsbrpmCol = 
					new TableColumn<Data,String>("Genset 2 Engine RPM (G2RPM)");
			gsbrpmCol.setMinWidth(30);
			gsbrpmCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("gsbrpm")
					);
		//		Create column for Data G2VRO (Genset 2 Voltage Regulator Enable Relay)
			TableColumn<Data,String> gsbvroCol = 
					new TableColumn<Data,String>("Genset 2 Voltage Regulator Enable Relay (G2VRO");
			gsbvroCol.setMinWidth(30);
			gsbvroCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("gsbvro")
					);
		//		Create column for Data G2FLT (Genset 2 Fault)
			TableColumn<Data,String> gsbfltCol = 
					new TableColumn<Data,String>("Genset 2 Fault (G2FLT)");
			gsbfltCol.setMinWidth(30);
			gsbfltCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("gsbflt")
					);
		//		Create column for Data G2SRV (Genset 2 Service Engine)
			TableColumn<Data,String> gsbsrvCol = 
					new TableColumn<Data,String>("Genset 2 Service Engine (G2SRV)");
			gsbsrvCol.setMinWidth(30);
			gsbsrvCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("gsbsrv")
					);
		//		Create column for Data GS2BK (Genset 2 Main Breaker Status)
			TableColumn<Data,String> gsbbrkCol = 
					new TableColumn<Data,String>("Genset 2 Main Breaker Status (GS2BK)");
			gsbbrkCol.setMinWidth(30);
			gsbbrkCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("gsbbrk")
					);
		//		Create column for Data G2EE (Genset 2 Engine Enable Relay)
			TableColumn<Data,String> gsbeeCol = 
					new TableColumn<Data,String>("Genset 2 Engine Enable Relay (G2EE)");
			gsbeeCol.setMinWidth(30);
			gsbeeCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("gsbee")
					);
		//		Create column for Data G2ST (Genset 2 Start Relay)
			TableColumn<Data,String> gsbstCol = 
					new TableColumn<Data,String>("Genset 2 Start Relay (G2ST)");
			gsbstCol.setMinWidth(30);
			gsbstCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("gsbst")
					);
		//		Create column for Data G2P1I (Genset 2 Phase 1 Output)
			TableColumn<Data,String> gsbpoiCol = 
					new TableColumn<Data,String>("Genset 2 Phase 1 Output (G2P1I)");
			gsbpoiCol.setMinWidth(30);
			gsbpoiCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("gsbpoi")
					);
		//		Create column for Data G2P3I (Genset 2 Phase 3 Output)
			TableColumn<Data,String> gsbptiCol = 
					new TableColumn<Data,String>("Genset 2 Phase 3 Output (G2P3I)");
			gsbptiCol.setMinWidth(30);
			gsbptiCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("gsbpti")
					);
		//		Create column for Data G2OIL (Genset 2 Oil Pressure)
			TableColumn<Data,String> gsboilCol = 
					new TableColumn<Data,String>("Genset 2 Oil Pressure (G2OIL)");
			gsboilCol.setMinWidth(30);
			gsboilCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("gsboil")
					);
		//		Create column for Data G2H2O (Genset 2 Water Temperature)
			TableColumn<Data,String> gsbwatCol = 
					new TableColumn<Data,String>("Genset 2 Water Temperature (G2H20)");
			gsbwatCol.setMinWidth(30);
			gsbwatCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("gsbwat")
					);
			gs2Table.setItems(obsList);
			gs2Table.getColumns().addAll(dateCol,timeCol,gsbrpmCol,gsbvroCol,gsbfltCol,
					gsbsrvCol,gsbbrkCol,gsbeeCol,gsbstCol,gsbpoiCol,gsbptiCol,gsboilCol,
					gsbwatCol);
			return gs2Table;
	}
	
	//Get table for viewing GS3 data
	@SuppressWarnings("unchecked")
	private TableView<Data> getGS3Table(ObservableList<Data> obsList) {
		TableView<Data> gs3Table = new TableView<Data>();
		gs3Table.setEditable(true);
		//Create general Columns for date and time
		//Create column for date of data
			TableColumn<Data,String> dateCol = new TableColumn<Data,String>("Date");
			dateCol.setMinWidth(40);
			dateCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("date")
					);
		//Create column for time of data
			TableColumn<Data,String> timeCol = new TableColumn<Data,String>("Time");
			timeCol.setMinWidth(30);
			timeCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("time")
					);
	//Create Columns for GS3 info
		//		Create column for Data G3RPM (Genset 3 Engine RPM)
			TableColumn<Data,String> gscrpmCol = 
					new TableColumn<Data,String>("Genset 3 Engine RPM (G3RPM)");
			gscrpmCol.setMinWidth(30);
			gscrpmCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("gscrpm")
					);
		//		Create column for Data G3VRO (Genset 3 Voltage Regulator Enable Relay)
			TableColumn<Data,String> gscvroCol = 
					new TableColumn<Data,String>("Genset 3 Voltage Regulator Enable Relay (G3VRO");
			gscvroCol.setMinWidth(30);
			gscvroCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("gscvro")
					);
		//		Create column for Data G3FLT (Genset 3 Fault)
			TableColumn<Data,String> gscfltCol = 
					new TableColumn<Data,String>("Genset 3 Fault (G3FLT)");
			gscfltCol.setMinWidth(30);
			gscfltCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("gscflt")
					);
		//		Create column for Data G3SRV (Genset 3 Service Engine)
			TableColumn<Data,String> gscsrvCol = 
					new TableColumn<Data,String>("Genset 3 Service Engine (G3SRV)");
			gscsrvCol.setMinWidth(30);
			gscsrvCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("gscsrv")
					);
		//		Create column for Data GS3BK (Genset 3 Main Breaker Status)
			TableColumn<Data,String> gscbrkCol = 
					new TableColumn<Data,String>("Genset 3 Main Breaker Status (GS3BK)");
			gscbrkCol.setMinWidth(30);
			gscbrkCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("gscbrk")
					);
		//		Create column for Data G3EE (Genset 3 Engine Enable Relay)
			TableColumn<Data,String> gsceeCol = 
					new TableColumn<Data,String>("Genset 3 Engine Enable Relay (G3EE)");
			gsceeCol.setMinWidth(30);
			gsceeCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("gscee")
					);
		//		Create column for Data G3ST (Genset 3 Start Relay)
			TableColumn<Data,String> gscstCol = 
					new TableColumn<Data,String>("Genset 3 Start Relay (G3ST)");
			gscstCol.setMinWidth(30);
			gscstCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("gscst")
					);
		//		Create column for Data G3P1I (Genset 3 Phase 1 Output)
			TableColumn<Data,String> gscpoiCol = 
					new TableColumn<Data,String>("Genset 3 Phase 1 Output (G3P1I)");
			gscpoiCol.setMinWidth(30);
			gscpoiCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("gscpoi")
					);
		//		Create column for Data G3P3I (Genset 3 Phase 3 Output)
			TableColumn<Data,String> gscptiCol = 
					new TableColumn<Data,String>("Genset 3 Phase 3 Output (G3P3I)");
			gscptiCol.setMinWidth(30);
			gscptiCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("gscpti")
					);
		//		Create column for Data G3OIL (Genset 3 Oil Pressure)
			TableColumn<Data,String> gscoilCol = 
					new TableColumn<Data,String>("Genset 3 Oil Pressure (G3OIL)");
			gscoilCol.setMinWidth(30);
			gscoilCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("gscoil")
					);
		//		Create column for Data G3H2O (Genset 3 Water Temperature)
			TableColumn<Data,String> gscwatCol = 
					new TableColumn<Data,String>("Genset 3 Water Temperature (G3H20)");
			gscwatCol.setMinWidth(30);
			gscwatCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("gscwat")
					);
			gs3Table.setItems(obsList);
			gs3Table.getColumns().addAll(dateCol,timeCol,gscrpmCol,gscvroCol,gscfltCol,
					gscsrvCol,gscbrkCol,gsceeCol,gscstCol,gscpoiCol,gscptiCol,gscoilCol,
					gscwatCol);
			return gs3Table;
	}
	
	//Get table for viewing all genset data
	@SuppressWarnings("unchecked")
	private TableView<Data> getAllGSTable(ObservableList<Data> obsList) {		
		TableView<Data> allGSTable = new TableView<Data>();
		allGSTable.setEditable(true);
		//Create general Columns for date and time
		//Create column for date of data
			TableColumn<Data,String> dateCol = new TableColumn<Data,String>("Date");
			dateCol.setMinWidth(40);
			dateCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("date")
					);
		//Create column for time of data
			TableColumn<Data,String> timeCol = new TableColumn<Data,String>("Time");
			timeCol.setMinWidth(30);
			timeCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("time")
					);
		//Create Columns for all GS info
			//		Create column for Data FPCR (Fuel Pump Cutoff Relay Status)
				TableColumn<Data,String> fpcrCol = 
						new TableColumn<Data,String>("Fuel Pump Cutoff Relay Status (FPCR)");
				fpcrCol.setMinWidth(30);
				fpcrCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("fpcr")
						);
			//		Create column for Data ECMP (Genset ECM Power Relay- All Three)
				TableColumn<Data,String> ecmpCol = 
						new TableColumn<Data,String>("Genset ECM Power Relay (ECMP)");
				ecmpCol.setMinWidth(30);
				ecmpCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("ecmp")
						);
			//		Create column for Data 3T (Engine Speed D Valve)
				TableColumn<Data,String> threetCol = 
						new TableColumn<Data,String>("Engine Speed D Valve (3T)");
				threetCol.setMinWidth(30);
				threetCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("threet")
						);
			//		Create column for Data 7T (Engine Speed C Valve)
				TableColumn<Data,String> seventCol = 
						new TableColumn<Data,String>("Engine Speed C Valve (7T)");
				seventCol.setMinWidth(30);
				seventCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("sevent")
						);
			//		Create column for Data 12T(Engine Speed B Valve)
				TableColumn<Data,String> twelvetCol = 
						new TableColumn<Data,String>("Engine Speed B Valve (12T)");
				twelvetCol.setMinWidth(30);
				twelvetCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("twelvet")
						);
			//		Create column for Data 15T(Engine Speed A Valve)
				TableColumn<Data,String> fifteentCol = 
						new TableColumn<Data,String>("Engine Speed A Valve (15T)");
				fifteentCol.setMinWidth(30);
				fifteentCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("fifteent")
						);
			//		Create column for Data J OK (J1939 Circuit OK Status)
				TableColumn<Data,String> jokCol = 
						new TableColumn<Data,String>("J1939 Circuit OK (J OK)");
				jokCol.setMinWidth(30);
				jokCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("jok")
						);
			//		Create column for Data J ACT (J1939 Circuit Activity Status)
				TableColumn<Data,String> jactCol = 
						new TableColumn<Data,String>("(J1939 Circuit Activity (J ACT)");
				jactCol.setMinWidth(30);
				jactCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("jact")
						);
			//		Create column for Data DADT
				TableColumn<Data,String> dadtCol = new TableColumn<Data,String>("DADT");
				dadtCol.setMinWidth(30);
				dadtCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("dadt")
						);
			//		Create column for Data RATE
				TableColumn<Data,String> rateCol = new TableColumn<Data,String>("RATE");
				rateCol.setMinWidth(30);
				rateCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("rate")
						);
			//Create Columns for GS1 info
					//		Create column for Data G1RPM (Genset 1 Engine RPM)
						TableColumn<Data,String> gsarpmCol = 
								new TableColumn<Data,String>("Genset 1 Engine RPM (G1RPM)");
						gsarpmCol.setMinWidth(30);
						gsarpmCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gsarpm")
								);
					//		Create column for Data G1VRO (Genset 1 Voltage Regulator Enable Relay)
						TableColumn<Data,String> gsavroCol = 
								new TableColumn<Data,String>("Genset 1 Voltage Regulator Enable Relay (G1VRO");
						gsavroCol.setMinWidth(30);
						gsavroCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gsavro")
								);
					//		Create column for Data G1FLT (Genset 1 Fault)
						TableColumn<Data,String> gsafltCol = 
								new TableColumn<Data,String>("Genset 1 Fault (G1FLT)");
						gsafltCol.setMinWidth(30);
						gsafltCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gsaflt")
								);
					//		Create column for Data G1SRV (Genset 1 Service Engine)
						TableColumn<Data,String> gsasrvCol = 
								new TableColumn<Data,String>("Genset 1 Service Engine (G1SRV)");
						gsasrvCol.setMinWidth(30);
						gsasrvCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gsasrv")
								);
					//		Create column for Data GS1BK (Genset 1 Main Breaker Status)
						TableColumn<Data,String> gsabrkCol = 
								new TableColumn<Data,String>("Genset 1 Main Breaker Status (GS1BK)");
						gsabrkCol.setMinWidth(30);
						gsabrkCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gsabrk")
								);
					//		Create column for Data G1EE (Genset 1 Engine Enable Relay)
						TableColumn<Data,String> gsaeeCol = 
								new TableColumn<Data,String>("Genset 1 Engine Enable Relay (G1EE)");
						gsaeeCol.setMinWidth(30);
						gsaeeCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gsaee")
								);
					//		Create column for Data G1ST (Genset 1 Start Relay)
						TableColumn<Data,String> gsastCol = 
								new TableColumn<Data,String>("Genset 1 Start Relay (G1ST)");
						gsastCol.setMinWidth(30);
						gsastCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gsast")
								);
					//		Create column for Data G1P1I (Genset 1 Phase 1 Output)
						TableColumn<Data,String> gsapoiCol = 
								new TableColumn<Data,String>("Genset 1 Phase 1 Output (G1P1I)");
						gsapoiCol.setMinWidth(30);
						gsapoiCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gsapoi")
								);
					//		Create column for Data G1P3I (Genset 1 Phase 3 Output)
						TableColumn<Data,String> gsaptiCol = 
								new TableColumn<Data,String>("Genset 1 Phase 3 Output (G1P3I)");
						gsaptiCol.setMinWidth(30);
						gsaptiCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gsapti")
								);
					//		Create column for Data G1OIL (Genset 1 Oil Pressure)
						TableColumn<Data,String> gsaoilCol = 
								new TableColumn<Data,String>("Genset 1 Oil Pressure (G1OIL)");
						gsaoilCol.setMinWidth(30);
						gsaoilCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gsaoil")
								);
					//		Create column for Data G1H2O (Genset 1 Water Temperature)
						TableColumn<Data,String> gsawatCol = 
								new TableColumn<Data,String>("Genset 1 Water Temperature (G1H20)");
						gsawatCol.setMinWidth(30);
						gsawatCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gsawat")
								);
			//Create Columns for GS2 info
					//		Create column for Data G2RPM (Genset 2 Engine RPM)
						TableColumn<Data,String> gsbrpmCol = 
								new TableColumn<Data,String>("Genset 2 Engine RPM (G2RPM)");
						gsbrpmCol.setMinWidth(30);
						gsbrpmCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gsbrpm")
								);
					//		Create column for Data G2VRO (Genset 2 Voltage Regulator Enable Relay)
						TableColumn<Data,String> gsbvroCol = 
								new TableColumn<Data,String>("Genset 2 Voltage Regulator Enable Relay (G2VRO");
						gsbvroCol.setMinWidth(30);
						gsbvroCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gsbvro")
								);
					//		Create column for Data G2FLT (Genset 2 Data)
						TableColumn<Data,String> gsbfltCol = 
								new TableColumn<Data,String>("Genset 2 Fault (G2FLT)");
						gsbfltCol.setMinWidth(30);
						gsbfltCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gsbflt")
								);
					//		Create column for Data G2SRV (Genset 2 Service Engine)
						TableColumn<Data,String> gsbsrvCol = 
								new TableColumn<Data,String>("Genset 2 Service Engine (G2SRV)");
						gsbsrvCol.setMinWidth(30);
						gsbsrvCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gsbsrv")
								);
					//		Create column for Data GS2BK (Genset 2 Main Breaker Status)
						TableColumn<Data,String> gsbbrkCol = 
								new TableColumn<Data,String>("Genset 2 Main Breaker Status (GS2BK)");
						gsbbrkCol.setMinWidth(30);
						gsbbrkCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gsbbrk")
								);
					//		Create column for Data G2EE (Genset 2 Engine Enable Relay)
						TableColumn<Data,String> gsbeeCol = 
								new TableColumn<Data,String>("Genset 2 Engine Enable Relay (G2EE)");
						gsbeeCol.setMinWidth(30);
						gsbeeCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gsbee")
								);
					//		Create column for Data G2ST (Genset 2 Start Relay)
						TableColumn<Data,String> gsbstCol = 
								new TableColumn<Data,String>("Genset 2 Start Relay (G2ST)");
						gsbstCol.setMinWidth(30);
						gsbstCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gsbst")
								);
					//		Create column for Data G2P1I (Genset 2 Phase 1 Output)
						TableColumn<Data,String> gsbpoiCol = 
								new TableColumn<Data,String>("Genset 2 Phase 1 Output (G2P1I)");
						gsbpoiCol.setMinWidth(30);
						gsbpoiCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gsbpoi")
								);
					//		Create column for Data G2P3I (Genset 2 Phase 3 Output)
						TableColumn<Data,String> gsbptiCol = 
								new TableColumn<Data,String>("Genset 2 Phase 3 Output (G2P3I)");
						gsbptiCol.setMinWidth(30);
						gsbptiCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gsbpti")
								);
					//		Create column for Data G2OIL (Genset 2 Oil Pressure)
						TableColumn<Data,String> gsboilCol = 
								new TableColumn<Data,String>("Genset 2 Oil Pressure (G2OIL)");
						gsboilCol.setMinWidth(30);
						gsboilCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gsboil")
								);
					//		Create column for Data G2H2O (Genset 2 Water Temperature)
						TableColumn<Data,String> gsbwatCol = 
								new TableColumn<Data,String>("Genset 2 Water Temperature (G2H20)");
						gsbwatCol.setMinWidth(30);
						gsbwatCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gsbwat")
								);
			//Create Columns for GS3 info
					//		Create column for Data G3RPM (Genset 3 Engine RPM)
						TableColumn<Data,String> gscrpmCol = 
								new TableColumn<Data,String>("Genset 3 Engine RPM (G3RPM)");
						gscrpmCol.setMinWidth(30);
						gscrpmCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gscrpm")
								);
					//		Create column for Data G3VRO (Genset 3 Voltage Regulator Enable Relay)
						TableColumn<Data,String> gscvroCol = 
								new TableColumn<Data,String>("Genset 3 Voltage Regulator Enable Relay (G3VRO");
						gscvroCol.setMinWidth(30);
						gscvroCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gscvro")
								);
					//		Create column for Data G3FLT (Genset 3 Fault)
						TableColumn<Data,String> gscfltCol = 
								new TableColumn<Data,String>("Genset 3 Fault (G3FLT)");
						gscfltCol.setMinWidth(30);
						gscfltCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gscflt")
								);
					//		Create column for Data G3SRV (Genset 3 Service Engine)
						TableColumn<Data,String> gscsrvCol = 
								new TableColumn<Data,String>("Genset 3 Service Engine (G3SRV)");
						gscsrvCol.setMinWidth(30);
						gscsrvCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gscsrv")
								);
					//		Create column for Data GS3BK (Genset 3 Main Breaker Status)
						TableColumn<Data,String> gscbrkCol = 
								new TableColumn<Data,String>("Genset 3 Main Breaker Status (GS3BK)");
						gscbrkCol.setMinWidth(30);
						gscbrkCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gscbrk")
								);
					//		Create column for Data G3EE (Genset 3 Engine Enable Relay)
						TableColumn<Data,String> gsceeCol = 
								new TableColumn<Data,String>("Genset 3 Engine Enable Relay (G3EE)");
						gsceeCol.setMinWidth(30);
						gsceeCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gscee")
								);
					//		Create column for Data G3ST (Genset 3 Start Relay)
						TableColumn<Data,String> gscstCol = 
								new TableColumn<Data,String>("Genset 3 Start Relay (G3ST)");
						gscstCol.setMinWidth(30);
						gscstCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gscst")
								);
					//		Create column for Data G3P1I (Genset 3 Phase 1 Output)
						TableColumn<Data,String> gscpoiCol = 
								new TableColumn<Data,String>("Genset 3 Phase 1 Output (G3P1I)");
						gscpoiCol.setMinWidth(30);
						gscpoiCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gscpoi")
								);
					//		Create column for Data G3P3I (Genset 3 Phase 3 Output)
						TableColumn<Data,String> gscptiCol = 
								new TableColumn<Data,String>("Genset 3 Phase 3 Output (G3P3I)");
						gscptiCol.setMinWidth(30);
						gscptiCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gscpti")
								);
					//		Create column for Data G3OIL (Genset 3 Oil Pressure)
						TableColumn<Data,String> gscoilCol = 
								new TableColumn<Data,String>("Genset 3 Oil Pressure (G3OIL)");
						gscoilCol.setMinWidth(30);
						gscoilCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gscoil")
								);
					//		Create column for Data G3H2O (Genset 3 Water Temperature)
						TableColumn<Data,String> gscwatCol = 
								new TableColumn<Data,String>("Genset 3 Water Temperature (G3H20)");
						gscwatCol.setMinWidth(30);
						gscwatCol.setCellValueFactory(
								new PropertyValueFactory<Data, String>("gscwat")
								);
					allGSTable.setItems(obsList);
					allGSTable.getColumns().addAll(dateCol, timeCol,gsarpmCol,gsbrpmCol,gscrpmCol,jokCol,
							jactCol,ecmpCol,
							gsafltCol,gsasrvCol,gsaeeCol,gsavroCol,gsapoiCol,gsaptiCol,gsaoilCol,gsawatCol,
							gsabrkCol,gsastCol,
							gsbfltCol,gsbsrvCol,gsbeeCol,gsbvroCol,gsbpoiCol,gsbptiCol,gsboilCol,gsbwatCol,
							gsbbrkCol,gsbstCol,
							gscfltCol,gscsrvCol,gsceeCol,gscvroCol,gscpoiCol,gscptiCol,gscoilCol,gscwatCol,
							gscbrkCol,gscstCol,
							threetCol,seventCol,twelvetCol,fifteentCol,fpcrCol,dadtCol,rateCol);
				return allGSTable;
	}
	
	//Get table for viewing auxiliary data
	@SuppressWarnings("unchecked")
	private TableView<Data> getAuxTable(ObservableList<Data> obsList) {
		TableView<Data> auxTable = new TableView<Data>();
		auxTable.setEditable(true);
		//Create general Columns for date and time
		//Create column for date of data
			TableColumn<Data,String> dateCol = new TableColumn<Data,String>("Date");
			dateCol.setMinWidth(40);
			dateCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("date")
					);
		//Create column for time of data
			TableColumn<Data,String> timeCol = new TableColumn<Data,String>("Time");
			timeCol.setMinWidth(30);
			timeCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("time")
					);
		//Create Columns for Aux Systems
			//		Create column for Data ACV (AC Bus Voltage)
				TableColumn<Data,String> acvCol = 
						new TableColumn<Data,String>("AC Bus Voltage (ACV)");
				acvCol.setMinWidth(30);
				acvCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("acv")
						);
			//		Create column for Data AUXFL (Auxiliary Power Fault)

				TableColumn<Data,String> auxflCol = 
						new TableColumn<Data,String>("Auxiliary Power Fault (AUXFL)");
				auxflCol.setMinWidth(30);
				auxflCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("auxfl")
						);
			//		Create column for Data CTB (Traction Motor Blower Amps)
				TableColumn<Data,String> ctbCol = 
						new TableColumn<Data,String>("Traction Motor Blower Amps (CTB)");
				ctbCol.setMinWidth(30);
				ctbCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("ctb")
						);
			//		Create column for Data ACC1 (GS1 AC Power Contactor Commanded)
				TableColumn<Data,String> acconecCol = 
						new TableColumn<Data,String>("GS1 AC Power Contactor Commanded (ACC1)");
				acconecCol.setMinWidth(30);
				acconecCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("acconec")
						);
			//		Create column for Data ACC2 (GS2 AC Power Contactor Commanded)
				TableColumn<Data,String> acctwocCol = 
						new TableColumn<Data,String>("GS2 AC Power Contactor Commanded (ACC2)");
				acctwocCol.setMinWidth(30);
				acctwocCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("acctwoc")
						);
			//		Create column for Data ACC3 (GS3 AC Power Contactor Commanded)
				TableColumn<Data,String> accthreecCol = 
						new TableColumn<Data,String>("GS3 AC Power Contactor Commanded (ACC3)");
				accthreecCol.setMinWidth(30);
				accthreecCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("accthreec")
						);
			//		Create column for Data ACC1 (GS1 AC Power Contactor Status)
				TableColumn<Data,String> acconesCol = 
						new TableColumn<Data,String>("GS1 AC Power Contactor Status (ACC1)");
				acconesCol.setMinWidth(30);
				acconesCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("accones")
						);
			//		Create column for Data ACC2 (GS2 AC Power Contactor Status)
				TableColumn<Data,String> acctwosCol = 
						new TableColumn<Data,String>("GS2 AC Power Contactor Status (ACC2)");
				acctwosCol.setMinWidth(30);
				acctwosCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("acctwos")
						);
			//		Create column for Data ACC3 (GS3 AC Power Contactor Status)
				TableColumn<Data,String> accthreesCol = 
						new TableColumn<Data,String>("GS3 AC Power Contactor Status (ACC3)");
				accthreesCol.setMinWidth(30);
				accthreesCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("accthrees")
						);
			//		Create column for Data EBC (Traction Motor Blower Contactor Commanded)
				TableColumn<Data,String> ebccCol = 
						new TableColumn<Data,String>("Traction Motor Blower Contactor Commanded (EBC)");
				ebccCol.setMinWidth(30);
				ebccCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("ebcc")
						);
			//		Create column for Data EBC (Traction Motor Blower Contactor Status)
				TableColumn<Data,String> ebcsCol = 
						new TableColumn<Data,String>("Traction Motor Blower Contactor Status (EBC)");
				ebcsCol.setMinWidth(30);
				ebcsCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("ebcs")
						);
			//		Create column for Data CTA (Air Compressor Input Amps)
				TableColumn<Data,String> ctaCol = 
						new TableColumn<Data,String>("Air Compressor Input Amps (CTA)");
				ctaCol.setMinWidth(30);
				ctaCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("cta")
						);
			//		Create column for Data CTD (Main Battery Charging Amps)
				TableColumn<Data,String> ctdCol = 
						new TableColumn<Data,String>("Main Battery Charging Amps (CTD)");
				ctdCol.setMinWidth(40);
				ctdCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("ctd")
						);
			//		Create column for Data CTC (LVPS Input Amps)
				TableColumn<Data,String> ctcCol = 
						new TableColumn<Data,String>("LVPS Input Amps (CTC)");
				ctcCol.setMinWidth(40);
				ctcCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("ctc")
						);
			//		Create column for Data CTE (24v Battery Charging Amps)
				TableColumn<Data,String> cteCol = 
						new TableColumn<Data,String>("24v Battery Charging Amps (CTE)");
				cteCol.setMinWidth(40);
				cteCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("cte")
						);
				auxTable.setItems(obsList);
				auxTable.getColumns().addAll(dateCol,timeCol,acvCol,auxflCol,ctaCol,ctbCol,
						ctcCol,ctdCol,cteCol,acconecCol,acconesCol,acctwocCol,acctwosCol,
						accthreecCol,accthreesCol,ebccCol,ebcsCol);
				return auxTable;
	}
	
	//Get table for viewing miscilanious data
	@SuppressWarnings("unchecked")
	private TableView<Data> getMiscTable(ObservableList<Data> obsList) {
		TableView<Data> miscTable = new TableView<Data>();
		miscTable.setEditable(true);
		//Create general Columns for date and time
		//Create column for date of data
			TableColumn<Data,String> dateCol = new TableColumn<Data,String>("Date");
			dateCol.setMinWidth(40);
			dateCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("date")
					);
		//Create column for time of data
			TableColumn<Data,String> timeCol = new TableColumn<Data,String>("Time");
			timeCol.setMinWidth(30);
			timeCol.setCellValueFactory(
					new PropertyValueFactory<Data, String>("time")
					);
		//Create Columns for Misc Systems
			//		Create column for Data ACRST (Air Cond Reset Button)
				TableColumn<Data,String> acrstCol = 
						new TableColumn<Data,String>("Air Cond Reset Button (ACRST)");
				acrstCol.setMinWidth(30);
				acrstCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("acrst")
						);
			//		Create column for Data BUZZ (Buzzer Output)
				TableColumn<Data,String> buzzCol = 
						new TableColumn<Data,String>("Buzzer Output (BUZZ)");
				buzzCol.setMinWidth(30);
				buzzCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("buzz")
						);
			//		Create column for Data LTS (Load Test Switch)
				TableColumn<Data,String> ltsCol = 
						new TableColumn<Data,String>("Load Test Switch (LTS)");
				ltsCol.setMinWidth(30);
				ltsCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("lts")
						);
			//		Create column for Data PA
				TableColumn<Data,String> paCol = 
						new TableColumn<Data,String>("(PA)");
				paCol.setMinWidth(30);
				paCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("pa")
						);
			//		Create column for Data RCTOT
				TableColumn<Data,String> rctotCol = 
						new TableColumn<Data,String>("RCTOT");
				rctotCol.setMinWidth(30);
				rctotCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("rctot")
						);
			//		Create column for Data 2T (Alarm Bell)
				TableColumn<Data,String> twotCol = 
						new TableColumn<Data,String>("Alarm Bell Trainline (2T)");
				twotCol.setMinWidth(30);
				twotCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("twot")
						);
			//		Create column for Data 13T(Control Switch)
				TableColumn<Data,String> thirteentCol = 
						new TableColumn<Data,String>("Control Switch (13T)");
				thirteentCol.setMinWidth(30);
				thirteentCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("thirteent")
						);
			//		Create column for Data START (Start Switch)
				TableColumn<Data,String> startCol = 
						new TableColumn<Data,String>("Start Switch (START)");
				startCol.setMinWidth(30);
				startCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("start")
						);
			//		Create column for Data 26T(TL Ground Reset)
				TableColumn<Data,String> twentysixtCol = 
						new TableColumn<Data,String>("Trainline Ground Reset (26T)");
				twentysixtCol.setMinWidth(30);
				twentysixtCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("twentysixt")
						);
			//		Create column for Data LVFLT
				TableColumn<Data,String> lvfltCol = 
						new TableColumn<Data,String>("LVFLT");
				lvfltCol.setMinWidth(30);
				lvfltCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("lvflt")
						);
			//		Create column for Data INFLT
				TableColumn<Data,String> infltCol = 
						new TableColumn<Data,String>("INFLT");
				infltCol.setMinWidth(30);
				infltCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("inflt")
						);
			//		Create column for Data ABR (Alarm Bell Relay)
				TableColumn<Data,String> abrCol = 
						new TableColumn<Data,String>("Alarm Bell Relay (ABR)");
				abrCol.setMinWidth(30);
				abrCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("abr")
						);
			//		Create column for Data SHTRP
				TableColumn<Data,String> shtrpCol = 
						new TableColumn<Data,String>("SHTRP");
				shtrpCol.setMinWidth(30);
				shtrpCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("shtrp")
						);
			//		Create column for Data AATS
				TableColumn<Data,String> aatsCol = 
						new TableColumn<Data,String>("AATS");
				aatsCol.setMinWidth(30);
				aatsCol.setCellValueFactory(
						new PropertyValueFactory<Data, String>("aats")
						);
				miscTable.setItems(obsList);
				miscTable.getColumns().addAll(dateCol,timeCol,startCol,twentysixtCol,
						acrstCol,buzzCol,ltsCol,paCol,rctotCol,twotCol,thirteentCol,abrCol,
						shtrpCol,aatsCol,lvfltCol,infltCol);
				return miscTable;
	}
}
