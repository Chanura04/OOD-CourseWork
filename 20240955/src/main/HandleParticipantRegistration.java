package main;

import java.io.File;
import java.util.ArrayList;


public class HandleParticipantRegistration {
    private final String name;
    private final String email;

    public HandleParticipantRegistration(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public boolean isARegisteredParticipant() {

        String basePath = System.getProperty("user.dir");
        String playerDataFile = basePath + File.separator + "DataBase" + File.separator + "ParticipantDatabase.csv";

        ParticipantDataLoader playerDataLoader = new ParticipantDataLoader();//10.1.1
        ArrayList<String> playerData = playerDataLoader.getPlayerData(playerDataFile);//10.1.2

        boolean isRegistered = false;

        for (int i = 1; i < playerData.size(); i++) {
            String raw = playerData.get(i).replace("[", "").replace("]", "").trim();
            String[] fields = raw.split(",");

            if (name.equals(fields[1].trim()) && email.equals(fields[2].trim()) ) {
                isRegistered= true;
                break;
            }
        }
        return isRegistered;
    }

    public int getRegisteredParticipantStoredRawNumber() {
        int rawNumber = 0;

        String basePath = System.getProperty("user.dir");
        String playerDataFile = basePath + File.separator + "DataBase" + File.separator + "ParticipantDatabase.csv";

        ParticipantDataLoader playerDataLoader = new ParticipantDataLoader();
        ArrayList<String> playerData = playerDataLoader.getPlayerData(playerDataFile);

        for (int i = 1; i < playerData.size(); i++) {
            String raw = playerData.get(i).replace("[", "").replace("]", "").trim();
            String[] fields = raw.split(",");

            if (name.equals(fields[1].trim()) && email.equals(fields[2].trim()) ) {
                rawNumber= i;
                break;
            }
        }
        return rawNumber;
    }


}



