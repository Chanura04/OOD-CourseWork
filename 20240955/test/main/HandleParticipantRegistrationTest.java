package main;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HandleParticipantRegistrationTest {


    @Test
    void testIsARegisteredOrganizer_ValidUser() {
        HandleParticipantRegistration reg =
                new HandleParticipantRegistration("Participant_1", "user1@university.edu");

        assertTrue(reg.isARegisteredParticipant(),
                "Expected Admin User to be recognized as registered");
    }

    @Test
    void testIsARegisteredOrganizer_InvalidUser() {
        HandleParticipantRegistration reg =
                new HandleParticipantRegistration("Fake Participant_1", "fake@test.com");

        assertFalse(reg.isARegisteredParticipant(),
                "Expected Fake Participant to NOT be recognized as Participant");
    }

    @Test
    void testIsARegisteredOrganizer_WrongEmail() {
        HandleParticipantRegistration reg =
                new HandleParticipantRegistration("Participant_1", "wrong@test.com");

        assertFalse(reg.isARegisteredParticipant(),
                "Expected wrong email to fail registration check");
    }
}
