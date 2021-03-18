package myClass;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapHeader;
import org.jnetpcap.PcapIf;
import org.jnetpcap.nio.DisposableGC;
import org.jnetpcap.winpcap.WinPcap;
import org.jnetpcap.winpcap.WinPcapSendQueue;

public class SendBuffer implements Runnable {

	public byte[] by;
	
	public SendBuffer (byte[] by) {
		this.by=by;
	}
	public static int sendedPacketCount;
	

	@Override
	public  void run() {
		sendedPacketCount = 0;
		for (int i = 0; i < 1; i++) {
			try {
				send(by);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.gc();
		}
	}

	public static void send(byte[] byteData) throws InterruptedException {
		// TODO Auto-generated method stub

		List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs
		StringBuilder errbuf = new StringBuilder(); // For any error msgs

		/***************************************************************************
		 * First get a list of devices on this system
		 **************************************************************************/
		int r = Pcap.findAllDevs(alldevs, errbuf);
		if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
			System.err.printf("Can't read list of devices, error is %s", errbuf.toString());
			return;
		}
		System.out.println("device count ::"+ alldevs.size());
//		for (PcapIf pcapIf : alldevs) {

			PcapIf device = alldevs.get(2); // pcapIf;//alldevs.get(0); // We know we have atleast 1 device

//			System.out.println(device);
//			System.out.println(device.getName());
			/***************************************************************************
			 * Second we open a network interface
			 **************************************************************************/
			int snaplen = 64 * 1024; // Capture all packets, no trucation
			int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
			int timeout = 10 * 1000; // 10 seconds in millis

			WinPcap pcap = WinPcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);

			/***************************************************************************
			 * Third we create our crude packet queue we will transmit out This creates a
			 * small queue full of broadcast packets
			 **************************************************************************/
			int paketSize = byteData.length+200;
			int paketSayisi = 10;
			int bufferAlloc = paketSize*paketSayisi*2;
			WinPcapSendQueue queue = WinPcap.sendQueueAlloc(bufferAlloc);
			PcapHeader hdr = new PcapHeader(paketSize, paketSize);
			byte[] pkt = new byte[paketSize];
			if(byteData == null || byteData.length<1) {
				Arrays.fill(pkt, (byte) 255); // Broadcast
			} else {
				pkt = byteData;
			}
				
			
			for (int i =0;i<paketSayisi;i++) {
				queue.queue(hdr, pkt); // Packet #3
			}
			
			/***************************************************************************
			 * Fourth We send our packet off using open device
			 **************************************************************************/
			r = pcap.sendQueueTransmit(queue, WinPcap.TRANSMIT_SYNCH_ASAP);//WinPcap.TRANSMIT_SYNCH_ASAP);
			if (r != queue.getLen()) {
				System.err.println(pcap.getErr());
				return;
			}
			/***************************************************************************
			 * Lastly we close
			 **************************************************************************/
			
			pcap.close();
//		}
		System.out.println("sendedPacketCount::"+sendedPacketCount);
	
	}
}
