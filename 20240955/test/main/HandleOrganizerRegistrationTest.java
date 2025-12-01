package main;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
class HandleOrganizerRegistrationTest {




    @Test
    void testIsARegisteredOrganizer_ValidUser() {
        HandleOrganizerRegistration reg =
                new HandleOrganizerRegistration("abc", "abc@");

        assertTrue(reg.isARegisteredOrganizer(),
                "Expected Admin User to be recognized as registered");
    }

    @Test
    void testIsARegisteredOrganizer_InvalidUser() {
        HandleOrganizerRegistration reg =
                new HandleOrganizerRegistration("Fake Organizer", "fake@test.com");

        assertFalse(reg.isARegisteredOrganizer(),
                "Expected Fake Organizer to NOT be recognized as registered");
    }

    @Test
    void testIsARegisteredOrganizer_WrongEmail() {
        HandleOrganizerRegistration reg =
                new HandleOrganizerRegistration("abc", "wrong@test.com");

        assertFalse(reg.isARegisteredOrganizer(),
                "Expected wrong email to fail registration check");
    }
}
