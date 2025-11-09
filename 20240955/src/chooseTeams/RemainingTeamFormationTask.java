package chooseTeams;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class RemainingTeamFormationTask implements Runnable {
    private final HandleRemainingPlayers remainingHandler;
    private final int maxAttempts;
    private final double minValue;
    private final double maxValue;
    private final CountDownLatch latch;
    private final Object remainingLock;

    public RemainingTeamFormationTask(HandleRemainingPlayers remainingHandler,
                                      int maxAttempts,
                                      double minValue,
                                      double maxValue,
                                      CountDownLatch latch,
                                      Object remainingLock) {
        this.remainingHandler = remainingHandler;
        this.maxAttempts = maxAttempts;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.latch = latch;
        this.remainingLock = remainingLock;
    }

    @Override
    public void run() {
        try {
            formRemainingTeams();
        } finally {
            if (latch != null) {
                latch.countDown();
            }
        }
    }

    private void formRemainingTeams() {
        int consecutiveFailures = 0;

        while (consecutiveFailures < 50) {
            ArrayList<String> candidateTeam;

            // Try to form a team (synchronized)
            synchronized (remainingLock) {
                if (!remainingHandler.hasEnoughPlayers()) {
                    break; // Not enough players left
                }
                candidateTeam = remainingHandler.tryFormTeam(maxAttempts);
            }

            if (candidateTeam == null) {
                consecutiveFailures++;
                continue;
            }

            // Validate team
            if (!remainingHandler.validateTeam(candidateTeam) ||
                    !remainingHandler.isGameCountValid(candidateTeam)) {
                synchronized (remainingLock) {
                    remainingHandler.returnPlayersToPool(candidateTeam);
                }
                consecutiveFailures++;
                continue;
            }

            // Check skill sum
            int teamSkillSum = remainingHandler.checkEachFormedTeamSkillSum(candidateTeam);

            if (teamSkillSum >= minValue && teamSkillSum <= maxValue) {
                synchronized (remainingLock) {
                    remainingHandler.addValidTeam(new ArrayList<>(candidateTeam));
                    consecutiveFailures = 0; // Reset on success
                }
            } else {
                consecutiveFailures++;
            }

            // Small delay
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}