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

import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {MyApp.class})
@WebAppConfiguration
@IntegrationTest
public class webService_Calculator_TestCase {

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

//        repositoryService.createDeployment().addClasspathResource("webService/WebService_Calculator.bpmn").deploy();

        ProcessEngine processEngine = ProcessEngineConfiguration
                .createStandaloneInMemProcessEngineConfiguration()
                .buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        repositoryService.createDeployment().addClasspathResource("webService/WebService_Calculator.bpmn").deploy();

        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery().singleResult();
        RuntimeService runtimeService = processEngine.getRuntimeService();


        System.out.println(processDefinition.getKey());
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("input1", 2);
        map.put("input2", 3);
//        ProcessInstance pi=runtimeService.startProcessInstanceByKey("process1", map);
        ProcessInstance pi=runtimeService.startProcessInstanceById(processDefinition.getId(), map);

        System.out.println(pi.getId());


//        TaskService taskService = processEngine.getTaskService();
//        FormService formService = processEngine.getFormService();
////        获取第一个任务
//        List<Task> tasks = taskService.createTaskQuery().list();
//        for (Task task : tasks) {
//            System.out.println(task.getId());
//            System.out.println("Following task is available for sales group: " + task.getName());
//            // 认领任务这里由foozie认领，因为fozzie是sales组的成员
//            taskService.claim(task.getId(), "fozzie");
//        }
//        for (Task task : tasks) {
//            System.out.println("Task for fozzie: " + task.getName());
//            // 执行(完成)任务
//            taskService.complete(task.getId());
//        }

        //获取第二个任务
//        tasks = taskService.createTaskQuery().list();
//        for (Task task : tasks) {
//            System.out.println(task.getId());
//            System.out.println("Following task is available" + task.getName());
//            // 认领任务这里由foozie认领，因为fozzie是sales组的成员
//            taskService.claim(task.getId(), "fozzie");
//
//        }
//        for (Task task : tasks) {
//            System.out.println("Task Name " + task.getName());
//            // 执行(完成)任务
//            taskService.complete(task.getId());
//        }

        //获取第一个任务
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().singleResult();
        System.out.println(task.getName());
        taskService.claim(task.getId(), "yuyong");
        taskService.complete(task.getId());
        System.out.println(task.getName());
//
//        //获取第二个任务
//        task = taskService.createTaskQuery().singleResult();
//        System.out.println(task.getId());
//        taskService.claim(task.getId(), "yuyong");
//        taskService.complete(task.getId());
//        System.out.println(task.getName());

        int output = (Integer) runtimeService.getVariable(pi.getId(), "output3");
        System.out.println(output);
        //获取第二个任务
        task = taskService.createTaskQuery().singleResult();
        System.out.println(task.getName());
        taskService.claim(task.getId(), "yuyong");
        taskService.complete(task.getId());
        System.out.println(task.getName());

    }

}
