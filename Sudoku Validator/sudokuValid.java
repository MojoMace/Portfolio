import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SudokuValidator {
    private int[][] sudoku_arr = new int[9][9];
    private Lock lock = new ReentrantLock();
    private Boolean overall_pass = true;
    private ArrayList<Thread> threads = new ArrayList<>();

    public SudokuValidator(String[] args) throws Exception
    {
        if (args.length > 0)
        {
            try {
                System.out.println(String.format("Reading File: \"%s\"",args[0]));
                BufferedReader br = new BufferedReader(new FileReader(args[0]));
                this.fill_2D_array(br);
            } catch (FileNotFoundException f)
            {
                System.out.println(String.format("Error: File \"%s\" not found. \nExiting...",args[0]));
                System.exit(1);
            }
        }
        else {
            System.out.println("Error: File Required... \nExiting...");
            System.exit(1);
        }
    }

    private void fill_2D_array(BufferedReader br) throws Exception
    {
        String current_line = br.readLine();
        int row = 0;
        int column = 0;

        while (current_line != null)
        {
            System.out.println(current_line);
            String[] current = current_line.split("\\s+");
            for(String s: current)
            {
                sudoku_arr[row][column] = Integer.parseInt(s);
                column++;
            }
            column = 0;
            row++;
            current_line = br.readLine();
        }
    }

    public void start() throws Exception
    {
        System.out.println("Starting Validation Process");
        readHorizontally();
        readVertically();
        readSquares();

        for(Thread t: threads)
        {
            t.start();
        }
        for(Thread t: threads)
        {
            t.join();
        }

        if (overall_pass)
            System.out.println("This Sudoku is valid");
        else
            System.out.println("This Sudoku is invalid");
    }

    private void readHorizontally()
    {
        Thread t;
        for (int i = 0; i < 9; i++) {
            t = new Worker("Horizontal", i, sudoku_arr[i], this);
            threads.add(t);
        }
    }

    private void readVertically()
    {
        Thread t;
        for (int i = 0; i < 9; i++) {
            t = new Worker("Vertical", i, sudoku_arr[i], this);
            threads.add(t);
        }

    }

    private void readSquares()
    {
        Thread t;
        int[] data;
        String name = "";

        for (int i = 0; i < sudoku_arr.length; i += 3)
        {
            for (int j = 0; j < sudoku_arr[i].length; j += 3)
            {
                int block = (((i / 3) * 3) + (j / 3));
                name+="Block : " + block + " ";
                int[][] newArray = new int[3][3];
                int newRow = 0;
                for (int k = i; k < (i + 3); k++)
                {
                    int newColumn = 0;
                    for (int l = j; l < (j + 3); l++)
                    {
                        newArray[newRow][newColumn] = sudoku_arr[k][l];
                        name+=String.format("[%-1s][%-1s] : %-3s ", newRow, newColumn, newArray[newRow][newColumn++]);
                    }
                    newRow++;
                    System.out.println();
                }
                System.out.println();
                data = new int[9];
                int index = 0;
                for(int y=0;y<3;y++){
                    for(int z=0;z<3;z++) {
                        data[index] = newArray[y][z];
                        index++;
                    }
                }
                t = new Worker("square",block,data,name,this);
                threads.add(t);
            }
        }
    }

    public void setValues(String type, int id, Boolean pass, String data){
        this.lock.lock();
        if (!pass)
            overall_pass = false;
        String out;
        String value = "";
        if (type.equals("Horizontal"))
            value = "Row";
        else if (type.equals("Vertical"))
            value = "Column";
        else
            value = "Square";

        if (pass)
            out = String.format("%s thread: %d indicated that its %s passed.",type,id,value);
        else
        {
            out = String.format("%s %d failed to pass verification.\nData: %s",value,id,data);
        }
        System.out.println(out);
        this.lock.unlock();
    }

    public static void main(String[] args) throws Exception
    {
        SudokuValidator sudoku = new SudokuValidator(args);
        sudoku.start();
    }
}

class Worker extends Thread {
    private String type;
    private int id;
    private int[] data;
    private Boolean isValid = false;
    private int[] marked = new int[9];
    private String name;
    private SudokuValidator sudoku;

    public Worker(String type, int id, int[] data, SudokuValidator sudoku)
    {
        this.type = type;
        this.id = id;
        this.data = data;
        this.sudoku = sudoku;
    }
    public Worker(String type, int id, int[] data,String name, SudokuValidator sudoku)
    {
        this.type = type;
        this.id = id;
        this.data = data;
        this.name = name;
        this.sudoku = sudoku;
    }

    public void run()
    {
        String out = "";
        if (type.equals("square"))
            out = name;
        else
        {
            for (int i : this.data)
                out += i + " ";
        }
        isValid = doWork();
        sudoku.setValues(type,id,isValid,out);
    }

    private boolean doWork()
    {
        int[] numbers = new int[9];
        for(int i = 1; i <= 9; i++)
            numbers[i-1] = i;

        for(int n: numbers)
        {
            if (findIndex(n) < 0)
                return false;
        }
        return true;
    }

    private int findIndex(int find){
        int counter = 0;
        for(int i: data) {
            if (i == find)
                return counter;
            counter++;
        }
        return -1;
    }
}
