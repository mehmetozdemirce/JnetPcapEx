package myClass;

import javafx.beans.property.SimpleStringProperty;

//	public MyPaket(String type, String data) {
//		this.type =  new SimpleStringProperty(type);
//		this.data = new SimpleStringProperty(data);
//	}

public class MyPaket implements Cloneable {

	private SimpleStringProperty type;
	private SimpleStringProperty data;
	private SimpleStringProperty date;

	public MyPaket(String type, String data,String date) {
		this.type = new SimpleStringProperty(type);
		this.data = new SimpleStringProperty(data);
		this.date = new SimpleStringProperty(date);
	}

	@Override
	public String toString() {
		return this.getType()+ this.getDate()+this.getData();
	}
	
	public String getType() {
		return type.get();
	}

	public void setType(String type) {
		this.type = new SimpleStringProperty(type);
	}

	public String getData() {
		return data.get() ;
	}

	public void setData(String data) {
		this.data = new SimpleStringProperty(data);
	}
	public String getDate() {
		return date.get() ;
	}
	
	public void setDate(String date) {
		this.date = new SimpleStringProperty(date);
	}
	
	@Override
	protected MyPaket clone() throws CloneNotSupportedException {
		return (MyPaket)super.clone();
	}

}
