package Source;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.swing.Icon;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.network.Arp;
import org.jnetpcap.winpcap.WinPcap;

import myClass.ICons;
import myClass.MyPaket;

public class SourceCapture {

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
		return pcapIf;
	}

	public static WinPcap getWinPicap(PcapIf device, int snaplen, int timeout) throws IOException {
		device = getDevice();
		StringBuilder errbuf = new StringBuilder(); // For any error msgs
		int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
		snaplen = 64 * 1024; // Capture all packets, no trucation
		timeout = 1 * 100; // seconds in millis
		return WinPcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);
	}
	
	public static void capturePacket(WinPcap wpcap) {
        //Create packet handler which will receive packets
        PcapPacketHandler jpacketHandler = new PcapPacketHandler() {
            @Override
            public void nextPacket(PcapPacket packet, Object user) {
            	byte[] destMacByte = packet.getByteArray(0, 6);
            	if(Arrays.equals(destMacByte, ICons.macSource)) {
            		byte[] byteArray = packet.getByteArray(0, packet.size());
            		byte[] srcMac =  packet.getByteArray(6, 6);
            		System.out.println("yakaladik SourceCapture :: "+ new String(byteArray));
            		System.arraycopy(srcMac, 0, byteArray, 0, 6);
            		System.arraycopy(ICons.macSource, 0, byteArray, 6, 6);
            		System.arraycopy("cap".getBytes(), 0, byteArray, 12, 3);
            		System.out.println("geri Gonderdimmm SourceCapture chance send :: "+ new String(byteArray));
            		boolean isSend = SourceSend.sendPacketNoBuff(byteArray, wpcap);
            		System.out.println("isSend::"+isSend);
            	}
            }
        };
        //we enter the loop and capture the 10 packets here.You can  capture any number of packets just by changing the first argument to pcap.loop() function below
        wpcap.loop(-1, jpacketHandler, "jnetpcap rocks!");
    }

}
