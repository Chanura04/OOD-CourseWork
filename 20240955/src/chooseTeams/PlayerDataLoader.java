package chooseTeams;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class PlayerDataLoader {
    public ArrayList<String> getPlayerData(){
        String filePath = "data/participants_sample.csv";
        String line;
        String[] player=new String[8];

        ArrayList<String> playerData = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
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
        boolean isDataSetValid=validatePlayerData(playerData);

        if(!isDataSetValid){
            System.out.println("Invalid player data set. Please check the file format and try again!");
            System.exit(0);
        }


        return playerData;
    }

    public boolean validatePlayerData(ArrayList<String> playerData){

        if(playerData.isEmpty()){
            return false;
        }
        String[] requiredColumns={"ID","Name","Email","PreferredGame","SkillLevel","PreferredRole","PersonalityScore","PersonalityType"};
        int i=0;
        String raw=playerData.get(i).replace("[", "").replace("]", "");
        String[] cleanedRaw=raw.split(",");
        for (int j = 0; j < cleanedRaw.length; j++) {
            cleanedRaw[j] = cleanedRaw[j].trim(); // remove extra spaces
        }

        Set<String> cleanedRawSet=new HashSet<>(Arrays.asList(cleanedRaw));
//        System.out.println("Columns are:"+Arrays.toString(cleanedRaw));

        boolean allPresent = true;
        for (String col : requiredColumns) {
            if (!cleanedRawSet.contains(col)) {
                System.out.println("Missing column: " + col);
                allPresent = false;
            }
        }
//        System.out.println("All columns are present: " + allPresent);

        return allPresent;
    }
}
