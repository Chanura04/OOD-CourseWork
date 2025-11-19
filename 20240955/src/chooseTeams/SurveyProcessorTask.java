package chooseTeams;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class SurveyProcessorTask implements Runnable {
    private final Participant player;
    private final CountDownLatch latch;
    private final String filePath;

    public SurveyProcessorTask(Participant player, String filePath, CountDownLatch latch) {
        this.player = player;
        this.filePath = filePath;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            processSurvey();
        } finally {
            if (latch != null) {
                latch.countDown();
            }
        }
    }

    private void processSurvey() {
        List<String> lines = new ArrayList<>();
        File playerDataFile = new File(filePath);

        if (!player.checkPersonalityType()) {
            System.out.println("⚠️ " + player.getName() + "'s Personality Score is very low! Please try again!\n");
            System.out.println("⚠️ Survey for " + player.getName() + " has been discarded.\n");
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

            } catch (IOException e) {
                System.err.println("❌ Error writing file for " + player.getName());
                e.printStackTrace();
            }
        }
    }
}