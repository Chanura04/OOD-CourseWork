package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Organizer extends User{
    private String id;

    public Organizer(String name, String email) {
        super(name, email);
        setId(newOrganizerId());
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public void storeRegisteredOrganizerData(){
        File organizerDataFile = new File("C:\\Github Projects\\OOD-CourseWork\\20240955\\DataBase\\organizersData.csv");
        //save to csv file
        try (FileWriter writer = new FileWriter(organizerDataFile, true)) {
            String[] data={
                    getId(),getName(),getEmail()
            };
            writer.append("\n");
            writer.append(String.join(",", data));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public int getStoredLastId(){
        String filePath = "C:\\Github Projects\\OOD-CourseWork\\20240955\\DataBase\\organizersData.csv";
        File playerDataFile = new File(filePath);

        OrganizerDataLoader organizerDataLoader = new OrganizerDataLoader();
        ArrayList<String> organizerData = organizerDataLoader.getOrganizerData(playerDataFile);

        if(organizerData.isEmpty()){
            return -1;
        }
        String lastPlayer= organizerData.getLast();
        String[] playerData=lastPlayer.split(",");
        String id=playerData[0];
        String idValue=id.split("R")[1];
        return Integer.parseInt(idValue);
    }

    public String newOrganizerId(){
        int previousId=getStoredLastId();
        if(previousId==-1){
            setId("R1");
        }
        setId("R"+(previousId + 1));
        return "R"+(previousId + 1);
    }
}
