package chooseTeams;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class TeamFormationTask implements Runnable {
    private final TeamMembersSelection teamSelector;
    private final int leaderCount;
    private final int balancerCount;
    private final int thinkerCount;
    private final int maxAttempts;
    private final CountDownLatch latch;
    private final Object teamLock;

    public TeamFormationTask(TeamMembersSelection teamSelector,
                             int leaderCount,
                             int balancerCount,
                             int thinkerCount,
                             int maxAttempts,
                             CountDownLatch latch,
                             Object teamLock) {
        this.teamSelector = teamSelector;
        this.leaderCount = leaderCount;
        this.balancerCount = balancerCount;
        this.thinkerCount = thinkerCount;
        this.maxAttempts = maxAttempts;
        this.latch = latch;
        this.teamLock = teamLock;
    }

    @Override
    public void run() {
        try {
            formTeams();
        } finally {
            if (latch != null) {
                latch.countDown();
            }
        }
    }

    private void formTeams() {
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            ArrayList<String> team = new ArrayList<>();
            ArrayList<String> leaders, balancers, thinkers;

            // Synchronized selection of players
            synchronized (teamLock) {
                // Check if enough players available
                if (!teamSelector.hasEnoughPlayersForTeam(leaderCount, balancerCount, thinkerCount)) {
                    break; // Not enough players, stop this thread
                }

                // Select players
                leaders = teamSelector.selectUniqueLeaders(leaderCount);
                balancers = teamSelector.selectUniqueBalancers(balancerCount);
                thinkers = teamSelector.selectUniqueThinkers(thinkerCount);

                if (leaders.isEmpty() || balancers.isEmpty() || thinkers.isEmpty()) {
                    continue; // Try again
                }

                // Build team
                team.addAll(leaders);
                team.addAll(balancers);
                team.addAll(thinkers);

                // Validate before committing
                if (team.size() == teamSelector.getTeamPlayerCount() &&
                        teamSelector.validateTeam(team) &&
                        teamSelector.isGameCountValid(team)) {

                    // Add to results and remove from pools
                    teamSelector.addTeam(new ArrayList<>(team));
                    teamSelector.removeSelectedPlayers(leaders, balancers, thinkers);
                    break; // Successfully formed a team, exit this thread
                }
            }

            // Small delay to prevent CPU spinning
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}