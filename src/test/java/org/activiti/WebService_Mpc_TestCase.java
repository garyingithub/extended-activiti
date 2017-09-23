package org.activiti;


import cn.edu.sysu.workflow.cloud.engine.activiti.MyApp;
import org.activiti.engine.*;
import org.activiti.engine.impl.test.PluggableActivitiTestCase;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
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
public class WebService_Mpc_TestCase {

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

//        repositoryService.createDeployment().addClasspathResource("webService/WebService_Mpc.bpmn").deploy();
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createStandaloneInMemProcessEngineConfiguration()
                .buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        repositoryService.createDeployment().addClasspathResource("webService/WebService_Mpc.bpmn").deploy();

        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery().singleResult();
        RuntimeService runtimeService = processEngine.getRuntimeService();


        System.out.println(processDefinition.getKey());

        // 初始化参数
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("nameVar", "mpc_test");
        ProcessInstance pi = runtimeService.startProcessInstanceByKey(
                "process1", vars);
        // 完成第一个任务

        TaskService taskService = processEngine.getTaskService();

        Task task = taskService.createTaskQuery().singleResult();
        System.out.println(task.getId());
        taskService.complete(task.getId());
        // 输出调用Web Service后的参数
//        String add = (String) runtimeService.getVariable(pi.getId(), "addVar");
        System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        System.out.println();
        System.out.println();
//        System.out.println("mpc_test现在居住的地点是—————————————————————————>" + add);
        System.out.println();
        System.out.println();
//        BpmnModel model = repositoryService.getBpmnModel("testUserTasksWithParallel:1:11");
    }

}
