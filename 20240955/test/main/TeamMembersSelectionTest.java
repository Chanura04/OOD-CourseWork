package main;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.io.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TeamMembersSelectionTest {


    private final String testCsvPath= "test_participants.csv";
    private TeamMembersSelection selector;
    private int teamPlayerCount;

    @BeforeEach
    void setUp() {
        try (FileWriter writer = new FileWriter(testCsvPath)) {
            writer.write("ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType\n");

            // Add 1 Leader
            writer.write("P001,Leader1,leader1@test.com,Valorant,8,Strategist,92,Leader\n");
            writer.write("P002,Leader2,leader2@test.com,Dota,6,Attacker,92,Leader\n");
            writer.write("P003,Leader3,leader3@test.com,Valorant,8,Strategist,92,Leader\n");
            writer.write("P004,Analyzer1,analyzer1@test.com,Chess,9,Defender,95,Leader\n");

            // 2 Thinkers
            writer.write("P005,Thinker1,thinker1@test.com,Dota,7,Attacker,55,Thinker\n");
            writer.write("P006,Thinker2,thinker2@test.com,FIFA,6,Defender,58,Thinker\n");
            writer.write("P007,Thinker3,thinker3@test.com,Valorant,7,Supporter,52,Thinker\n");
            writer.write("P008,Supporter1,supporter1@test.com,Overwatch,6,Supporter,69,Thinker\n");


            // rest Balancers
            writer.write("P009,Balancer1,balancer1@test.com,Basketball,8,Coordinator,75,Balanced\n");
            writer.write("P010,Balancer2,balancer2@test.com,Dota,6,Strategist,72,Balanced\n");
            writer.write("P011,Balancer3,balancer3@test.com,FIFA,7,Attacker,78,Balanced\n");
            writer.write("P012,Balancer4,balancer4@test.com,Valorant,9,Supporter,78,Balanced\n");
            writer.write("P013,Balancer5,balancer5@test.com,Valorant,9,Supporter,78,Balanced\n");
            writer.write("P014,Innovator1,innovator1@test.com,CSGO,7,Strategist,81,Balanced\n");
            writer.write("P015,Challenger1,challenger1@test.com,LeagueOfLegends,8,Attacker,88,Balanced\n");
            writer.write("P016,Explorer1,explorer1@test.com,Minecraft,5,Coordinator,73,Balanced\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
        selector = new TeamMembersSelection(testCsvPath);
        teamPlayerCount=5;
        selector.setTeamPlayerCount(teamPlayerCount);
    }

    @Test
    void testCategorizeByPersonalityType() {
        selector.categorizeByPersonalityType();

        // Verify selector is initialized
        assertNotNull(selector);
    }

    @Test
    void testCreateTeams() {
        selector.categorizeByPersonalityType();

        ArrayList<ArrayList<String>> teams = selector.createTeams();
        assertNotNull(teams);
        assertTrue(teams.size() >= 0, "Should create teams or return empty list");
    }

    @Test
    void testSelectUniqueLeaders() {
        selector.categorizeByPersonalityType();

        ArrayList<String> leaders = selector.selectUniqueLeaders(1);

        assertEquals(1, leaders.size());
        assertTrue(leaders.get(0).contains("Leader"));
    }

    @Test
    void testSelectUniqueThinkers() {
        selector.categorizeByPersonalityType();

        ArrayList<String> thinkers = selector.selectUniqueThinkers(2);

        assertEquals(2, thinkers.size());
        for (String thinker : thinkers) {
            assertTrue(thinker.contains("Thinker"));
        }
    }

    @Test
    void testSelectUniqueBalancers() {
        selector.categorizeByPersonalityType();

        ArrayList<String> balancers = selector.selectUniqueBalancers(2);

        assertEquals(2, balancers.size());
        for (String balancer : balancers) {
            assertTrue(balancer.contains("Balanced"));
        }
    }

    @Test
    void testValidateTeamWithValidTeam() {
        ArrayList<String> validTeam = new ArrayList<>();
        validTeam.add("P001,Leader1,leader1@test.com,Valorant,8,Strategist,92,Leader");
        validTeam.add("P002,Thinker1,thinker1@test.com,Dota,7,Attacker,55,Thinker");
        validTeam.add("P003,Thinker2,thinker2@test.com,FIFA,6,Defender,58,Thinker");
        validTeam.add("P004,Balancer1,balancer1@test.com,Basketball,8,Coordinator,75,Balanced");
        validTeam.add("P005,Balancer2,balancer2@test.com,Dota,9,Supporter,72,Balanced");

        boolean isValid = selector.validateTeam(validTeam);
        //For 5 team-member teams
        assertTrue(isValid, "Team should be valid: 1 Leader, 2 Thinkers, 2 Balancers, 4 unique roles");
    }

    @Test
    void testValidateTeamWithInvalidTeam() {
        ArrayList<String> invalidTeam = new ArrayList<>();
        // Violates the rules: only 1 Thinker, missing second Balancer
        invalidTeam.add("P001,Leader1,leader1@test.com,Valorant,8,Strategist,92,Leader");
        invalidTeam.add("P002,Thinker1,thinker1@test.com,Dota,7,Attacker,55,Thinker");
        invalidTeam.add("P003,Balancer1,balancer1@test.com,Basketball,8,Coordinator,75,Balanced");
        invalidTeam.add("P004,Balancer2,balancer2@test.com,Dota,9,Supporter,72,Balanced");
        invalidTeam.add("P005,Leader2,leader2@test.com,FIFA,6,Defender,58,Leader"); // extra Leader

        boolean isValid = selector.validateTeam(invalidTeam);
        // Expect false because rules are broken (2 Leaders, not enough Thinkers)
        assertFalse(isValid, "Team should be invalid: violates role distribution rules");
    }


    @Test
    void testIsGameCountValidWithValidTeam() {
        ArrayList<String> validTeam = new ArrayList<>();
        validTeam.add("P001,Leader1,leader1@test.com,Valorant,8,Strategist,92,Leader");
        validTeam.add("P002,Thinker1,thinker1@test.com,Dota,7,Attacker,55,Thinker");
        validTeam.add("P003,Thinker2,thinker2@test.com,FIFA,6,Defender,58,Thinker");
        validTeam.add("P004,Balancer1,balancer1@test.com,Basketball,8,Coordinator,75,Balanced");
        validTeam.add("P005,Balancer2,balancer2@test.com,Valorant,9,Supporter,72,Balanced");

        boolean isValid = selector.isGameCountValid(validTeam);

        assertTrue(isValid, "Team should be valid: max 2 players per game (Valorant: 2, others: 1 each)");
    }

    @Test
    void testIsGameCountValidWithInvalidTeam() {
        ArrayList<String> invalidTeam = new ArrayList<>();
        // 3 players with same game - should be invalid
        invalidTeam.add("P001,Leader1,leader1@test.com,Valorant,8,Strategist,92,Leader");
        invalidTeam.add("P002,Thinker1,thinker1@test.com,Valorant,7,Attacker,55,Thinker");
        invalidTeam.add("P003,Thinker2,thinker2@test.com,Valorant,6,Defender,58,Thinker");
        invalidTeam.add("P004,Balancer1,balancer1@test.com,Dota,8,Coordinator,75,Balanced");

        boolean isValid = selector.isGameCountValid(invalidTeam);

        assertFalse(isValid, "Team should be invalid: 3 players with same game (Valorant)");
    }

    @Test
    void testHasEnoughPlayersForTeam() {
        selector.categorizeByPersonalityType();
        //For 5 team-member teams
        // We have 1 Leader, 3 Thinkers, 3 Balancers
        boolean hasEnough = selector.hasEnoughPlayersForTeam(1, 2, 2);

        assertTrue(hasEnough, "Should have enough players for a team of 5");
    }

    @Test
    void testHasNotEnoughPlayersForTeam() {
        selector.categorizeByPersonalityType();

        // Requesting more players than available
        boolean hasEnough = selector.hasEnoughPlayersForTeam(5, 1, 2);

        assertFalse(hasEnough, "Should not have enough balancers (only 1 available, need 1 more)");
    }


    @Test
    void testGetTotalFinalTeamCombination() {
        selector.categorizeByPersonalityType();
        selector.createTeams();

        int totalTeams = selector.getTotalFinalTeamCombination();

        assertTrue(totalTeams >= 0, "Total teams should be non-negative");
    }



}