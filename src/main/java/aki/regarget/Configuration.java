package aki.regarget;

import aki.regarget.service.Cache;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public Cache getCache() { return new Cache();}
}
