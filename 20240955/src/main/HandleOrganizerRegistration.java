package main;

import java.io.File;
import java.util.ArrayList;



public class HandleOrganizerRegistration {
    private final String name;
    private final String email;

    public HandleOrganizerRegistration(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public boolean isARegisteredParticipant() {
        String filePath = "DataBase/organizersData.csv";
        File playerDataFile = new File(filePath);

        OrganizerDataLoader organizerDataLoader = new OrganizerDataLoader();
        ArrayList<String> playerData = organizerDataLoader.getOrganizerData(playerDataFile);

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
}



