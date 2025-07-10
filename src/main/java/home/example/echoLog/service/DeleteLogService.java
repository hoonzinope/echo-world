package home.example.echoLog.service;

import home.example.echoLog.mapper.EchoLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class DeleteLogService {

    private final EchoLogMapper echoLogMapper;
    public DeleteLogService(EchoLogMapper echoLogMapper) {
        this.echoLogMapper = echoLogMapper;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void purgeLogs() {
        try {
            echoLogMapper.deleteLogByDate();
        } catch (Exception e) {
            // Handle the exception, log it, or rethrow it as needed
            log.error("Error occurred while deleting logs: ", e);
        }
    }
}
