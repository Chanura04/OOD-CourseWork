package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ViewRemainingPlayers {

    public void viewPlayersInCsvFile(String filename){
        try(BufferedReader br=new BufferedReader((new FileReader(filename)))) {

            String line;
            while ((line=br.readLine())!=null){
                String[] columns = line.split(",");
                System.out.printf("%-15s %-18s %-27s %-20s %-15s %-16s %-18s %-19s%n", columns[0], columns[1], columns[2], columns[3],columns[4], columns[5], columns[6], columns[7]);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void viewPlayersInCsvFile(File file){
        try(BufferedReader br=new BufferedReader((new FileReader(file)))) {

            String line;
            while ((line=br.readLine())!=null){
                String[] columns = line.split(",");
                System.out.printf("%-15s %-18s %-27s %-20s %-15s %-16s %-18s %-19s %-17s%n", columns[0], columns[1], columns[2], columns[3],columns[4], columns[5], columns[6], columns[7],columns[8]);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
