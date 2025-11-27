package main;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class SurveyProcessorTask implements Runnable {
    private final Participant player;
    private final CountDownLatch latch;
    private final String filePath;
    private static final Logger logger = Logger.getLogger(SurveyProcessorTask.class.getName());
    public SurveyProcessorTask(Participant player, String filePath, CountDownLatch latch) {
        this.player = player;
        this.filePath = filePath;
        this.latch = latch;
    }

    @Override
    public void run() {
        String thread = Thread.currentThread().getName();
        logger.info(thread + " started processing survey for " + player.getName());
        try {
            processSurvey();
        } finally {
            if (latch != null) {
                latch.countDown();
            }
        }
    }

    private void processSurvey() {
        String thread = Thread.currentThread().getName();
        List<String> lines = new ArrayList<>();
        File playerDataFile = new File(filePath);

        if (!player.checkPersonalityType()) {
            System.out.println("⚠️ " + player.getName() + "'s Personality Score is very low! Please try again!\n");
            System.out.println("⚠️ Survey for " + player.getName() + " has been discarded.\n");
            logger.info(thread+" : Survey for "+player.getName()+" has been discarded.Because Personality Score is very low!");
            return;
        }

        // Read file with synchronization (CLASS-LEVEL lock for file access)
        synchronized (SurveyProcessorTask.class) {
            try (BufferedReader br = new BufferedReader(new FileReader(playerDataFile))) {
                br.readLine(); // Skip header
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    if (values.length > 2 && player.getName().equals(values[1].trim())
                            && player.getEmail().equals(values[2].trim())) {
                        String id = values[0];
                        String[] newData = {
                                id, player.getName(), player.getEmail(), player.getInterestSport(),
                                String.valueOf(player.getSkillLevel()), player.getPreferredRole(),
                                String.valueOf(player.getTotalScore()), player.getPersonalityType()
                        };
                        line = String.join(",", newData);
                    }
                    lines.add(line);
                }
                logger.info(thread+" successfully read file for "+player.getName()+" and updated data.");
            } catch (IOException e) {
                System.err.println("❌ Error reading file for " + player.getName());
                e.printStackTrace();
                return;
            }
        }

        // Write file with synchronization
        synchronized (SurveyProcessorTask.class) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(playerDataFile))) {
                bw.write("ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType");
                bw.newLine();
                for (String l : lines) {
                    bw.write(l);
                    bw.newLine();
                }
                logger.info(thread+" successfully updated file for "+player.getName()+" with new data.");
            } catch (IOException e) {
                System.err.println("❌ Error writing file for " + player.getName());
                e.printStackTrace();
            }
        }
    }
}