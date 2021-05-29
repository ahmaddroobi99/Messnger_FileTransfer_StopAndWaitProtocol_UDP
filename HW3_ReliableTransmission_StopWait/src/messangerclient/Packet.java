package messangerclient;

import java.nio.*;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class Packet {
    static final short MAX_DATA_LENGTH = 1024;
    
    private short seqNo;
    private short srcPort, dstPort, dataLength, chkSum;
    private byte[] data;
    private ByteBuffer buffer;
    public static final short HEADER_LENGTH = (short) (2+2+2+2+2);
    
    private Checksum cs;
    
    public Packet() {
        this.srcPort = 0;
        this.dstPort = 0;
        this.dataLength = 0;
        this.seqNo = 0;
        data = new byte[MAX_DATA_LENGTH];    // 1024 = 1KB
        cs = new CRC32();
    }
    
    public Packet(short seqNo, short srcPort, short dstPort) {
        this.seqNo = seqNo;
        this.srcPort = srcPort;
        this.dstPort = dstPort;
        this.dataLength = 0;
        data = new byte[MAX_DATA_LENGTH];    // 1024 = 1KB
        cs = new CRC32();
    }
    
    public void setPorts(short srcPort, short dstPort) {
        this.srcPort = srcPort;
        this.dstPort = dstPort;
    }
    
    public short getSrcPort() {
        return this.srcPort;
    }
    
    public short getDstPort() {
        return this.dstPort;
    }
    
    public short getSeqNo() {
        return this.seqNo;
    }
    
    public void setChkSum(short chkSum) {
        this.chkSum = chkSum;
    }
    
    public short getChkSum() {
        return this.chkSum;
    }
    
    public short getDataLength() {
        return this.dataLength;
    }
    
    public void addData(byte[] data, short n) {
        this.dataLength = n;
        if(n > MAX_DATA_LENGTH) {
            System.out.println("Data is too big!");
            return;
        }
        System.arraycopy(data, 0, this.data, 0, n);
    }
    
    public ByteBuffer toByteBuffer() {
        buffer = ByteBuffer.allocate(HEADER_LENGTH + dataLength);
        buffer.clear();
        buffer.putShort(seqNo);
        buffer.putShort(srcPort);
        buffer.putShort(dstPort);
        buffer.putShort(dataLength);
        buffer.put(data, 0, dataLength);
        short chkSum = (short) checkSum(buffer);
        buffer.putShort(chkSum);
        this.chkSum = chkSum;
        return buffer;
    }
    
    public void printPacketAsArray() {
        if (buffer != null) {
            Packet.printBufferHex(buffer, this.getPacketLength());
        } else {
            System.out.println("Buffer is , Execute to Buffer");
        }
    }
    
    static void printBufferHex(ByteBuffer b, int limit) {
        String S = "[";
        for (int i = 0; i < limit; i++) {
            S += String.format("%02X", b.array()[i] & 0x00ff);
            if (i != (limit - 1)) {
                S += ", ";
            }
        }
        S += "]\n";
        S += "Position: " + b.position()
                + "\nLimit: " + b.limit();
        System.out.println(S);
    }

    public void printPacket() {
        String S = "";
        S += ("Packet Cotents = { ");
        S += ("\n\tSeq No = " + this.seqNo);
        S += ("\n\tSrc Port#: " + this.srcPort);
        S += ("\n\tDst Port#: " + this.dstPort);
        S += ("\n\tdataLength: " + this.dataLength);
        S += ("\n\tChkSum = " + this.chkSum);

        int limit = dataLength;
        S += "\n\tdata = [";
        for (int i = 0; i < limit; i++) {
            S += String.format("%02X", (byte) data[i] & 0x00ff);
            if (i != (limit - 1)) {
                S += ", ";
            }
        }
        S += "]";
        S += "\n}\n";
        System.out.println(S); // instead of this use next 
        // return S; // use this to return a String to print it in GUI
        // change function type to String
    }

    public int getPacketLength() {
        return HEADER_LENGTH + dataLength;   // Returns packet length including header length
    }
    
    public long checkSum(ByteBuffer buffer) {
        byte [] allPacket = buffer.array();
        cs.update(allPacket, 0, getPacketLength());
        return cs.getValue();
    }
    
    public void extractPacketFromByteBuffer(ByteBuffer buffer) {
        try {
            this.seqNo = buffer.getShort(0);
            this.srcPort = buffer.getShort(2);
            this.dstPort = buffer.getShort(4);
            this.dataLength = buffer.getShort(6);
            for(int i=0; i<this.dataLength; i++)
                data[i] = buffer.get(8+i);
            this.chkSum = buffer.getShort(8+dataLength);
            System.out.println("Check sum (Extracted) = " + this.chkSum);
        } catch(Exception e) { }
    }
    
    public byte [] extractDataFromPacket() {
        return this.data;
    }
}