package com.autoscale.client;

import java.net.Socket;
import java.net.InetAddress;

import java.util.Timer;
import java.util.TimerTask;

import java.io.File;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.SigarException;

public class AutoScaleClient extends TimerTask {
    static final long FREQUENCY = 5;
    static final int PORT = 1234;
    static final String IPADDRESS = "71.69.151.14";
    File disk;
    Sigar sigar;
    Mem memory;
    Socket socket;
    InetAddress ip;
    DataInputStream dis;
    DataOutputStream dos;

    public AutoScaleClient() throws SigarException, IOException {
        this.sigar = new Sigar();
        this.memory = sigar.getMem();
        ip = InetAddress.getByName(IPADDRESS);
        disk = new File("/");
        socket = new Socket(ip,PORT);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
    }
    @Override
    public void run() {
        double free = disk.getFreeSpace();
        double total = disk.getTotalSpace();
        double totalMemory = memory.getTotal();
        double memoryUsed = memory.getUsed();
        double percentDisk = ((total-free)/total)*100;
        double percentMemory = (memoryUsed/totalMemory)*100;
        System.out.printf("Percent of space used: %f %n",percentDisk);
        System.out.printf("Percent of memory used: %f %n",percentMemory);
        try {
            dos.writeUTF(String.valueOf(percentDisk)+","+percentMemory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SigarException, IOException {
        System.out.println("Started Kafka Monitor Client");
        Timer timer = new Timer();
        AutoScaleClient scale = new AutoScaleClient();
        timer.schedule(scale, 0, FREQUENCY*1000);
    }
}