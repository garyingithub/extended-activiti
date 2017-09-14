package cn.edu.sysu.workflow.cloud.engine.activiti;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.rest.service.api.repository.DeploymentResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;



@RestController
@RequestMapping(value = "/extended")
public class ExtendedController {
    @Resource
    private TaskService taskService;

    @RequestMapping(method = RequestMethod.POST, value = "/completeTask/{processInstanceId}/{taskName}")
    public String completeTaskWithName(@RequestBody(required = false) Map<String, Object> data, @PathVariable String processInstanceId, @PathVariable String taskName) {
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        if (taskList.size() == 0) {
            return Boolean.FALSE.toString();
        }
        taskList.forEach(task -> {
            taskService.complete(task.getId(), data);
        });
        return Boolean.TRUE.toString();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/claimTask/{processInstanceId}/{taskName}")
    public void claimTaskWithName(@PathVariable String taskName, @PathVariable String processInstanceId) {
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        if (taskList.size() == 0) {
            throw new RuntimeException("No such Task");
        }
        taskList.forEach(task -> {
            taskService.claim(task.getId(), "admin");
        });
    }

    @Resource
    private RuntimeService runtimeService;


    Random random = new Random();

    @RequestMapping(value="/startProcess/{definitionId}", method= RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public String startHireProcess(@RequestBody(required = false) Map<String, Object> data, @PathVariable String definitionId) {

//        Applicant applicant = new Applicant(data.get("name"), data.get("email"), data.get("phoneNumber"));
//        applicantRepository.save(applicant);

//        Map<String, Object> variables = new HashMap<String, Object>();
//        variables.put("applicant", applicant);
        return runtimeService.startProcessInstanceByKey(definitionId, data).getProcessInstanceId();
    }

    @Resource
    private RepositoryService repositoryService;

    @RequestMapping(value = "/getBpmnModel/{processKey}")
    public BpmnModel getBpmnModel(@PathVariable String processKey) {
        return repositoryService.getBpmnModel(processKey);
    }
}
