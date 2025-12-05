package main;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.logging.*;

public class HandleDataCsvFiles {
    private String fileName;
    private String uploadCsvFileName;
    private static final Logger logger = Logger.getLogger(HandleDataCsvFiles.class.getName());


    public String getFileName(){
        return fileName;
    }
    public void setFileName(String fileName){
        this.fileName=fileName;
    }
    public String getUploadCsvFileName(){
        return uploadCsvFileName;
    }

    //Check and Accept uploaded csv file.
    public  void dataFileImport(Scanner input) {
        InputValidator inputValidator = new InputValidator();//2.2.1
        try {
            System.out.println("\n\n" + "-".repeat(80));
            System.out.println("\n                      IMPORT PARTICIPANT DATA\n");
            System.out.println("-".repeat(80));
            System.out.println("\n");

            String filePath = inputValidator.isValidStringInput(input,
                    "Enter CSV file path (or 'q' to cancel): ");//2.2.2

            if (filePath == null) {
                System.out.println("🔙 Import cancelled.");
                return;
            }
            createNewCsvFile(filePath);//3.2
            uploadCsvFileName =getFileName();
        } catch (Exception e) {
            System.out.println("⚠️ Error importing data: " + e.getMessage());
        }
    }

    //If accepted, create a new csv file in the same directory with the same name.
    public void createNewCsvFile(String filePath){
        InputValidator inputValidator = new InputValidator();
        Path sourcePath = Paths.get(filePath);

        //  Check if the file exists
        if (!Files.exists(sourcePath)) {
            System.out.println("❌ The file does not exist!!");
            return;
        }

        //  Get the current working directory
        Path currentDir = Paths.get(System.getProperty("user.dir"));

        //  Set the destination path
        Path destinationPath = currentDir.resolve(sourcePath.getFileName());

        setFileName(String.valueOf(sourcePath.getFileName()));
        //  Copy file
        try {
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            inputValidator.display("✅ File is processing...");

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
        boolean isCSVFileValid=validateUploadedCSVFile(String.valueOf(sourcePath.getFileName()));//3.4


        if(!isCSVFileValid){
            System.out.println("⚠️ CSV upload failed: Required fields are missing.Please try again!");
            deleteCsvFile();
            return;
        }
        if(isCsvFile && isCSVFileValid){
            inputValidator.display("✅ File is accepted and imported successfully!\n");//3.6
            logger.info("✅ File is accepted and imported successfully!");
        }

    }

    //Check if the required fields are included.
    public boolean validateUploadedCSVFile(String csvFilePath) {
        ArrayList<String> playerData = new ArrayList<>();
        String[] column=new String[8];

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line = br.readLine(); // Read the first line only

            if (line != null) {
                String[] values = line.split(",");

                if (values.length < 8) {
                    return false;
                }

                for (int idx = 0; idx < 8; idx++) {
                    column[idx] = values[idx];
                }
                playerData.add(Arrays.toString(column));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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

        boolean allPresent = true;
        for (String col : requiredColumns) {
            if (!cleanedRawSet.contains(col)) {
                System.out.println("Missing column: " + col);
                allPresent = false;
            }
        }
        return allPresent;
    }

    //If file is invalid delete the file from the directory.
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

    public  boolean isCsvFile(String filePath){
        File file = new File(filePath);
        if(file.isFile() && file.getName().endsWith(".csv")){
            return true;
        }
        return false;
    }

}

