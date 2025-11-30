package main;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;
class HandleParticipantRegistrationTest {

    private final String filePath =
            "C:\\Github Projects\\OOD-CourseWork\\20240955\\DataBase\\test_students_loop.csv";

    @BeforeEach
    void setUp() throws IOException {
        // Ensure directory exists
        File dir = new File("C:\\Github Projects\\OOD-CourseWork\\20240955\\DataBase");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Create test file with sample participants
        FileWriter writer = new FileWriter(filePath);
        writer.write("ID,Name,Email,Game,Level,Role,Score,Status\n"); // header row
        writer.write("P001,TestUser,testuser@test.com,Basketball,8,Coordinator,75,Balanced\n");
        writer.write("P002,AnotherUser,another@test.com,Dota,6,Strategist,72,Balanced\n");
        writer.close();
    }

    @AfterEach
    void tearDown() {
        // Clean up test file
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testIsARegisteredParticipant_ValidUser() {
        HandleParticipantRegistration reg =
                new HandleParticipantRegistration("TestUser", "testuser@test.com");

        assertTrue(reg.isARegisteredParticipant(),
                "Expected TestUser to be recognized as registered");
    }

    @Test
    void testIsARegisteredParticipant_InvalidUser() {
        HandleParticipantRegistration reg =
                new HandleParticipantRegistration("FakeUser", "fake@test.com");

        assertFalse(reg.isARegisteredParticipant(),
                "Expected FakeUser to NOT be recognized as registered");
    }

    @Test
    void testIsARegisteredParticipant_WrongEmail() {
        HandleParticipantRegistration reg =
                new HandleParticipantRegistration("TestUser", "wrong@test.com");

        assertFalse(reg.isARegisteredParticipant(),
                "Expected wrong email to fail registration check");
    }

}