package cz.zcu.kiv.server.beecommunity.schedule;

import cz.zcu.kiv.server.beecommunity.config.PropertiesConfiguration;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;

/**
 * Class for backup docker database
 */

@Slf4j
@Component
@EnableAsync
@AllArgsConstructor
public class DatabaseBackupSchedule {

    private final PropertiesConfiguration propertiesConfiguration;

    // Container name with running Postgres database
    private static final String databaseContainerName = "beecommunity_postgres_1";

    /**
     * Async schedule task for backup database every day at 23:00 (11:00 PM) and save backup file into folder
     */
    @Async
    @Scheduled(zone = "Europe/Paris", cron = "0 0 23 * * ?")
    public void scheduleDatabaseBackup() {
        String backupName = String.format("db-backup-%s.bak", LocalDate.now());
        String outputFile = String.format("db_backup/%s", backupName);

        // Run backup command on docker container with database
        var successExitCode = runSingleCommand(
                "docker",
                "exec",
                databaseContainerName,
                "sh",
                "-c",
                String.format("pg_dump -U %s %s > %s",
                        propertiesConfiguration.getDatabaseUser(), propertiesConfiguration.getDatabaseName(), backupName));
        // Exit code check
        if (!successExitCode) {
            log.error("Unable to backup database");
        } else {
            // Copy created backup file to local folder with backup files
            successExitCode = runSingleCommand(
                    "docker",
                    "cp",
                    String.format("%s:%s", databaseContainerName, backupName),
                    outputFile
            );
            // Check return code of copy command
            if (!successExitCode) {
                log.error("Unable to move backup {} from container {}", backupName, databaseContainerName);

            } else  {
                log.info("Database backup complete successfully");
            }
        }
    }

    /**
     * Run single command and check error output and log them
     * @param commands list of commands to run
     * @return true - command run successfully, false - command failed
     */
     boolean runSingleCommand(String... commands) {
        boolean commandSuccessful = true;
        Process process;
        var pb = new ProcessBuilder(
                commands
        );
        // Try to run command and read errors from stream if exist
        try {
            process = pb.start();
            final BufferedReader r = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));
            String line = r.readLine();
            while (line != null) {
                commandSuccessful = false;
                log.error(line);
                line = r.readLine();
            }
            r.close();
            process.waitFor();
            log.info("Database backup exit code: {}", process.exitValue());
        } catch (IOException | InterruptedException e) {
            log.error("Unable to backup database: {}", e.getMessage());
            commandSuccessful = false;
        }
        return commandSuccessful;
    }

}
