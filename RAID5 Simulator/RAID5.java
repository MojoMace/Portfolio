import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class RAID5 {
    int blockSize;
    int numDrives;
    String command;
    String filename;
    private static int currentBlock;

    public RAID5(int disks, int blockSize, String command, String filename){
        this.blockSize = blockSize;
        this.numDrives = disks;
        this.command = command;
        this.filename = filename;
        this.currentBlock = 0;
        switcher(this.command);
    }

    private void switcher(String command){

        switch (command.toLowerCase())
        {
            case "read":
                parseData(readData());
                break;
            case "write":
                readBytes();
                break;
            case "rebuild":
                String badDisk = "";
                if(this.filename.contains("./")){
                    badDisk = this.filename.substring(this.filename.lastIndexOf("./")+2,this.filename.length());
                }
                else
                    badDisk = this.filename;
                rebuildDiskMain(badDisk);
                break;
            default:
                System.out.println(String.format("%s is not a valid option",command));
                System.exit(0);
        }
    }

    // Rebuilding
    private void rebuildDiskMain(String disk){
        HashMap<String,byte[]> disks = readGoodDisks(disk);
        rebuildDisk(disks,disk);
    }

    private void rebuildDisk(HashMap<String,byte[]> disks,String badDisk){
        int badDriveNum = Integer.parseInt(badDisk.substring(badDisk.length()-1));
        int iterations = -99;
        for(byte[] b: disks.values()) {
            iterations = b.length / this.blockSize;
            break;
        }
        byte[] diskRepairedBytes = new byte[0];
        int start = 0;
        int end = this.blockSize;
        Boolean isParity = false;
        for(int i = 0; i < iterations; i++){
            HashMap<Integer,byte[]> currentBytes = new HashMap<>();
            int parity = findParityDrive();
            //Is bad drive the parity drive
            if (badDriveNum == parity)
                isParity = true;
            for(int d = 0; d < 4; d++) {
                // If bad drive number, do nothing
                if (d == badDriveNum)
                    continue;
                // If good drive number
                else {
                    // Get all good drive data
                    String diskFile = String.format("disk.%d", d);
                    // Get the data from the disk
                    byte[] diskData = disks.get(diskFile);
                    // Read this.blockSize of data
                    byte[] current = copyArray(diskData, start, end);
                    // Store disk.i and its current subset byte[]
                    currentBytes.put(Integer.valueOf(d),current);
                }
            }
            // derive bad drive data from good drives
            diskRepairedBytes = updateArray(diskRepairedBytes,rebuildStripe(currentBytes,parity,isParity));
            start+=this.blockSize;
            end+=this.blockSize;
            incrementCurrentBlock();
            }

        //Convert to string
        String toFile = new String(diskRepairedBytes, StandardCharsets.UTF_8);
        File fp = new File(this.filename);
        try {
            if (!fp.exists())
                fp.createNewFile();
            FileWriter fw = new FileWriter(fp);
            fw.write(toFile);
            fw.close();
            System.out.println(String.format("Successfully written to %s",this.filename));
        } catch (IOException e){}
    }

    private byte[] rebuildStripe(HashMap<Integer,byte[]> goodDisks,int parityDrive, Boolean isParity){
        byte[] out;
        if (isParity){
            out = findParity(goodDisks);
        }
        else{
            out = goodDisks.get(parityDrive);
            for(int i = 3; i >= 0; i--){
                if(goodDisks.containsKey(i) && i != parityDrive){
                    byte[] b = goodDisks.get(i);
                    for(int j = 0; j < out.length; j++){
                        out[j] = (byte) (out[j] ^ b[j]);
                    }
                }
            }
        }
        return out;
    }

    private HashMap<String,byte[]> readGoodDisks(String badDisk){
        for(int i = 0; i < 4; i++)
        {
            String name = String.format("disk.%d",i);
            File fp = new File(name);
            if (name.equals(badDisk))
                continue;
            else if (fp.exists())
                continue;
            else {
                System.out.println(String.format("File %s does not exist", i));
                System.exit(0);
            }
        }
        // Files needed for rebuild all exist
        FileInputStream inputStream;
        HashMap<String,byte[]> diskData = new HashMap<>();
        // Read over each disk (file)
        for(int i = 0; i < 4; i++) {
            String filename = String.format("disk.%d",i);
            if (filename.equals(badDisk))
                continue;
            byte[] ba;
            try {
                inputStream = new FileInputStream(filename);
                ba = inputStream.readAllBytes();
                diskData.put(filename,ba);
            }catch (IOException e){};
        }
        return diskData;
    }

    // Reading
    private void parseData(HashMap<String,byte[]> disks){
        int iterations = disks.get("disk.0").length/this.blockSize;
        byte[] fullBytes = new byte[0];
        int start = 0;
        int end = this.blockSize;
        for(int i = 0; i < iterations; i++){
            int parity = findParityDrive();
            for(int d = 0; d < 4; d++){
                if (d == parity)
                    continue;
                else{
                    String diskFile = String.format("disk.%d",d);
                    // Get the data from the disk
                    byte[] diskData = disks.get(diskFile);
                    // Read this.blockSize of data
                    byte[] current = copyArray(diskData,start,end);
                    fullBytes = updateArray(fullBytes,current);
                }
            }
            start+=this.blockSize;
            end+=this.blockSize;
            incrementCurrentBlock();
        }
        //Convert to string
        String toFile = new String(fullBytes, StandardCharsets.UTF_8).trim();
        File fp = new File(this.filename);
        try {
            if (!fp.exists())
                fp.createNewFile();
            FileWriter fw = new FileWriter(fp);
            fw.write(toFile);
            fw.close();
            System.out.println(String.format("Successfully written to %s",this.filename));
        } catch (IOException e){}

    }

    private byte[] updateArray(byte[] a, byte[] b){
        byte[] out = new byte[a.length + b.length];
        for(int i = 0; i < out.length; i++)
        {
            if (i < a.length){
                out[i] = a[i];
            }
            else
                out[i] = b[i - a.length];
        }
        return out;
    }

    private HashMap<String,byte[]> readData(){
        for(int i = 0; i < 4; i++)
        {
            String name = String.format("disk.%d",i);
            File fp = new File(name);
            if (fp.exists())
                continue;
            else {
                System.out.println(String.format("File %s does not exist", i));
                System.exit(0);
            }
        }
        // Files all exist
        FileInputStream inputStream;
        HashMap<String,byte[]> diskData = new HashMap<>();
        // Read over each disk (file)
        for(int i = 0; i < 4; i++) {
            String filename = String.format("disk.%d",i);
            byte[] ba;
            try {
                inputStream = new FileInputStream(filename);
                ba = inputStream.readAllBytes();
                diskData.put(filename,ba);
            }catch (IOException e){};
        }
        return diskData;
    }
    // Writing
    private void readBytes() {
        FileInputStream inputStream;
        try{
            inputStream = new FileInputStream(this.filename);
            try {
                while (inputStream.available() > 0){
                    byte[] currentBytes = inputStream.readNBytes(this.blockSize * (this.numDrives - 1));
                    if (currentBytes.length != this.blockSize * (this.numDrives - 1))
                        currentBytes = fillArray(currentBytes);
                    splitBlock(currentBytes);
                    this.incrementCurrentBlock();
                }
            } catch (IOException e) {
                System.out.println("Error Reading Bytes from file");
                System.exit(0);
            }
        } catch(FileNotFoundException f)
        {
            System.out.println(String.format("Could not locate file %s", this.filename));
            System.exit(0);
        }
    }

    private void splitBlock(byte[] bytes){
        HashMap<Integer,byte[]> drives = new HashMap<>();

        //find the parity drive
        int parityDrive = findParityDrive();
        int start = 0;
        int end = this.blockSize;

        //split into n-1 parts
        for(int i = 0; i < this.numDrives; i++){
            if (i != parityDrive) {
                drives.put(i, copyArray(bytes, (start), (end)));
                start+=this.blockSize;
                end+=this.blockSize;
            }
        }

        //find the parity (XoR)
        drives.put(parityDrive,findParity(drives));

        //Write to file
        Thread threads[] = new Thread[4];
        for(Integer i: drives.keySet()){
            Thread thread = new WorkerThread(i,drives.get(i));
            threads[i] = thread;
        }
        try{
            for(Thread t: threads){
                t.start();
                t.join();
            }
        }catch(InterruptedException e) {System.out.println("Error with thread joining");}
    }

    private byte[] findParity(HashMap<Integer,byte[]> map){
        byte[] parity = null;
        for(byte[] b: map.values()){
            if (parity == null) {
                parity = copyArray(b,0,b.length);
                continue;
            }
            else{
                for(int i = 0; i < b.length; i++){
                    parity[i] = (byte) (parity[i] ^ b[i]);
                }
            }
        }
        return parity;
    }

    private byte[] copyArray(byte[] b, int start, int end){
        byte[] out = new byte[this.blockSize];
        int index = 0;
        for(int i = start; i < end; i++){
            out[index] = b[i];
            index++;
        }
        return out;
    }

    private byte[] fillArray(byte[] ba){
        byte[] newArray = new byte[this.blockSize * (this.numDrives - 1)];
        for (int i = 0; i < newArray.length; i++){
            if (i < ba.length - 1)
                newArray[i] = ba[i];
            else
                newArray[i] = 0;
        }
        return newArray;
    }

    private void incrementCurrentBlock(){
        this.currentBlock++;
    }

    // Return drive for parity storage of current block
    private int findParityDrive(){
        int parity = this.currentBlock % this.numDrives + 1;
        if (parity >= this.numDrives)
            parity = 0;
        return parity;
    }

    // Worker Threads
    class WorkerThread extends Thread {
        int drive;
        byte[] data;
        String fp;

        public WorkerThread(int diskDrive, byte[] data){
            this.drive = diskDrive;
            this.data = data;
            this.fp = String.format("disk.%d",drive);
        }
        // Generate 4 disk files disk.0, .... disk.N-1
        public void run(){
            try{
                File file = new File(fp);
                if (!file.exists())
                    file.createNewFile();
                FileOutputStream fos = new FileOutputStream(this.fp, true);
                fos.write(this.data);
            }catch (Exception e){}
        }
    }


    public static void main(String[] args) {
        //number of member disks, the block size, the command, the filename
        if (args.length != 4){
            System.out.println(String.format("Error: Not enough arguments. Got %d expected 4", args.length));
            System.exit(0);
        }
        RAID5 raid = new RAID5(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2], args[3]);

    }
}
