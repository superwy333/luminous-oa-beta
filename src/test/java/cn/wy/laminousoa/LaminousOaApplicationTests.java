package cn.wy.laminousoa;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

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
                .addClasspathResource("processes/test-process-01.bpmn20.xml")
                .deploy();
    }

    @Test
    public void deployWithZip() {
        InputStream in=this.getClass().getClassLoader().getSystemResourceAsStream("processes/qjlc.zip");
        ZipInputStream zipInputStream=new ZipInputStream(in);
        Deployment deployment=processEngine.getRepositoryService()
                .createDeployment().addZipInputStream(zipInputStream)
                .name("请假流程WithZip")
                .deploy();
        System.out.println("流程部署ID:"+deployment.getId());
        System.out.println("流程部署Name:"+deployment.getName());

    }



    /**
     * 删除已经部署的流程
     */
    @Test
    public void delete() {
        String[] ids = {"277501","277531","302501"};
        for (int i =0;i<ids.length;i++) {
            processEngine.getRepositoryService()
                    .deleteDeployment(ids[i],true);
        }


    }

    /**
     * 启动流程
     */
    @Test
    public void start() {
        System.out.println("【Before】Number of process instances: " + runtimeService.createProcessInstanceQuery().count());
        Map<String,Object> variables = new HashMap<>();
        variables.put("assignee","aaa");
        processEngine.getRuntimeService()
                .startProcessInstanceByKey("test-process-01",variables); // 流程实例id传act_re_procdef.KEY
        System.out.println("【After】Number of process instances: " + runtimeService.createProcessInstanceQuery().count());
    }

    /**
     * 完成任务
     */
    @Test
    public void completeTask() {
        processEngine.getTaskService().complete("85007");

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
    public void viewPic() throws IOException {
        /**将生成图片放到文件夹下*/
        String deploymentId = "77501";
        //获取图片资源名称
        List<String> list = processEngine.getRepositoryService()//
                .getDeploymentResourceNames(deploymentId);
        //定义图片资源的名称
        String resourceName = "";
        if(list!=null && list.size()>0){
            for(String name:list){
                if(name.indexOf(".png")>=0){
                    resourceName = name;
                }
            }
        }


        //获取图片的输入流
        InputStream in = processEngine.getRepositoryService()//
                .getResourceAsStream(deploymentId, resourceName);

        //将图片生成到D盘的目录下
        File file = new File("D:/"+resourceName);
        //将输入流的图片写到D盘下
        FileUtils.copyInputStreamToFile(in, file);
    }






    @Test
    public void contextLoads() {
        System.out.println(ioc.containsBean("taskService"));
        System.out.println(taskService);

    }



}
