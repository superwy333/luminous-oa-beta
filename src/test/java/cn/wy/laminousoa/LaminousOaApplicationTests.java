package cn.wy.laminousoa;

import org.activiti.engine.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LaminousOaApplicationTests {

    @Autowired
    private ApplicationContext ioc;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private HistoryService historyService;

//    API文档
//    https://www.activiti.org/userguide/#api.services.start.processinstance

    /**
     * 部署流程
     */
    @Test
    public void deploy() {
        processEngine.getRepositoryService()
                .createDeployment()
                .addClasspathResource("processes/qjdemo2.bpmn")
                .deploy();
    }

    /**
     * 删除已经部署的流程
     */
    @Test
    public void delete() {
        processEngine.getRepositoryService()
                .deleteDeployment("60001",true);
    }

    /**
     * 启动流程
     */
    @Test
    public void start() {
        System.out.println("【Before】Number of process instances: " + runtimeService.createProcessInstanceQuery().count());
        processEngine.getRuntimeService()
                .startProcessInstanceByKey("myProcess_1"); // 流程实例id传act_re_procdef.KEY
        System.out.println("【After】Number of process instances: " + runtimeService.createProcessInstanceQuery().count());
    }

    /**
     * 完成任务
     */
    @Test
    public void completeTask() {
        processEngine.getTaskService().complete("1");

    }

    /**
     * 完成任务
     * 带流程变量
     */
    @Test
    public void completeTask2() {
        String post = "cwkzy"; // 财务科职员
        String assignee = "";
        Map<String,Object> variables = new HashMap<>();
        variables.put("post","zy");
        variables.put("days",18);
        if ("cwkzy".equals(post)) assignee = "财务科科长";
        if ("xxkzy".equals(post)) assignee = "信息科科长";
        variables.put("assignee",assignee);
        processEngine.getTaskService().complete("70005",variables);

    }



    @Test
    public void contextLoads() {
        System.out.println(ioc.containsBean("taskService"));
        System.out.println(taskService);

    }

}
