package main;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class HandleDataCsvFilesTest {

    private HandleDataCsvFiles handleDataCsvFiles;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        handleDataCsvFiles = new HandleDataCsvFiles();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        // Clean up any created files
        if (handleDataCsvFiles.getFileName() != null) {
            Path currentDir = Paths.get(System.getProperty("user.dir"));
            Path filePath = currentDir.resolve(handleDataCsvFiles.getFileName());
            try {
                Files.deleteIfExists(filePath);
            } catch (Exception _) {

            }
        }
    }



    @Test
    void createNewCsvFile_FileDoesNotExist() {
        // Capture output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        handleDataCsvFiles.createNewCsvFile("nonexistent.csv");

        System.setOut(originalOut);
        String output = outputStream.toString();

        assertTrue(output.contains("The file does not exist"));
    }


    @Test
    void isCsvFile_InvalidFiles() {
        assertFalse(handleDataCsvFiles.isCsvFile("test.txt"));
        assertFalse(handleDataCsvFiles.isCsvFile("data.xlsx"));
        assertFalse(handleDataCsvFiles.isCsvFile("csv"));
        assertFalse(handleDataCsvFiles.isCsvFile(""));
    }
    @Test
    void validateUploadedCSVFile_withAllRequiredColumns_returnsTrue() throws IOException {
        // Create a CSV file with all required columns
        Path csvFile = tempDir.resolve("valid_complete.csv");
        String csvContent = "ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType\n" +
                "1,John Doe,john@example.com,Chess,Expert,Leader,85,INTJ\n";
        Files.writeString(csvFile, csvContent);

        boolean result = handleDataCsvFiles.validateUploadedCSVFile(csvFile.toString());

        assertTrue(result);

    }


    @Test
    void validateUploadedCSVFile_withExtraColumns_returnsTrue() throws IOException {
        // Test with extra columns beyond the required ones
        Path csvFile = tempDir.resolve("valid_extra_columns.csv");
        String csvContent = "ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType,ExtraColumn1,ExtraColumn2\n" +
                "3,Alice Smith,alice@example.com,Soccer,Advanced,Midfielder,90,ESTJ,Extra1,Extra2\n";
        Files.writeString(csvFile, csvContent);

        boolean result = handleDataCsvFiles.validateUploadedCSVFile(csvFile.toString());

        assertTrue(result);
    }






}