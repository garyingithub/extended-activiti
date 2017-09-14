package org.activiti;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import java.util.function.Consumer;

@SpringBootApplication
public class MyApp {

    public static void main(String[] args) {
        SpringApplication.run(MyApp.class, args);
    }

    private static Class<MyApp> applicationClass = MyApp.class;

    @Bean
    InitializingBean usersAndGroupsInitializer(final IdentityService identityService, final RepositoryService repositoryService) {

        return new InitializingBean() {
            public void afterPropertiesSet() throws Exception {

                Group group = identityService.newGroup("user");
                group.setName("users");
                group.setType("security-role");
                identityService.saveGroup(group);

                User admin = identityService.newUser("admin");
                admin.setPassword("admin");
                identityService.saveUser(admin);

                Group testGroup = identityService.newGroup("testUser");
                testGroup.setName("testUser");
//                testGroup.setType("security-role");
                identityService.saveGroup(group);

                User testUser0 = identityService.newUser("testUser0");
                testUser0.setPassword("testUser0");
                identityService.saveUser(testUser0);

                ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
//                query.processDefinitionKey("testUserTasks").list().parallelStream().forEach(processDefinition -> repositoryService.deleteDeployment(processDefinition.getDeploymentId()));


//                ProcessDefinitionQuery withParallerlQuery = repositoryService.createProcessDefinitionQuery();
//                withParallerlQuery.processDefinitionKey("testUserTasksWithParallel").list().parallelStream().forEach(processDefinition -> repositoryService.deleteDeployment(processDefinition.getDeploymentId()));
//                repositoryService.createDeployment().name("testUserTasksWithParallel").addClasspathResource("processes/testUserTasksWithParallel.bpmn20.xml").deploy();


//                repositoryService.createDeployment().name("testUserTasks").addClasspathResource("processes/testUserTasks.bpmn20.xml").deploy();


            }
        };
    }

}
