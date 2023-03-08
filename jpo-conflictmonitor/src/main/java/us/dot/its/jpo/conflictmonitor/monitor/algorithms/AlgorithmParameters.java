package us.dot.its.jpo.conflictmonitor.monitor.algorithms;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;


import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigDataClass;


/**
 * Component for collecting Parameter Objects for Algorithms with class-level 
 * {@link ConfigDataClass} annotations.
 * 
 */
@Component
public class AlgorithmParameters {


    final GenericApplicationContext appContext;

    @Autowired
    public AlgorithmParameters(GenericApplicationContext appContext) {
        this.appContext = appContext;
    }

    /**
     * @return List of parameters objects annotated with {@link ConfigDataClass}
     */
    public List<Object> listParameterObjects() {
        var paramObjects = new ArrayList<Object>();
        var annotatedBeans = appContext.getBeansWithAnnotation(ConfigDataClass.class);
        for (var beanName : annotatedBeans.keySet()) {
            paramObjects.add(appContext.getBean(beanName));
        }
        return paramObjects;
    }

}
