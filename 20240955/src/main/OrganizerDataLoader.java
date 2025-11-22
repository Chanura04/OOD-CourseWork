package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class OrganizerDataLoader {

    public ArrayList<String> getOrganizerData(File csvFilePath){
        String line;
        String[] player=new String[3];

        ArrayList<String> playerData = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                player[0]=values[0];
                player[1]=values[1];
                player[2]=values[2];

                playerData.add(Arrays.toString(player));

            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return playerData;
    }

}
