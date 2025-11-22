package main;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.io.*;
import java.util.Scanner;

public class ParticipantTest {

    private final String testCsvPath = "DataBase/test_students_loop.csv";

    @BeforeEach
    void setUp() {
        // Create test CSV file with header
        try (FileWriter writer = new FileWriter(testCsvPath)) {
            writer.write("ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        // Clean up test file
        File testFile = new File(testCsvPath);
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    void testParticipantSurveyAssignsLeader() {
        // Input: High scores (5,5,5,5,5) -> totalScore = 100 -> "Leader"
        String input = "5\n5\n5\n5\n5\nValorant\n8\n1\ny\n";
        Scanner scanner = new Scanner(input);

        Participant p = new Participant("Chanura", "chanura@test.com");
        p.participantSurvey(scanner);

        assertEquals("Leader", p.getPersonalityType());
        assertEquals("Valorant", p.getInterestSport());
        assertEquals(8, p.getSkillLevel());
        assertEquals("Strategist", p.getPreferredRole());
    }

    @Test
    void testParticipantSurveyAssignsBalanced() {
        // Input: Medium-high scores (4,4,4,4,4) -> totalScore = 80 -> "Balanced"
        String input = "4\n4\n4\n4\n4\nDota\n7\n2\ny\n";
        Scanner scanner = new Scanner(input);

        Participant p = new Participant("Tom", "tom@test.com");
        p.participantSurvey(scanner);

        assertEquals("Balanced", p.getPersonalityType());
        assertEquals("Dota", p.getInterestSport());
        assertEquals(7, p.getSkillLevel());
        assertEquals("Attacker", p.getPreferredRole());
    }

    @Test
    void testParticipantSurveyAssignsThinker() {
        // Input: Medium scores (3,3,3,3,3) -> totalScore = 60 -> "Thinker"
        String input = "3\n3\n3\n3\n3\nFIFA\n6\n3\ny\n";
        Scanner scanner = new Scanner(input);

        Participant p = new Participant("Lily", "lily@test.com");
        p.participantSurvey(scanner);

        assertEquals("Thinker", p.getPersonalityType());
        assertEquals("FIFA", p.getInterestSport());
        assertEquals(6, p.getSkillLevel());
        assertEquals("Defender", p.getPreferredRole());
    }

    @Test
    void testParticipantSurveyWithLowScores() {
        // Input: Low scores (2,2,2,2,2) -> totalScore = 40 -> Below "Thinker" threshold
        String input = "2\n2\n2\n2\n2\nBasketball\n5\n4\ny\n";
        Scanner scanner = new Scanner(input);

        Participant p = new Participant("Mark", "mark@test.com");
        p.participantSurvey(scanner);

        // Score = 40, which is below 50, so checkPersonalityType() returns false
        // PersonalityType should not be set
        assertNull(p.getPersonalityType());
        assertEquals("Basketball", p.getInterestSport());
        assertEquals(5, p.getSkillLevel());
        assertEquals("Supporter", p.getPreferredRole());
    }

    @Test
    void testCalculateTotalScore() {
        Participant p = new Participant("TestUser", "test@test.com");

        // Test score calculation: (5+4+3+2+1) * 4 = 60
        p.calculateTotalScore(5, 4, 3, 2, 1);

        assertEquals(60, p.getTotalScore());
    }

    @Test
    void testCheckPersonalityTypeLeader() {
        Participant p = new Participant("TestUser", "test@test.com");

        // Score >= 90 should result in "Leader"
        p.setTotalScore(95);
        boolean result = p.checkPersonalityType();

        assertTrue(result);
        assertEquals("Leader", p.getPersonalityType());
    }

    @Test
    void testCheckPersonalityTypeBalanced() {
        Participant p = new Participant("TestUser", "test@test.com");

        // Score >= 70 and < 90 should result in "Balanced"
        p.setTotalScore(75);
        boolean result = p.checkPersonalityType();

        assertTrue(result);
        assertEquals("Balanced", p.getPersonalityType());
    }

    @Test
    void testCheckPersonalityTypeThinker() {
        Participant p = new Participant("TestUser", "test@test.com");

        // Score >= 50 and < 70 should result in "Thinker"
        p.setTotalScore(55);
        boolean result = p.checkPersonalityType();

        assertTrue(result);
        assertEquals("Thinker", p.getPersonalityType());
    }

    @Test
    void testCheckPersonalityTypeInvalid() {
        Participant p = new Participant("TestUser", "test@test.com");

        // Score < 50 should return false and not set personality type
        p.setTotalScore(40);
        boolean result = p.checkPersonalityType();

        assertFalse(result);
    }

    @Test
    void testParticipantSurveyWithChanges() {
        // Test changing preferences before confirmation
        String input = "4\n4\n4\n4\n4\nValorant\n7\n1\nn\n2\n9\ny\n";
        Scanner scanner = new Scanner(input);

        Participant p = new Participant("Alice", "alice@test.com");
        p.participantSurvey(scanner);

        assertEquals("Balanced", p.getPersonalityType());
        assertEquals("Valorant", p.getInterestSport());
        assertEquals(9, p.getSkillLevel()); // Changed from 7 to 9
        assertEquals("Strategist", p.getPreferredRole());
    }

    @Test
    void testParticipantToString() {
        Participant p = new Participant("John", "john@test.com");
        p.setTotalScore(80);
        p.setPersonalityType("Balanced");

        String result = p.toString();

        assertTrue(result.contains("John"));
        assertTrue(result.contains("john@test.com"));
        assertTrue(result.contains("Balanced"));
    }
}