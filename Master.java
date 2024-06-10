import java.net.*;
import java.io.*;

public class Master {
    public static void main(String args[]) {
        DatagramSocket soket1 = null;
        DatagramSocket soket2 = null;
        DatagramSocket soket3 = null;

        try {
            //Create the three sockets for the three sample slaves
            soket1 = new DatagramSocket();
            soket2 = new DatagramSocket();
            soket3 = new DatagramSocket();

            byte[] buffer = new byte[1000];

            while (true) {
                System.out.println("Time");

                // Ask the slaves for the time
                buffer = "Time".getBytes();
                soket1.send(new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), 6787));
                soket2.send(new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), 6788));
                soket3.send(new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), 6789));

                //Save the master time
                long masterTime = System.currentTimeMillis();

                //Receive the time from the slaves
                DatagramPacket request1 = new DatagramPacket(buffer, buffer.length);
                DatagramPacket request2 = new DatagramPacket(buffer, buffer.length);
                DatagramPacket request3 = new DatagramPacket(buffer, buffer.length);
                soket1.receive(request1);
                soket2.receive(request2);
                soket3.receive(request3);

                System.out.println("time1: " +request1.toString().trim()+ " time2: " +request2.toString().trim()+" time3" +request3.toString().trim());

                //Unpack the time from the slaves
                long time1 = Long.parseLong(new String(request1.getData(), 0, request1.getLength()).trim());
                long time2 = Long.parseLong(new String(request2.getData(), 0, request2.getLength()).trim());
                long time3 = Long.parseLong(new String(request3.getData(), 0, request3.getLength()).trim());

                //Calculate the differences
                long diferencia1 = time1-masterTime;
                long diferencia2 = time2-masterTime;
                long diferencia3 = time3-masterTime;

                System.out.println("diff1: " +diferencia1+ " diff2: " +diferencia2+" diff3" +diferencia3);

                //Calculate the average of the differences
                long media = (diferencia1 + diferencia2 + diferencia3)/3;

                System.out.println("Media: "+ media);

                //Calculate the corrections
                String correccion1 = String.valueOf(diferencia1-media);
                String correccion2 = String.valueOf(diferencia2-media);
                String correccion3 = String.valueOf(diferencia3-media);

                System.out.println("corr1: " +correccion1+ " corr2: " +correccion2+" corr3" +correccion3);

                byte[] buffer1 = new byte[1000];
                byte[] buffer2 = new byte[1000];
                byte[] buffer3 = new byte[1000];

                buffer1 = correccion1.getBytes();
                buffer2 = correccion2.getBytes();
                buffer3 = correccion3.getBytes();

                //Send the corrections to the slaves
                soket1.send(new DatagramPacket(buffer1, buffer1.length, InetAddress.getLocalHost(), 6787));
                soket2.send(new DatagramPacket(buffer2, buffer2.length, InetAddress.getLocalHost(), 6788));
                soket3.send(new DatagramPacket(buffer3, buffer3.length, InetAddress.getLocalHost(), 6789));

                //Five seconds of sample delay
                Thread.sleep(5000);
            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("InterruptedException: " + e.getMessage());
        } finally {
            if (soket1 != null)
                soket1.close();
            if (soket2 != null)
                soket2.close();
            if (soket3 != null)
                soket3.close();
        }
    }
}