package aki.regarget;

import aki.regarget.service.Core;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StartupInit implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    Core core;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        try {
            core.loadStoreCatalog();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
