package chooseTeams;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlayerDataLoader {
    public ArrayList<String> getPlayerData(){
//        String filePath = "./data/participants_sample.csv";
        String filePath = "students_loop.csv";
        String line;
        Map<String, String> player = new HashMap<>();

        ArrayList<String> playerData = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                player.put("id", values[0]);
                player.put("name", values[1]);
                player.put("email", values[2]);
                player.put("preferredGame", values[3]);
                player.put("skillLevel", values[4]);
                player.put("preferredRole", values[5]);
                player.put("personalityScore", values[6]);
                player.put("personalityType", values[7]);

                playerData.add(player.toString());

            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return playerData;
    }
}
