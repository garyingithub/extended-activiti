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
        repositoryService.createDeployment().addClasspathResource("processes/testUserTasksWithParallel.bpmn20.xml").deploy();
//        repositoryService.createDeployment().addClasspathResource("webService/WebService_Calculator.bpmn").deploy();
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery().singleResult();

        RuntimeService runtimeService = processEngine.getRuntimeService();

        System.out.println(processDefinition.getKey());
        // 初始化参数
//        Map<String, Object> vars = new HashMap<String, Object>();
//        vars.put("nameVar", "mpc_test");
//        ProcessInstance pi = runtimeService.startProcessInstanceByKey(
//                "testUserTasksWithParallel");
//        // 完成第一个任务
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().singleResult();
        taskService.complete(task.getId());
        // 输出调用Web Service后的参数
//        String add = (String) runtimeService.getVariable(pi.getId(), "addVar");
//        BpmnModel model = repositoryService.getBpmnModel("testUserTasksWithParallel:1:11");
    }

}
