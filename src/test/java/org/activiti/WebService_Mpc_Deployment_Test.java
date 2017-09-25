package org.activiti;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.test.PluggableActivitiTestCase;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WebService_Mpc_Deployment_Test extends AbstractTest {

    @Test
    @Deployment(resources = "webService/WebService_Mpc.bpmn")
    public void test() {
        long count = repositoryService.createProcessDefinitionQuery().count();
        assertEquals(1, count);
        // 初始化参数
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionCategory("process1").singleResult();

        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("nameVar", "mpc_test");
        ProcessInstance pi = runtimeService.startProcessInstanceByKey(
                "process1", vars);
        // 完成第一个任务
//        Task task = taskService.createTaskQuery().singleResult();
//        System.out.println(task.getId());
//        taskService.claim(task.getId(),"yuyong");
//        taskService.complete(task.getId());
//        taskService.complete(task.getId());
//        // 输出调用Web Service后的参数
//        String add = (String) runtimeService.getVariable(pi.getId(), "addVar");
    }

}
