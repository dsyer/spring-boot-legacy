package demo.config;
import org.slf4j.*;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class Config
{
    private static final Logger log = LoggerFactory.getLogger(Config.class);

    @PostConstruct
    public void init(){
        log.info("info statement");
        log.debug("debug statement");
    }
}
