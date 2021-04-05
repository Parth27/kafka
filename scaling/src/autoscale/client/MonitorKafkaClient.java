package autoscale.client;

import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.io.File;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.SigarException;

public class MonitorKafkaClient extends TimerTask {
    public final long frequency;
    File disk;
    Sigar sigar;
    Mem memory;
    public MonitorKafkaClient(long frequency) throws SigarException {
        this.frequency = frequency;
        disk = new File("/home");
        this.sigar = new Sigar();
        this.memory = sigar.getMem();
    }
    public void run() {
        double free = disk.getFreeSpace();
        double total = disk.getTotalSpace();
        double totalMemory = memory.getTotal();
        double memoryUsed = memory.getUsed();
        System.out.printf("Percent of space used: %f %n",((total-free)/total)*100);
        System.out.printf("Percent of memory used: %f %n",(memoryUsed/totalMemory)*100);
    }
    public static void main(String[] args) throws SigarException {
        System.out.println("Started Kafka Monitor Client");
        Timer timer = new Timer();
        MonitorKafkaClient monitor = new MonitorKafkaClient(5);
        timer.schedule(monitor, 0, monitor.frequency*1000);
    }
}