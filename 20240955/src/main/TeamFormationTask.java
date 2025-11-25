package main;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TeamFormationTask implements Runnable {

    private static final Logger logger = Logger.getLogger(TeamFormationTask.class.getName());

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
        String thread = Thread.currentThread().getName();
        logger.info(thread + " started team formation.");

        try {
            formTeams();
        } finally {
            latch.countDown();
        }
    }

    private void formTeams() {
        String thread = Thread.currentThread().getName();
        int teamsFormedByThisThread = 0;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {

            // Check if we have enough players (thread-safe check)
            if (!teamSelector.hasEnoughPlayersForTeam(leaderCount, balancerCount, thinkerCount)) {
                logger.info(thread + " : Not enough players left. Stopping after forming "
                        + teamsFormedByThisThread + " teams.");
                break;
            }

            //SELECT AND REMOVE IN ONE ATOMIC OPERATION
            ArrayList<String> leaders = teamSelector.selectUniqueLeaders(leaderCount);
            ArrayList<String> balancers = teamSelector.selectUniqueBalancers(balancerCount);
            ArrayList<String> thinkers = teamSelector.selectUniqueThinkers(thinkerCount);

            // If selection failed (not enough players), stop
            if (leaders.isEmpty() || balancers.isEmpty() || thinkers.isEmpty()) {
                // Return any that were selected
                teamSelector.returnPlayersToPool(leaders, balancers, thinkers);
                logger.info(thread + " : Selection failed after forming "
                        + teamsFormedByThisThread + " teams. Stopping.");
                break;
            }

            // BUILD TEAM (players are already removed from pool)
            ArrayList<String> team = new ArrayList<>();
            team.addAll(leaders);
            team.addAll(balancers);
            team.addAll(thinkers);

            // VALIDATION (outside lock)
            if (!teamSelector.checkPlayerCount(team.size())) {
                teamSelector.returnPlayersToPool(leaders, balancers, thinkers);
                continue;
            }

            if (!teamSelector.validateTeam(team)) {
                teamSelector.returnPlayersToPool(leaders, balancers, thinkers);
                continue;
            }

            if (!teamSelector.isGameCountValid(team)) {
                teamSelector.returnPlayersToPool(leaders, balancers, thinkers);
                continue;
            }

            // TEAM IS VALID
            teamSelector.addTeam(new ArrayList<>(team));
            teamsFormedByThisThread++;

            logger.info(thread + " : 🔥 Successfully formed team #" + teamsFormedByThisThread);

        }

        logger.info(thread + " : Finished. Formed " + teamsFormedByThisThread + " teams total.");
    }
}