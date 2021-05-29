package messangerclient;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;


public class FileReceiver implements Runnable
{
    String address, to;
    private Random random;
    FileReceiver(String getAddress, String from) {
        this.to = from;
        address=getAddress.replace('/',' ').trim();
        random = new Random();
        System.out.println(address);
    }
    
    private short calculate(Packet rcvPacket, ByteBuffer buffer) {
        for(int i=0; i<2; ++i)
            buffer.put(8 + i + rcvPacket.getDataLength(), (byte) 0);
        return (short) rcvPacket.checkSum(buffer);
    }

    @Override
    public void run() {
        try {
            // UDP transfering =================>  Using chunks of data
            DatagramSocket ds = new DatagramSocket(13267);
            byte [] ACK_message = "Packet received correctly!".getBytes();
            byte [] NACK_message = "Some Error".getBytes();
            
            System.out.println("Connecting...");
            
            // Receive file
            FileOutputStream fos = new FileOutputStream("receivedTo"+to.toUpperCase()+".txt");
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            
            short dstPort;
            short waitedSeqNo = 0;
            
            while(true) {
                byte[] mybytearray;
                byte[] rcvBytes = new byte[Packet.HEADER_LENGTH + Packet.MAX_DATA_LENGTH];
                byte[] dataRcvd;
                
                DatagramPacket dp = new DatagramPacket(rcvBytes, rcvBytes.length);
                ds.receive(dp);
                InetAddress ip = dp.getAddress();
                
                ByteBuffer bigBuffer = ByteBuffer.wrap(rcvBytes);
                short dataLength = bigBuffer.getShort(6);
                dataRcvd = new byte[dataLength + Packet.HEADER_LENGTH];
                // Copy data to suitable size
                System.arraycopy(rcvBytes, 0, dataRcvd, 0, dataLength + Packet.HEADER_LENGTH);
                
                
                ByteBuffer rcvBuffer = ByteBuffer.wrap(dataRcvd);
                // Construct receiving packet
                Packet rcvPkt = new Packet();
                rcvPkt.extractPacketFromByteBuffer(rcvBuffer);
                rcvPkt.printPacket();
                dstPort = rcvPkt.getSrcPort();   // Old source port becomes as dst port #
                
                mybytearray = new byte[dataLength];
                System.arraycopy(rcvPkt.extractDataFromPacket(), 0, mybytearray, 0, dataLength);
                
                String s = new String(mybytearray);
                if(s.contains("EOF")) {
                    DatagramPacket sending = new DatagramPacket(ACK_message, ACK_message.length, ip, dstPort);
                    ds.send(sending);
                    System.out.println("ACK sent");
                    break;
                }
                
                int rand = random.nextInt(1000);
                System.out.println("Random number = " + rand);
                if(rand >= 0 && rand <= 800)
                    rcvPkt.setChkSum((short) (rcvPkt.getChkSum() ^ 0xFFFF));
                
                
                // Here we need to check sum to detect the error
                short computedChkSum = calculate(rcvPkt, rcvBuffer);
                System.out.println("Computed checkSum = " + computedChkSum);
                System.out.println("Extracted check sum = " + rcvPkt.getChkSum());
                System.out.println("rcv seq = " + rcvPkt.getSeqNo());
                System.out.println("waited seq = " + waitedSeqNo);
                if(rcvPkt.getChkSum() != computedChkSum || rcvPkt.getSeqNo() != waitedSeqNo) {
                    // Send Negative ACK
                    DatagramPacket sending = new DatagramPacket(NACK_message, NACK_message.length, ip, dstPort);
                    ds.send(sending);
                    System.out.println("N-ACK sent");
                    continue;   // Don't deliver data and write to file
                }
                
                
                
                // Deliver data and write to file
                bos.write(mybytearray, 0, s.length());
                bos.flush();

                // Send ACK
                DatagramPacket sending = new DatagramPacket(ACK_message, ACK_message.length, ip, dstPort);
                ds.send(sending);
                System.out.println("ACK sent");
                // Because the packet is correct, we can flip the waited seqNo
                if(waitedSeqNo == 0)
                    waitedSeqNo = 1;
                else
                    waitedSeqNo = 0;
                System.out.println();
            }
            System.out.println("File has been received successfully :)");
            bos.close();
            fos.close();
            ds.close();
        } catch (UnknownHostException ex) {
            Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
