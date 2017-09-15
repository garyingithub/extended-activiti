package cn.edu.sysu.workflow.cloud.engine.activiti;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.task.Task;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


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
        taskList.forEach(task -> taskService.complete(task.getId(), data));
        return Boolean.TRUE.toString();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/claimTask/{processInstanceId}/{taskName}")
    public void claimTaskWithName(@PathVariable String taskName, @PathVariable String processInstanceId) {
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        if (taskList.size() == 0) {
            throw new RuntimeException("No such Task");
        }
        taskList.forEach(task -> taskService.claim(task.getId(), "admin"));
    }

    @Resource
    private RuntimeService runtimeService;

    @RequestMapping(value = "/startProcess/{definitionId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String startHireProcess(@RequestBody(required = false) Map<String, Object> data, @PathVariable String definitionId) {
        return runtimeService.startProcessInstanceByKey(definitionId, data).getProcessInstanceId();
    }

    @Resource
    private RepositoryService repositoryService;

    @RequestMapping(value = "/getBpmnModel/{processKey}")
    public BpmnModel getBpmnModel(@PathVariable String processKey) {
        return repositoryService.getBpmnModel(processKey);
    }

    void uploadFile(HttpServletRequest request) throws IOException {

        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession()
                .getServletContext());

        if (multipartResolver.isMultipart(request)) {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            multiRequest.getFileMap().forEach((key, value) -> {
                try {
                    repositoryService.createDeployment().name(key)
                            .addInputStream(key, value.getInputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @RequestMapping(value = "/uploadDefinition}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void uploadDefinition(HttpServletRequest request) throws IOException {
       uploadFile(request);
    }

}
