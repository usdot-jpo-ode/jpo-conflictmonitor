/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.deduplication;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
@EnableConfigurationProperties(DeduplicationProperties.class)
public class DeduplicationApplication {

   private static final Logger logger = LoggerFactory.getLogger(DeduplicationApplication.class);

   static final int DEFAULT_NO_THREADS = 10;
   static final String DEFAULT_SCHEMA = "default";

   public static void main(String[] args) throws MalformedObjectNameException, InterruptedException,
         InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {


      SpringApplication.run(DeduplicationApplication.class, args);
      // MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
      // SystemConfig mBean = new SystemConfig(DEFAULT_NO_THREADS, DEFAULT_SCHEMA);
      // ObjectName name = new ObjectName("us.dot.its.jpo.geojson:type=SystemConfig");
      // mbs.registerMBean(mBean, name);
      logger.info("Message Deduplication has started");

   }

   @Bean
   CommandLineRunner init(DeduplicationProperties geojsonProperties) {
      return args -> {
      };
   }
}
