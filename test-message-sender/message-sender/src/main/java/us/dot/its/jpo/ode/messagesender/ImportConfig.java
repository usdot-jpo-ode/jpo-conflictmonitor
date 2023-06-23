package us.dot.its.jpo.ode.messagesender;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {us.dot.its.jpo.ode.messagesender.scriptrunner.ScriptRunnerConfig.class})
public class ImportConfig {
    
}
