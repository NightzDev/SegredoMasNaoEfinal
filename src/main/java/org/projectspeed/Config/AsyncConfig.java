package org.projectspeed.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Configuration
public class AsyncConfig {

    /**
     * Configura e retorna um ThreadPoolTaskExecutor.
     *
     * @return o ThreadPoolTaskExecutor configurado
     */
    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        int idealThreadPoolSize = numIdealThreadPoolSizeAutomatic();

        executor.setKeepAliveSeconds(120);
        executor.setCorePoolSize(idealThreadPoolSize);
        executor.setMaxPoolSize(idealThreadPoolSize);
        executor.setThreadNamePrefix("poolThread-");
        executor.initialize();

        return executor;
    }


    public static int numIdealThreadPoolSizeAutomatic() {
        int numOfCores = Runtime.getRuntime().availableProcessors();
        double waitTime = 100;
        double ServiceTime = 10;

        return (int) (numOfCores * (1 + (waitTime / ServiceTime))/2);
    }

    private int numIdealThreadPoolSizeManual(int NumberOfCores) {
        return NumberOfCores * (1 + 100 / 10);
    }


}
