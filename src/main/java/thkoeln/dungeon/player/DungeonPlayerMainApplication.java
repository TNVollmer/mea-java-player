package thkoeln.dungeon.player;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@SpringBootApplication
@EnableAsync
public class DungeonPlayerMainApplication {

    public static void main(String[] args) {

        log.info("Player-Tom");
        log.info("Version: 1.1");

        SpringApplication.run(DungeonPlayerMainApplication.class, args);
    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("AsyncEvent-");
        executor.initialize();
        return executor;
    }
}
