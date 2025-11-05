package chooseTeams;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class HandleDataCsvFiles {
    public String fileName;

    public void handleDataCsvFiles() {
            // Example user input (you can replace with Scanner input)
        String userInput = "participants_sample.csv";

            // Current working directory
        Path currentDir = Paths.get("").toAbsolutePath();
        Path userPath = Paths.get(userInput).toAbsolutePath();

        if (userPath.startsWith(currentDir)) {
            if (Files.exists(userPath)) {
                System.out.println("✅ File is inside the current directory and exists.");
            } else {
                System.out.println("⚠️ File path is within current directory but file does not exist.");
            }
        } else {
            System.out.println("❌ File is NOT inside the current directory.");
        }

    }

    public void createNewCsvFile(String filePath){
        Path sourcePath = Paths.get(filePath);

        //  Check if the file exists
        if (!Files.exists(sourcePath)) {
            System.out.println("❌ The file does not exist at: " + sourcePath);
            return;
        }

        //  Get the current working directory
        Path currentDir = Paths.get(System.getProperty("user.dir"));

        //  Set the destination path (same filename in current directory)
        Path destinationPath = currentDir.resolve(sourcePath.getFileName());
        System.out.println(sourcePath.getFileName());
        fileName=String.valueOf(sourcePath.getFileName());
        //  Copy file
        try {
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("✅ File copied to current directory:");
            System.out.println(destinationPath.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("⚠️ Error copying file: " + e.getMessage());
        }

        PlayerDataLoader playerDataLoader=new PlayerDataLoader();
        boolean isValid=playerDataLoader.validatePlayerData(String.valueOf(sourcePath.getFileName()));


        if(!isValid){
            System.out.println("Invalid player data set. Please check the file format and try again!");
            System.exit(0);
        }

    }







}

