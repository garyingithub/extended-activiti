package cn.edu.sysu.workflow.cloud.engine.activiti;

import cn.edu.sysu.workflow.cloud.isolation.Scheduler;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;


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

    @RequestMapping(method = RequestMethod.POST, value = "/claimTask2/{processInstanceId}/{taskName}")
    public DeferredResult<String> claimTaskWithName2(@PathVariable String taskName, @PathVariable String processInstanceId) {
        return claimTask(processInstanceId, 2);

    }

    @RequestMapping(method = RequestMethod.POST, value = "/claimTask5/{processInstanceId}/{taskName}")
    public DeferredResult<String> claimTaskWithName5(@PathVariable String taskName, @PathVariable String processInstanceId) {
        return claimTask(processInstanceId, 5);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/claimTask10/{processInstanceId}/{taskName}")
    public DeferredResult<String> claimTaskWithName10(@PathVariable String taskName, @PathVariable String processInstanceId) {
        return claimTask(processInstanceId, 10);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/completeTask2/{processInstanceId}/{taskName}")
    public DeferredResult<String> completeTaskWithName2(@RequestBody(required = false) Map<String, Object> data,
                                                        @PathVariable String processInstanceId, @PathVariable String taskName) {

        DeferredResult<String> result = new DeferredResult<>();
        completeTask(data, processInstanceId, 2, result);
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/completeTask5/{processInstanceId}/{taskName}")
    public DeferredResult<String> completeTaskWithName5(@RequestBody(required = false) Map<String, Object> data,
                                                        @PathVariable String processInstanceId, @PathVariable String taskName) {

        DeferredResult<String> result = new DeferredResult<>();
        completeTask(data, processInstanceId, 5, result);
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/completeTask10/{processInstanceId}/{taskName}")
    public DeferredResult<String> completeTaskWithName10(@RequestBody(required = false) Map<String, Object> data,
                                                         @PathVariable String processInstanceId, @PathVariable String taskName) {

        DeferredResult<String> result = new DeferredResult<>();
        completeTask(data, processInstanceId, 10, result);
        return result;
    }


    private void completeTask(Map<String, Object> data, String processInstanceId, int weight, DeferredResult<String> result) {

        Scheduler.Task task = new Scheduler.Task(weight, result) {
            @Override
            public void run() {

                List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
                if (taskList.size() == 0) {
                    logger.info("Process {} has finish its execution.", processInstanceId);
                    result.setResult(Boolean.FALSE.toString());
                } else {
                    taskList.forEach(task1 -> taskService.complete(task1.getId(), data));

                    result.setResult(Boolean.TRUE.toString());
                }
                MyApp.scheduler.finish();
            }
        };
        MyApp.scheduler.submit(task);
    }

    private Logger logger = LoggerFactory.getLogger(getClass());

    private DeferredResult<String> claimTask(String processInstanceId, int weight) {

        DeferredResult<String> result = new DeferredResult<>();
        Scheduler.Task t = new Scheduler.Task(weight, result) {
            @Override
            public void run() {
                List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
                if (taskList.size() == 0) {
                    throw new RuntimeException("No such Task");
                }
                taskList.forEach(task -> taskService.claim(task.getId(), "admin"));

                result.setResult("");
                MyApp.scheduler.finish();

            }
        };

        MyApp.scheduler.submit(t);
        return result;

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

    private void uploadFile(HttpServletRequest request) throws IOException {

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
