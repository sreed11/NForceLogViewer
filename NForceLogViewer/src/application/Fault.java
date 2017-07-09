package application;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Fault {
	private List<String> faults = new ArrayList<String>();
	//Date and time Simple Properties and regular variables
	private int yearI,monthI,dayI,hourI,minuteI;
	private float secondI;
	private SimpleStringProperty faultsSimp, date, time;
	private SimpleIntegerProperty year,month,day,hour,minute,faultFreq;
	private SimpleFloatProperty second;
	//List of Data instances containing rows of data
	private List<Data> dataRowList = new ArrayList<Data>();
	//Get data and convert to SimpleProperty for time and date
	public Fault(String splitRow[]) {
		this.faults.add(splitRow[2]);
		faultsSimp = new SimpleStringProperty(splitRow[2]);
		String[] temp = splitRow[0].trim().split("/");
		if (temp.length == 3) {
			this.dayI = Integer.parseInt(temp[0]);
			this.day = new SimpleIntegerProperty(dayI);
			this.monthI = Integer.parseInt(temp[1]);
			this.month = new SimpleIntegerProperty(monthI);
			this.yearI = Integer.parseInt(temp[2]) + 2000;
			this.year = new SimpleIntegerProperty(yearI);
		}
		temp = splitRow[1].trim().split(":");
		if (temp.length == 3) {
			this.hourI = Integer.parseInt(temp[0]);
			this.hour = new SimpleIntegerProperty(hourI);
			this.minuteI = Integer.parseInt(temp[1]);
			this.minute = new SimpleIntegerProperty(minuteI);
			this.secondI = Float.parseFloat(temp[2]);
			this.second = new SimpleFloatProperty(secondI);
		}
	}
	//For each fault, add to faults
	public void addFault(String fault) {
		this.faults.add(fault);
		faultsSimp = new SimpleStringProperty(getFaultsSimp() + ", " + fault);
	}
	//For each for of non-fault data, add to list of Datas
	public void addData(String[] data, int version) {
		this.dataRowList.add(new Data(data, version));
	}
	//Return array of faults
	public String[] getFault() {
		return (String[]) this.faults.toArray(new String[0]);
	}
	//Return list of Datas
	public List<Data> getData() {
		return dataRowList;
	}

	public int getYear() {
		return year.get();
	}

	public int getMonth() {
		return month.get();
	}

	public int getDay() {
		return day.get();
	}
	
	public int getHour() {
		return hour.get();
	}

	public int getMinute() {
		return minute.get();
	}
	
	public float getSecond() {
		return second.get();
	}

	public String getFaultsSimp() {
		return faultsSimp.get();
	}
	
	public String getDate() {
		date = new SimpleStringProperty(getMonth() + "/" + getDay() + "/" + getYear());
		return date.get();
	}
	
	public String getTime() {
		time = new SimpleStringProperty(getHour() + ":" + getMinute() + ":" + getSecond());
		return time.get();
	}
	public int getFaultFreq() {
		return faultFreq.get();
	}
	public void setFaultFreq(int faultFreq) {
		this.faultFreq = new SimpleIntegerProperty(faultFreq);
	}
	
	
}
