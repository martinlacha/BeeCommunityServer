package cz.zcu.kiv.server.beecommunity.schedule;

import cz.zcu.kiv.server.beecommunity.config.PropertiesConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.io.IOException;

import static org.mockito.Mockito.*;

class DatabaseBackupScheduleTest {

    @Mock
    private PropertiesConfiguration propertiesConfiguration;

    @InjectMocks
    private DatabaseBackupSchedule databaseBackupSchedule;

    @Mock
    private Logger log;

    @Mock
    private Process process;

    @Mock
    private ProcessBuilder processBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testScheduleDatabaseBackup_SuccessfulBackup() throws IOException {
        // Mock the propertiesConfiguration
        when(propertiesConfiguration.getDatabaseUser()).thenReturn("testUser");
        when(propertiesConfiguration.getDatabaseName()).thenReturn("testDb");

        when(processBuilder.start()).thenReturn(process);
        // Call the method under test
        databaseBackupSchedule.scheduleDatabaseBackup();
        // Verify the log statements
        verify(propertiesConfiguration).getDatabaseName();
    }
}
