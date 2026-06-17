package com.mes.admin.config;

import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.local.LocalConverter;
import org.jodconverter.local.office.LocalOfficeManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "jodconverter", name = "enabled", havingValue = "true", matchIfMissing = false)
public class OpenOfficeConfig {

    @Value("${jodconverter.office-home:/opt/openoffice.org3}")
    private String officeHome;

    @Value("${jodconverter.max-tasks-per-process:200}")
    private int maxTasksPerProcess;

    @Value("${jodconverter.task-execution-timeout:60000}")
    private long taskExecutionTimeout;

    @Value("${jodconverter.task-queue-timeout:120000}")
    private long taskQueueTimeout;

    @Value("${jodconverter.port-numbers:8100,8101}")
    private String portNumbers;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public LocalOfficeManager localOfficeManager() {
        String[] portArr = portNumbers.split(",");
        int[] ports = new int[portArr.length];
        for (int i = 0; i < portArr.length; i++) {
            ports[i] = Integer.parseInt(portArr[i].trim());
        }
        LocalOfficeManager.Builder builder = LocalOfficeManager.builder();
        builder.officeHome(officeHome);
        builder.portNumbers(ports);
        builder.maxTasksPerProcess(maxTasksPerProcess);
        builder.taskExecutionTimeout(taskExecutionTimeout);
        builder.taskQueueTimeout(taskQueueTimeout);
        return builder.build();
    }

    @Bean
    public DocumentConverter documentConverter(LocalOfficeManager localOfficeManager) throws OfficeException {
        return LocalConverter.make(localOfficeManager);
    }
}
