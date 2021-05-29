package messangerclient;

import java.net.*;
import java.io.*;
import java.nio.*;

public class FileSender implements Runnable
{
    String fileName;
    private short srcPort, dstPort;
    FileSender(String file) {
        fileName = file;
        srcPort = 4789;
        dstPort = 13267;
    }
    
    private void sendCurrentPacket(short seqNo, byte [] mybytearray, int bytesAmount, DatagramSocket ds, InetAddress IPAddress) {
        try {
            Packet packet = new Packet(seqNo, srcPort, dstPort);
            packet.addData(mybytearray, (short) bytesAmount);

            ByteBuffer byteBuf = packet.toByteBuffer();
            packet.printPacket();
            packet.printPacketAsArray();
            byte [] sendData = byteBuf.array();

            DatagramPacket dp = new DatagramPacket(sendData, packet.getPacketLength(), IPAddress, dstPort);
            ds.send(dp);
        } catch(Exception e) {}
    }

    @Override
    public void run() {
        try {
            // UDP transfering =================> Using chunks of data
            DatagramSocket ds = new DatagramSocket(4789);
            InetAddress IPAddress = InetAddress.getByName("localhost");
            System.out.println("Waiting...");
            
            // sendfile
            File myFile = new File(fileName);
            byte[] mybytearray = new byte[1024];         // Chunk size = 1 KB
            
            int bytesAmount;
            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            System.out.println("Sending...");
            
            int resend = 0;
            short seqNo = 0;
            while((bytesAmount = bis.read(mybytearray)) > 0) {   //Read & Send a chunk
                sendCurrentPacket(seqNo, mybytearray, bytesAmount, ds, IPAddress);

                while(true) {
                    byte [] recvData = new byte[1024];
                    DatagramPacket recvPck = new DatagramPacket(recvData, recvData.length);
                    ds.receive(recvPck);
                    String ack = new String(recvData);
                    System.out.println(ack);
                    if(ack.contains("Some Error")) {
                        System.out.println("I'm error ACK");
                        resend++;
                        sendCurrentPacket(seqNo, mybytearray, bytesAmount, ds, IPAddress);
                    }
                    else
                        break;
                }
                // Because the last sent packet is correct and we receive good ack, we flip the seqNo
                if(seqNo == 1)
                    seqNo = 0;
                else
                    seqNo = 1;
                System.out.println();
            }
            
            // Reaching EOF
            String eof = "EOF";
            Packet end = new Packet(seqNo, srcPort, dstPort);
            end.addData(eof.getBytes(), (short) eof.length());
            end.printPacket();
            
            ByteBuffer endBuf = end.toByteBuffer();
            end.printPacketAsArray();
            byte [] endSend = endBuf.array();
            
            DatagramPacket dp = new DatagramPacket(endSend, end.getPacketLength(), IPAddress, dstPort);
            ds.send(dp);
            
            // Receive ACK for EOF
            while(true) {
                    byte [] recvData = new byte[1024];
                    DatagramPacket recvPck = new DatagramPacket(recvData, recvData.length);
                    ds.receive(recvPck);
                    String ack = new String(recvData);
                    System.out.println(ack);
                    if(ack.equals("Some Error")) {
                        resend++;
                        ds.send(dp);   // Resend EOF
                    }
                    else
                        break;
                }
            
            bis.close();
            fis.close();
            ds.close();
            
            System.out.println("File has been sent successfully :)");
            System.out.println("Total number of retransmissions = " + resend);
            
        } 
        catch (IOException ex)
        {}
      }
}
