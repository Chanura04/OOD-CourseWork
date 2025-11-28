package main;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class ParticipantDataLoader {

    public ArrayList<String> getPlayerData(File csvFilePath){
        String line;
        String[] player=new String[8];

        ArrayList<String> playerData = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                player[0]=values[0];
                player[1]=values[1];
                player[2]=values[2];
                player[3]=values[3];
                player[4]=values[4];
                player[5]=values[5];
                player[6]=values[6];
                player[7]=values[7];

                playerData.add(Arrays.toString(player));

            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return playerData;
    }
    public ArrayList<String> getPlayerData(String csvFilePath){
        String line;
        String[] player=new String[8];

        ArrayList<String> playerData = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                player[0]=values[0];
                player[1]=values[1];
                player[2]=values[2];
                player[3]=values[3];
                player[4]=values[4];
                player[5]=values[5];
                player[6]=values[6];
                player[7]=values[7];

                playerData.add(Arrays.toString(player));

            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return playerData;
    }


}
