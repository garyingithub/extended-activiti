package org.activiti;

import cn.edu.sysu.workflow.cloud.engine.activiti.MyApp;
import org.activiti.engine.*;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.subethamail.wiser.Wiser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {MyApp.class})
@WebAppConfiguration
@IntegrationTest
public class HelloTest {

//    @Autowired
//    private RuntimeService runtimeService;
//
//    @Autowired
//    private TaskService taskService;
//
//    @Autowired
//    private HistoryService historyService;
//
//    @Autowired
//    private RepositoryService repositoryService;


    private Wiser wiser;

    @Before
    public void setup() {
        wiser = new Wiser();
        wiser.setPort(1025);
        wiser.start();
    }

    @After
    public void cleanup() {
        wiser.stop();
    }

    @Test
    public void testHappyPath() {
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createStandaloneInMemProcessEngineConfiguration()
                .buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
//        repositoryService.createDeployment().addClasspathResource("processes/testUserTasksWithParallel.bpmn20.xml").deploy();
        repositoryService.createDeployment().addClasspathResource("processes/test.bpmn").deploy();

        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery().singleResult();

        RuntimeService runtimeService = processEngine.getRuntimeService();

        System.out.println(processDefinition.getKey());

        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey(processDefinition.getKey());


        TaskService taskService = processEngine.getTaskService();
//        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("sales").list();
        List<Task> tasks = taskService.createTaskQuery().list();
        for (Task task : tasks) {
            System.out.println(task.getId());
            System.out.println("Following task is available for sales group: " + task.getName());
            // 认领任务这里由foozie认领，因为fozzie是sales组的成员
            taskService.claim(task.getId(), "fozzie");
        }
        for (Task task : tasks) {
            System.out.println("Task for fozzie: " + task.getName());
            // 执行(完成)任务
            taskService.complete(task.getId());
        }
//        Task task = taskService.createTaskQuery().singleResult();
//        System.out.println(task.getId());
//        taskService.complete(task.getId());
    }

}
