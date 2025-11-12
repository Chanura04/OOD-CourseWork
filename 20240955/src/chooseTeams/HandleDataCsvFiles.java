package chooseTeams;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class HandleDataCsvFiles {
    private String fileName;

    public String getFileName(){
        return fileName;
    }
    public void setFileName(String fileName){
        this.fileName=fileName;
    }


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

        setFileName(String.valueOf(sourcePath.getFileName()));
        //  Copy file
        try {
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("✅ File is processing...");
//            System.out.println(destinationPath.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("⚠️ Error copying file: " + e.getMessage());
        }

        boolean isCsvFile=isCsvFile(String.valueOf(sourcePath.getFileName()));
        if(!isCsvFile){
            System.out.println("⚠️ Invalid file format. Please upload a csv file!");
            deleteCsvFile();
            return;
        }

        //Check if the required fields are included.
        PlayerDataLoader playerDataLoader=new PlayerDataLoader();
        boolean isValid=playerDataLoader.validatePlayerData(String.valueOf(sourcePath.getFileName()));


        if(!isValid){
            System.out.println("⚠️ CSV upload failed: Required fields are missing.Please try again!");
            deleteCsvFile();
            System.exit(0);
        }
        if(isCsvFile && isValid){
            System.out.println("✅ File is accepted and imported successfully!\n");
        }

    }

    public void deleteCsvFile(){
        Path currentDir = Paths.get(System.getProperty("user.dir"));
        Path filePath = currentDir.resolve(fileName);

        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("🗑️ File rejected!!! " + filePath.toAbsolutePath());
            } else {
                System.out.println("❌ File not found in current directory: " + fileName);
            }
        } catch (IOException e) {
            System.out.println("⚠️ Error deleting file: " + e.getMessage());
        }

    }
    public void deleteCsvFile(File file){
        Path currentDir = Paths.get(System.getProperty("user.dir"));
        Path filePath = currentDir.resolve(file.getName());

        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            } else {
                System.out.println("❌ File not found in current directory: " + fileName);
            }
        } catch (IOException e) {
            System.out.println("⚠️ Error deleting file: " + e.getMessage());
        }

    }

    private boolean isCsvFile(String filePath){
        File file = new File(filePath);
        if(file.isFile() && file.getName().endsWith(".csv")){
            return true;
        }
        return false;
    }







}

