package application;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapHeader;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.winpcap.WinPcap;
import org.jnetpcap.winpcap.WinPcapSendQueue;

import Source.SourceCapture;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import myClass.CapturePacket;
import myClass.DeviceManager1;
import myClass.DeviceManager2;
import myClass.ICons;
import myClass.MyPaket;
import myClass.SendBuffer;

public class SampleController implements Initializable {
	
	@FXML private Button xBtnPacketSend;
	@FXML private Button xbtnComandAdd;
	@FXML private Button xbtnSendLoop;
	@FXML private Button xbtnSendNoBuffer;
	@FXML private TextField xtxtRepeatCount;
	@FXML private TextField xtxtData;
	@FXML private TableView<MyPaket> xtv1;
	@FXML private TableColumn<MyPaket , String> xtcType1;
	@FXML private TableColumn<MyPaket , String> xtcData1;
	@FXML private TableColumn<MyPaket , String> xtcDate1;
	
	@FXML private TableView<MyPaket> xtv2;
	@FXML private TableColumn<MyPaket , String> xtcType2;
	@FXML private TableColumn<MyPaket , String> xtcData2;
	@FXML private TableColumn<MyPaket , String> xtcDate2;
	
	@FXML private ComboBox<String> xcmbCommandList;
	
	static ObservableList<MyPaket> mp1 = FXCollections.observableArrayList();
	static ObservableList<MyPaket> mp2 = FXCollections.observableArrayList();
	static ObservableList<MyPaket> mp3 = FXCollections.observableArrayList();
	
	WinPcap wPicap;
//	Pcap pcap;
	
	Thread sender1 = new Thread();
	Thread capture1 = new Thread();
	Thread sender2 = new Thread();
	Thread capture2 = new Thread();
	
	Thread sourceThread = new Thread();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		xcmbCommandList.getItems().add("Type1");
		xcmbCommandList.getItems().add("Type2");
		xcmbCommandList.getItems().add("Type3");

		xtv1.setEditable(true);
		xtcType1.setCellValueFactory(new PropertyValueFactory<MyPaket,String>("type"));
		xtcType1.setCellFactory(TextFieldTableCell.forTableColumn());
		xtcData1.setCellValueFactory(new PropertyValueFactory<MyPaket,String>("data"));
		xtcData1.setCellFactory(TextFieldTableCell.forTableColumn());
		xtcDate1.setCellValueFactory(new PropertyValueFactory<MyPaket,String>("date"));
		xtcDate1.setCellFactory(TextFieldTableCell.forTableColumn());
		
		xtv1.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		xtv1.setItems(mp1);
		
		/**
		 */
		xtcType2.setCellValueFactory(new PropertyValueFactory<MyPaket,String>("type"));
//		xtcType2.setCellFactory(TextFieldTableCell.forTableColumn());
		xtcData2.setCellValueFactory(new PropertyValueFactory<MyPaket,String>("data"));
//		xtcData2.setCellFactory(TextFieldTableCell.forTableColumn());
		xtcDate2.setCellValueFactory(new PropertyValueFactory<MyPaket,String>("date"));
//		xtcDate2.setCellFactory(TextFieldTableCell.forTableColumn());
//		xtv2.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		xtv2.setItems(mp2);
		/**
		 */
		String type ="Type1";
		String data = "1234567890987654321123456789qqqqqqq1111111111qqqqqqqqqqqqqqqqq1111111111qqqqqqqqqqqqqqqqq1111111111qqqqqqqqqqqqqqqqq1111111111qqqqqqqqqqqqqqqqq1111111111qqqqqqqqqqqqqqqqq1111111111qqqqqqqqqqqqqqqqq1111111111qqqqqqqqqqqqqqqqq1111111111qqqqqqqqqqqqqqqqq1111111111qqqqqqqqqqqqqqqqq1111111111qqqqqqqqqqqqqqqqq1111111111qqqqqqqqqqqqqqqqq1111111111qqqqqqqqqqqqqqqqq1111111111qqqqqqqqqqqqqqqqq1111111111qqqqqqqqqqqqqqqqq1111111111qqqqqqqqqqqqqqqqq";
		String date = String.valueOf(Calendar.getInstance().getTimeInMillis());
		MyPaket myPaket = new MyPaket(type, data, date);
		xtv1.getItems().add(myPaket);
		System.out.println(myPaket.toString());
		System.out.println("s length"+myPaket.toString().length());
		System.out.println("b length"+myPaket.toString().getBytes().length);
		type ="Type2";
		data = "2222222222wwwwwwwwwwwwwwwwwwwww2222222222wwwwwwwwwwwwwwwwwwwww2222222222wwwwwwwwwwwwwwwwwwwww2222222222wwwwwwwwwwwwwwwwwwwww2222222222wwwwwwwwwwwwwwwwwwwww2222222222wwwwwwwwwwwwwwwwwwwww2222222222wwwwwwwwwwwwwwwwwwwww2222222222wwwwwwwwwwwwwwwwwwwww2222222222wwwwwwwwwwwwwwwwwwwww2222222222wwwwwwwwwwwwwwwwwwwww2222222222wwwwwwwwwwwwwwwwwwwww2222222222wwwwwwwwwwwwwwwwwwwww2222222222wwwwwwwwwwwwwwwwwwwww2222222222wwwwwwwwwwwwwwwwwwwww2222222222wwwwwwwwwwwwwwwwwwwww2222222222wwwwwwwwwwwwwwwwwwwww2222222222wwwwwwwwwwwwwwwwwwwww2222222222wwwwwwwwwwwwwwwwwwwww2222222222wwwwwwwwwwwwwwwwwwwww2222222222wwwwwwwwwwwwwwwwwwwww";
		date = String.valueOf(Calendar.getInstance().getTimeInMillis());
		xtv1.getItems().add(new MyPaket(type, data, date));
		type ="Type3";
		data = "3333333333333333333333eeeeeeeeeeeeeeeee3333333333333333333333eeeeeeeeeeeeeeeee3333333333333333333333eeeeeeeeeeeeeeeee3333333333333333333333eeeeeeeeeeeeeeeee3333333333333333333333eeeeeeeeeeeeeeeee3333333333333333333333eeeeeeeeeeeeeeeee3333333333333333333333eeeeeeeeeeeeeeeee3333333333333333333333eeeeeeeeeeeeeeeee3333333333333333333333eeeeeeeeeeeeeeeee3333333333333333333333eeeeeeeeeeeeeeeee3333333333333333333333eeeeeeeeeeeeeeeee3333333333333333333333eeeeeeeeeeeeeeeee3333333333333333333333eeeeeeeeeeeeeeeee3333333333333333333333eeeeeeeeeeeeeeeee3333333333333333333333eeeeeeeeeeeeeeeee3333333333333333333333eeeeeeeeeeeeeeeee3333333333333333333333eeeeeeeeeeeeeeeee";
		date = String.valueOf(Calendar.getInstance().getTimeInMillis());
		xtv1.getItems().add(new MyPaket(type, data, date));
		
		try {
			wPicap = DeviceManager1.getWinPicap(null, 0, 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("EXCEPTÝon ::: "+e.getMessage());
		} // defaul degerleri alsin die verdik  ben uydudum

//		  List<PcapIf> alldevs = new ArrayList<PcapIf>();
//          StringBuilder errbuf = new StringBuilder();
//          int r = Pcap.findAllDevs(alldevs, errbuf);
//          System.out.println(r);
//          if (r != Pcap.OK) {
//              System.err.printf("Can't read list of devices, error is %s", errbuf.toString());
//              return;
//          }
//          PcapIf device = alldevs.get(3);
//          int snaplen = 64 * 1024;           // Capture all packets, no trucation
//          int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
//          int timeout = 10 * 1000;           // 10 seconds in millis
//
//          //Open the selected device to capture packets
//          pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);

		new Thread(new Runnable() { // source capture start infinite
			public void run() {
				while (true) {
					SourceCapture.capturePacket(wPicap);
				}
				
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
		        PcapPacketHandler jpacketHandler = new PcapPacketHandler() {
		            @Override
		            public void nextPacket(PcapPacket packet, Object user) {
		            	byte[] destMacByte = packet.getByteArray(0, 6);
		            	if(Arrays.equals(destMacByte, ICons.macSource)) {
		            		byte[] byteArray = packet.getByteArray(0, packet.size());
		            		byte[] srcMac =  packet.getByteArray(6, 6);
		            		System.out.println("yakaladik SourceCapture :: "+ new String(byteArray));
		            		MyPaket newP =  new MyPaket("cc yakaladik",  new String(byteArray), String.valueOf(Calendar.getInstance().getTimeInMillis()));
							xtv2.getItems().add(newP);
		            	}
		            }
		        };
		        //we enter the loop and capture the 10 packets here.You can  capture any number of packets just by changing the first argument to pcap.loop() function below
		        int loop = wPicap.loop(-1, jpacketHandler, "jnetpcap rocks!");
			}
		}).start();

		capture1 = new Thread( new Runnable() {
			public void run() {
				 try {
					 WinPcap wpap = wPicap;
//					try {
//							wpap = DeviceManager1.getWinPicap(null, 0, 0);
//						} catch (IOException e) {
//							e.printStackTrace();
//							System.err.println("EXCEPTÝon ::: "+e.getMessage());
//						} // defaul degerleri alsin die verdik  ben uydudum
			            System.out.println("device opened");
			            //Create packet handler which will receive packets
			            PcapPacketHandler jpacketHandler = new PcapPacketHandler() {
			                @Override
			                public void nextPacket(PcapPacket packet, Object user) {
			                	byte[] mc = packet.getByteArray(0, 6);
			                	if(Arrays.equals(mc, ICons.macSource1)) {
			                		byte[] byteArray = packet.getByteArray(0, packet.size());
			                		String sdata ="";
			                		try {
			                			sdata = new String(byteArray, "UTF-8");
			                		} catch (UnsupportedEncodingException e) {
			                			e.printStackTrace();
			                		}
			                		MyPaket newCapPaket = new MyPaket(sdata.substring(0, 6), "captured__"+sdata, String.valueOf(Calendar.getInstance().getTimeInMillis()));
			                		xtv1.getItems().add(newCapPaket);
	//			                	MyPaket newCapPaket = new MyPaket(sdata.substring(0, 6), "captured__"+sdata, String.valueOf(Calendar.getInstance().getTimeInMillis()));
	//			                	xtv1.getItems().add(newCapPaket);
				                	System.out.println("sdata::::::::::"+sdata);
			                        System.out.print(" Packet:wirelen " + packet.getPacketWirelen());
			                        System.out.print(" Packet:fnumber " + packet.getFrameNumber());
			                        System.out.println(" Packet: tsize " + packet.getTotalSize());
			                	}
			                }
			            };
			            //we enter the loop and capture the 10 packets here.You can  capture any number of packets just by changing the first argument to pcap.loop() function below
			            wpap.loop(-1, jpacketHandler, "jnetpcap rocks!");
			        } catch (Exception ex) {
			            System.out.println(ex);
			        }
			}
		});
		
		capture2 = new Thread( new Runnable() {
			public void run() {
			 try {
//					try {
//							wpap = DeviceManager1.getWinPicap(null, 0, 0);
//						} catch (IOException e) {
//							e.printStackTrace();
//							System.err.println("EXCEPTÝon ::: "+e.getMessage());
//						} // defaul degerleri alsin die verdik  ben uydudum
		            //Create packet handler which will receive packets
		            PcapPacketHandler jpacketHandler = new PcapPacketHandler() {
		                @Override
		                public void nextPacket(PcapPacket packet, Object user) {
		                	byte[] mc = packet.getByteArray(0, 6);
		                	if(Arrays.equals(mc, ICons.macSource2)) {
		                		byte[] byteArray = packet.getByteArray(0, packet.size());
		                		String sdata ="";
		                		try {
		                			sdata = new String(byteArray, "UTF-8");
		                		} catch (UnsupportedEncodingException e) {
		                			e.printStackTrace();
		                		}
		                		MyPaket newCapPaket = new MyPaket(sdata.substring(0, 6), "captured__"+sdata, String.valueOf(Calendar.getInstance().getTimeInMillis()));
		                		xtv2.getItems().add(newCapPaket);
		                		DeviceManager2.sendNoBuffer(wPicap, newCapPaket);
	//			                	MyPaket newCapPaket = new MyPaket(sdata.substring(0, 6), "captured__"+sdata, String.valueOf(Calendar.getInstance().getTimeInMillis()));
	//			                	xtv1.getItems().add(newCapPaket);
			                	System.out.println("sdata::::::::::"+sdata);
		                        System.out.print(" Packet:wirelen " + packet.getPacketWirelen());
		                        System.out.print(" Packet:fnumber " + packet.getFrameNumber());
		                        System.out.println(" Packet: tsize " + packet.getTotalSize());
		                	}
		                }
		            };
		            //we enter the loop and capture the 10 packets here.You can  capture any number of packets just by changing the first argument to pcap.loop() function below
		            wPicap.loop(-1, jpacketHandler, "jnetpcap rocks!");
		        } catch (Exception ex) {
		            System.out.println(ex);
		        }
			}
		});
//		capture1.setName("capture1");
//		capture1.start();
//		capture2.setName("capture2");
//		capture2.start();
	}

	@FXML private void xbtnClearAll() throws InterruptedException {
		xtv1.getItems().clear();
		xtv2.getItems().clear();
	}

	@FXML private void xbtnSendNoBufferAction()  {
		new Thread(new Runnable() {
			
			@Override
			public void run() {

//					try {
////						System.gc();
////						Thread.sleep(1);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
				/**
					for (MyPaket mp : xtv1.getItems()) {
						byte[] byteList = (mp.getType()+mp.getDate()+mp.getData()).getBytes();
						System.arraycopy(ICons.macSource,0,byteList,0,6); // destination Mac
						System.arraycopy(ICons.macSource1,0,byteList,6,6); // src mac
						byte[] eth = {(byte) 0x88,0x08};
						System.arraycopy(eth,0,byteList,12,2);
						if (wPicap.sendPacket(byteList) != Pcap.OK) {
							System.err.println(wPicap.getErr());
							System.err.println( "Exception:::Can not Sended::: "+ new String(byteList));
						} else {
							System.out.println( "Sended::: "+new String(byteList));
						}
					}
					*/
					List<byte[]> byteArrList =  new ArrayList<byte[]>();
					for (MyPaket mp : xtv1.getItems()) {
						byteArrList.add(mp.getData().getBytes());
					}
				
					int paketSize = ICons.MAX_ETHERNET_PACKET_SIZE;
					int paketSayisi = byteArrList.size();
					int bufferAlloc = paketSize * paketSayisi * 4;
					WinPcapSendQueue queue = WinPcap.sendQueueAlloc(bufferAlloc);
					PcapHeader hdr = new PcapHeader(paketSize, paketSize);
					for (byte[] byteArr : byteArrList) {
						queue.queue(hdr, byteArr);
					}
					int r = wPicap.sendQueueTransmit(queue, WinPcap.TRANSMIT_SYNCH_ASAP);
					if (r != queue.getLen()) {
						System.err.println("wpcap.sendQueueTransmit" + wPicap.getErr());
						return;
					} else {
						System.out.println("Success Send by Buffer wpcap.sendQueueTransmit");
					}
					
			}
		}).start();;
	}

	@FXML private void xbtnSendLoopAction() throws InterruptedException {
//		System.out.println("xbtnSendLoop clicked");
//		if(wPicap == null) {
//			System.out.println("wPicap null ");
//			return;
//		}

//		DeviceManager1.sendNoBuffer(wPicap, xtv1);

//		DeviceManager1.sendBufferNEW(wPicap,xtv1);
		
		
	}
	
	@FXML private void xbtnComandAddAction() throws InterruptedException {
		System.out.println("xbtnComandAdd Ekle clicked");
		MyPaket myp = new MyPaket(xcmbCommandList.getValue(), xtxtData.getText(),  String.valueOf(Calendar.getInstance().getTimeInMillis()));
		xtv1.getItems().add(myp);
	}
	@FXML private void xcmbCommandListActon() throws InterruptedException {
		System.out.println("xtvComandList clicked");
		System.out.println(xcmbCommandList.getValue());
	}
	
	@FXML private void xtcType1EditCommit(CellEditEvent<MyPaket, String> nType1) {
		MyPaket selectedItem = xtv1.getSelectionModel().getSelectedItem();
		selectedItem.setType(nType1.getNewValue());
	}
	@FXML private void xtcData1EditCommit(CellEditEvent<MyPaket, String> nData1) {
		MyPaket selectedItem = xtv1.getSelectionModel().getSelectedItem();
		selectedItem.setData(nData1.getNewValue());
	}
	@FXML private void xtcDate1EditComiit(CellEditEvent<MyPaket, String> nDate1) {
		MyPaket selectedItem = xtv1.getSelectionModel().getSelectedItem();
		selectedItem.setDate(nDate1.getNewValue());
	}
}
