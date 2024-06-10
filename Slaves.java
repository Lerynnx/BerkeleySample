
import java.net.*;
import java.io.*;

public class Slaves {
    public static void main(String args[]) {
        DatagramSocket socket = null;
        // We receive the time to add passed as an argument
        long timePlus = Long.parseLong(args[0]);

        try {
            System.out.println("Slave port: "+ Integer.parseInt(args[1]) + " --> On");

            // Creating socket with specific port passed as an argument
            socket = new DatagramSocket(Integer.parseInt(args[1]));
            byte[] buffer = new byte[1000];
            
            while(true) {
            	// Listening to the master
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);

                System.out.println("Message received from master");

                // We save and send time
                long currentTime = System.currentTimeMillis()+timePlus;

                buffer = String.valueOf(currentTime).getBytes();
                socket.send(new DatagramPacket(buffer, buffer.length, request.getAddress(), request.getPort()));

                // We wait for the master's correction
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                socket.receive(reply);
                String receivedData = new String(reply.getData(), 0, reply.getLength());
                long correction = Long.parseLong(receivedData.trim());

                // Results
                System.out.println("The correction is: " + correction + "\nFinal time: " + currentTime+correction);
            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (socket != null)
                socket.close();
        }
    }
}
