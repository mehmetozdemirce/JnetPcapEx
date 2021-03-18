package Source;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapHeader;
import org.jnetpcap.PcapIf;
import org.jnetpcap.winpcap.WinPcap;
import org.jnetpcap.winpcap.WinPcapSendQueue;

import myClass.ICons;

public class SourceSend {

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

	public static boolean sendPacketNoBuff(byte[] byteList, WinPcap wpcap) {
		boolean isSend = false;
		System.arraycopy(ICons.macSource, 0, byteList, 6, 6); // src mac
		byte[] eth = { (byte) 0x88, 0x08 };
		System.arraycopy(eth, 0, byteList, 12, 2);
		String paket = new String(byteList);
		if (wpcap.sendPacket(byteList) != Pcap.OK) {
			System.err.println(wpcap.getErr());
			System.err.println("Can not Sended::: " + paket);
		} else {
			isSend = true;
			System.out.println("Sended::: " + paket);
		}
		return isSend;
	}

	public static void sendPacketBuff(List<byte[]> byteArrList, WinPcap wpcap) {
		int paketSize = ICons.MAX_ETHERNET_PACKET_SIZE;
		int paketSayisi = byteArrList.size();
		int bufferAlloc = paketSize * paketSayisi * 2;
		WinPcapSendQueue queue = WinPcap.sendQueueAlloc(bufferAlloc);
		PcapHeader hdr = new PcapHeader(paketSize, paketSize);
		for (byte[] byteArr : byteArrList) {
			queue.queue(hdr, byteArr);
		}
		int r = wpcap.sendQueueTransmit(queue, WinPcap.TRANSMIT_SYNCH_ASAP);
		if (r != queue.getLen()) {
			System.err.println("wpcap.sendQueueTransmit" + wpcap.getErr());
			return;
		} else {
			System.out.println("Success Send by Buffer wpcap.sendQueueTransmit");
		}
	}

}
