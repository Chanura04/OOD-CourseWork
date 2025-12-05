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

    //Check logged in user is a registered organizer or not
    public boolean isARegisteredOrganizer() {

        String basePath = System.getProperty("user.dir");
        String filePath = basePath + File.separator + "DataBase" + File.separator + "OrganizersDatabase.csv";

        File playerDataFile = new File(filePath);

        OrganizerDataLoader organizerDataLoader = new OrganizerDataLoader();//5.1.1
        ArrayList<String> organizerData = organizerDataLoader.getOrganizerData(playerDataFile);//5.1.2

        boolean isRegistered = false;

        for (int i = 1; i < organizerData.size(); i++) {
            String raw = organizerData.get(i).replace("[", "").replace("]", "").trim();
            String[] fields = raw.split(",");

            if (name.equals(fields[1].trim()) && email.equals(fields[2].trim()) ) {
                isRegistered= true;
                break;
            }
        }
        return isRegistered;
    }
}



