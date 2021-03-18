package myClass;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapAddr;
import org.jnetpcap.PcapHeader;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.JProtocol;
import org.jnetpcap.protocol.network.Arp;
import org.jnetpcap.winpcap.WinPcap;
import org.jnetpcap.winpcap.WinPcapSendQueue;

import javafx.scene.control.TableView;

public class DeviceManager1 {

	public static PcapIf getDevice() throws IOException {
		List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs
		StringBuilder errbuf = new StringBuilder(); // For any error msgs
		int r = Pcap.findAllDevs(alldevs, errbuf);
		if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
			System.err.printf("Can't read list of devices, error is %s", errbuf.toString());
			return null;
		}
		System.out.println("device count ::" + alldevs.size());
		PcapIf pcapIf = alldevs.get(3);
		return pcapIf; // pcapIf;//alldevs.get(0); // We know we have atleast 1 device
	}

	public static WinPcap getWinPicap(PcapIf device, int snaplen, int timeout) throws IOException {
		device = getDevice();
		StringBuilder errbuf = new StringBuilder(); // For any error msgs
		int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
		snaplen = 64 * 1024; // Capture all packets, no trucation
		timeout = 1 * 1000; // 10 seconds in millis
		return WinPcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);
	}
	
	public static void sendNoBuffer(WinPcap pcap, TableView<MyPaket> xtv1) {
		System.err.println("Send click");
		for (MyPaket mp : xtv1.getItems()) {
			byte[] byteList = (mp.getType()+mp.getDate()+mp.getData()).getBytes();
//			ByteBuffer byteBuffer = ByteBuffer.wrap(byteList);
			System.arraycopy(ICons.macSource2,0,byteList,0,6); // destination Mac
			System.arraycopy(ICons.macSource1,0,byteList,6,6); // src mac
			byte[] eth = {(byte) 0x88,0x08};
			System.arraycopy(eth,0,byteList,12,2);
			
			
			for (int i = 0; i < 10; i++) {
				
				if (pcap.sendPacket(byteList) != Pcap.OK) {
					System.err.println(pcap.getErr());
					System.err.println( i+"Can not Sended::: "+mp.getType()+mp.getDate()+mp.getData());
				} else {
					System.out.println( i+"Sended::: "+mp.getType()+mp.getDate()+mp.getData());
				}

				try {
					Thread.currentThread().sleep(100L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
//		pcap.close();
	}

	public static void sendBuffer(WinPcap pcap, TableView<MyPaket> xtv1) {
		try {
			pcap = DeviceManager1.getWinPicap(null, 0, 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("EXCEPTÝon ::: "+e.getMessage());
		} // defaul degerleri alsin die verdik  ben uydudum
//		
//		if(pcap == null) {
//			System.err.println("pcap  is null");
//			return;
//		}
//		if(xtv1 == null) {
//			System.err.println("Data list xtv1  is null");
//			return;
//		}
		
		WinPcapSendQueue queue = WinPcap.sendQueueAlloc(512);
		PcapHeader hdr = new PcapHeader(128, 128);
		byte[] pkt = new byte[128];
		
		Arrays.fill(pkt, (byte) 255); // Broadcast
		queue.queue(hdr, pkt); // Packet #1
		queue.queue(hdr, pkt); // Packet #2

		Arrays.fill(pkt, (byte) 0x11); // Junk packet
		queue.queue(hdr, pkt); // Packet #3
		
		/**
		 * 
			MyPaket mp = xtv1.getItems().get(0);
	//		for (MyPaket mp : xtv1.getItems()) {
			byte[]  bPaket = (mp.getType()+mp.getDate()+mp.getData()).getBytes();
			int paketSize = bPaket.length;
			int paketSayisi = 1;
			int bufferAlloc = paketSize * paketSayisi * 2;
			
			WinPcapSendQueue queue = WinPcap.sendQueueAlloc(bufferAlloc);
			
			byte[] eth = {(byte) 0x88,0x08};
			System.arraycopy(eth,0,bPaket,12,2);
	
	//			ByteBuffer byteBuffer = ByteBuffer.wrap(bPaket);
	
			PcapHeader hdr = new PcapHeader(paketSize,paketSize);

			queue.queue(hdr, bPaket);
			System.out.println("dataS:::" + mp.getType()+mp.getDate()+mp.getData());
			System.out.println("dataS LEngth:::" + bPaket.length);
		 */
//		}

		System.err.println(" ::::: pcap.sendQueueTransmit before ");
		int r = pcap.sendQueueTransmit(queue, WinPcap.TRANSMIT_SYNCH_ASAP);//WinPcap.TRANSMIT_SYNCH_ASAP);
		System.err.println(" ::::: pcap.sendQueueTransmit after ");
		if (r != queue.getLen()) {
			System.err.println("sendBuffer() Exception:: " + pcap.getErr());
			return;
		} else {
			System.out.println("sendBuffer() data sended by buffer");
		}
		queue=null;
		System.gc();
		pcap.close();
	}
	public static void sendBufferNEW(WinPcap pcap, TableView<MyPaket> xtv1) {
		
		if(pcap == null) {
			System.err.println("pcap  is null");
			return;
		}
		if(xtv1 == null) {
			System.err.println("Data list xtv1  is null");
			return;
		}
		
		int pSize=1514;
		int pSayi=100;

		WinPcapSendQueue queue = WinPcap.sendQueueAlloc(pSize*pSayi*2);
		PcapHeader hdr = new PcapHeader(pSize,pSize);
		
		byte[] pkt = new byte[pSize];

		byte[] eth = {(byte) 0x88,0x08};
		Arrays.fill(pkt, (byte) 255); // Broadcast

		System.arraycopy(eth,0,pkt,12,2);

		for (int i = 0; i < pSayi; i++) {
			System.arraycopy(ICons.macSource2,0,pkt,0,6); // destination Mac
			System.arraycopy(ICons.macSource1,0,pkt,6,6); // src mac
			queue.queue(hdr, pkt); // Packet #1
		}

		
		
		/**
		 * 
			MyPaket mp = xtv1.getItems().get(0);
	//		for (MyPaket mp : xtv1.getItems()) {
			byte[]  bPaket = (mp.getType()+mp.getDate()+mp.getData()).getBytes();
			int paketSize = bPaket.length;
			int paketSayisi = 1;
			int bufferAlloc = paketSize * paketSayisi * 2;
			
			WinPcapSendQueue queue = WinPcap.sendQueueAlloc(bufferAlloc);
				
			byte[] eth = {(byte) 0x88,0x08};
			System.arraycopy(eth,0,bPaket,12,2);
	
	//			ByteBuffer byteBuffer = ByteBuffer.wrap(bPaket);
	
			PcapHeader hdr = new PcapHeader(paketSize,paketSize);

			queue.queue(hdr, bPaket);
			System.out.println("dataS:::" + mp.getType()+mp.getDate()+mp.getData());
			System.out.println("dataS LEngth:::" + bPaket.length);
		 */
//		}
		
		System.err.println(" ::::: pcap.sendQueueTransmit before ");
		int r = pcap.sendQueueTransmit(queue, WinPcap.TRANSMIT_SYNCH_ASAP);//WinPcap.TRANSMIT_SYNCH_ASAP);
		System.err.println(" ::::: pcap.sendQueueTransmit after ");
		if (r != queue.getLen()) {
			System.err.println("sendBuffer() Exception:: " + pcap.getErr());
			return;
		} else {
			System.out.println("sendBuffer() data sended by buffer");
		}
		queue=null;
		System.gc();
	}
	
	public static void capturePacket(Pcap pcap, TableView<MyPaket> xtv1) {
		 try {
			 StringBuilder errbuf = new StringBuilder();
	            if (pcap == null) {
	                System.err.printf("Error while opening device for capture: " + errbuf.toString());
	                return;
	            }
	            System.out.println("device opened");
	            //Create packet handler which will receive packets
	            PcapPacketHandler jpacketHandler = new PcapPacketHandler() {
	                @Override
	                public void nextPacket(PcapPacket packet, Object user) {
	                	byte[] destMacByte = packet.getByteArray(0, 6);
	                	byte[] srcMacByte = packet.getByteArray(6, 12);
	                	byte[] mc = packet.getByteArray(0, 6);
	                	byte[] byteArray = packet.getByteArray(0, packet.getTotalSize());
	                	String sdata ="";
	                	try {
	                		sdata = new String(byteArray, "UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
	                	if(Arrays.equals(mc, ICons.macSource2)) {
	                		MyPaket newCapPaket = new MyPaket(sdata.substring(0, 6), "captured__"+sdata, String.valueOf(Calendar.getInstance().getTimeInMillis()));
	                		xtv1.getItems().add(newCapPaket);
	                	}
	                	MyPaket newCapPaket = new MyPaket(sdata.substring(0, 6), "captured__"+sdata, String.valueOf(Calendar.getInstance().getTimeInMillis()));
	                	xtv1.getItems().add(newCapPaket);
	                	
                     System.out.print(" Packet:wirelen " + packet.getPacketWirelen());
                     System.out.print(" Packet:fnumber " + packet.getFrameNumber());
                     System.out.println(" Packet: tsize " + packet.getTotalSize());
	                }
	            };
	            //we enter the loop and capture the 10 packets here.You can  capture any number of packets just by changing the first argument to pcap.loop() function below
	            pcap.loop(-1, jpacketHandler, "jnetpcap rocks!");
	        } catch (Exception ex) {
	            System.out.println(ex);
	        }
	}
}
