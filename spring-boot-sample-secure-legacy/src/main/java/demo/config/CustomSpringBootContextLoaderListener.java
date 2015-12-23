package demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.BackgroundPreinitializer;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.legacy.context.web.SpringBootContextLoaderListener;
import org.springframework.context.ApplicationListener;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;

import javax.validation.Validation;
import java.util.Iterator;
import java.util.Set;


public class CustomSpringBootContextLoaderListener extends SpringBootContextLoaderListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomSpringBootContextLoaderListener.class);


    @Override
    protected void configureSpringApplicationBuilder(SpringApplicationBuilder builder) {
        // Configure your app here, e.g. activate a profile with
        // builder.profiles(applicationEnvironment());
        removeBackgroundPreinitializerIfOnGae(builder);
    }


    private void removeBackgroundPreinitializerIfOnGae(SpringApplicationBuilder builder) {
        String appEngineEnv = System.getProperty("com.google.appengine.runtime.environment");
        if ("Production".equals(appEngineEnv))  {
            Set<ApplicationListener<?>> listeners = builder.application().getListeners();
            ApplicationListener<?> listener = null;
            for (Iterator<ApplicationListener<?>> iterator = listeners.iterator(); iterator.hasNext(); ) {
                listener = iterator.next();
                if (listener instanceof BackgroundPreinitializer) {
                    break;
                }
            }
            if (listener instanceof BackgroundPreinitializer) {
                listeners.remove(listener);
                listeners.add(new ForegroundPreinitializer());
                builder.application().setListeners(listeners);
            }
        }
    }

    public static class ForegroundPreinitializer implements ApplicationListener<ApplicationStartedEvent> {
        // These are the initializers that are contained in the Spring Boot 1.3 BackgroundPreinitializer
        // We will run them in the Foreground

        @Override
        public void onApplicationEvent(ApplicationStartedEvent event) {
            new AllEncompassingFormHttpMessageConverter();
            // This does not run, because JMX does not work on GAE, so just removed for now
//            new MBeanFactory();
            Validation.byDefaultProvider().configure();
        }
    }
}


