package main;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

public class HandleUploadedDataCsvFiles {
    private String fileName;
    private String uploadCsvFileName;


    public String getFileName(){
        return fileName;
    }
    public void setFileName(String fileName){
        this.fileName=fileName;
    }
    public String getUploadCsvFileName(){
        return uploadCsvFileName;
    }

    public  void dataFileImport(Scanner input) {
        InputValidator inputValidator = new InputValidator();
        try {
            System.out.println("\n\n" + "-".repeat(80));
            System.out.println("\n                      IMPORT PARTICIPANT DATA\n");
            System.out.println("-".repeat(80));
            System.out.println("\n");

            String filePath = inputValidator.isValidStringInput(input,
                    "Enter CSV file path (or 'q' to cancel): ");

            if (filePath == null) {
                System.out.println("🔙 Import cancelled.");
                return;
            }


            createNewCsvFile(filePath);
            uploadCsvFileName =getFileName();

            System.out.println("✅ Data imported successfully from: " + uploadCsvFileName);

        } catch (Exception e) {
            System.out.println("⚠️ Error importing data: " + e.getMessage());
        }
    }

    public void createNewCsvFile(String filePath){
        InputValidator inputValidator = new InputValidator();
        Path sourcePath = Paths.get(filePath);

        //  Check if the file exists
        if (!Files.exists(sourcePath)) {
            System.out.println("❌ The file does not exist at: " + sourcePath);
            return;
        }

        //  Get the current working directory
        Path currentDir = Paths.get(System.getProperty("user.dir"));

        //  Set the destination path
        Path destinationPath = currentDir.resolve(sourcePath.getFileName());
//        System.out.println(sourcePath.getFileName());

        setFileName(String.valueOf(sourcePath.getFileName()));
        //  Copy file
        try {
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            inputValidator.display("✅ File is processing...");

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
        ParticipantDataLoader playerDataLoader=new ParticipantDataLoader();
        boolean isValid=playerDataLoader.validatePlayerData(String.valueOf(sourcePath.getFileName()));


        if(!isValid){
            System.out.println("⚠️ CSV upload failed: Required fields are missing.Please try again!");
            deleteCsvFile();
            System.exit(0);
        }
        if(isCsvFile && isValid){
//            System.out.println("✅ File is accepted and imported successfully!\n");
            inputValidator.display("✅ File is accepted and imported successfully!\n");

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

