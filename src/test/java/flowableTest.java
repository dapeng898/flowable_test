import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Before;
import org.junit.Test;
import org.pojo.form.BpmJumpForm;
import org.springframework.cache.annotation.Cacheable;

import java.util.*;

public class flowableTest {

    public static void main(String[] args) {
        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
                .setJdbcUrl("jdbc:mysql://localhost:3306/flowable?serverTimezone=UTC&nullCatalogMeansCurrent=true")
                .setJdbcUsername("root")
                .setJdbcPassword("root")
                .setJdbcDriver("com.mysql.cj.jdbc.Driver")
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        ProcessEngine processEngine = cfg.buildProcessEngine();
    }

    ProcessEngineConfiguration cfg = null;

    @Before
    public void before(){
        // 配置数据库相关信息 获取 ProcessEngineConfiguration
        cfg = new StandaloneProcessEngineConfiguration().setJdbcUrl("jdbc:mysql://localhost:3306/flowable?serverTimezone=UTC&nullCatalogMeansCurrent=true")
                .setJdbcUsername("root")
                .setJdbcPassword("root")
                .setJdbcDriver("com.mysql.cj.jdbc.Driver")
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
    }
    /**
     * 部署流程
     */
    @Test
    public void testDeploy(){
        ProcessEngine processEngine = cfg.buildProcessEngine();
        // 部署流程 获取RepositoryService对象
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()// 创建Deployment对象
                .addClasspathResource("static/binxinhuiqian_3.bpmn") // 添加流程部署文件
                .name("并行流程3") // 设置部署流程的名称
                .deploy(); // 执行部署操作
        System.out.println("deployment.getId() = " + deployment.getId());
        System.out.println("deployment.getName() = " + deployment.getName());
    }
    /**
     * 查看流程定义
     */
    @Test
    public void testDeployQuery(){
        ProcessEngine processEngine = cfg.buildProcessEngine();
        // 部署流程 获取RepositoryService对象
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 获取流程定义对象
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId("382501")
                .singleResult();
        System.out.println("processDefinition.getId() = " + processDefinition.getId());
        System.out.println("processDefinition.getName() = " + processDefinition.getName());
        System.out.println("processDefinition.getDeploymentId() = " + processDefinition.getDeploymentId());
        System.out.println("processDefinition.getDescription() = " + processDefinition.getDescription());
    }

    /**
     * 启动流程实例
     */
    @Test
    @Cacheable(sync = true,key = "#id", condition = "")
    public void testRunProcess(String id){
        // 获取流程引擎对象
        ProcessEngine processEngine = cfg.buildProcessEngine();
        // 启动流程实例通过 RuntimeService 对象
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 构建流程变量
        Map<String,Object> variables = new HashMap<>();
        variables.put("assigneeList",Arrays.asList("admin", "qiudp", "testuser")) ;// 会签人员
        variables.put("amount",3000); // 金额
        variables.put("description","买台式机"); // 原因
        // 启动流程实例，第一个参数是流程定义的id
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("binxinhuiqian_2", variables);// 启动流程实例
        // 输出相关的流程实例信息
        System.out.println("流程定义的ID：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例的ID：" + processInstance.getId());
        System.out.println("当前活动的ID：" + processInstance.getActivityId());
    }

    /**
     * 查看任务
     */
    @Test
    public void testQueryTask(){
        // 获取流程引擎对象
        ProcessEngine processEngine = cfg.buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("parallel")
//                .taskAssignee("lisi")
                .list();
        for (Task task : list) {
            System.out.println("task.getProcessDefinitionId() = " + task.getProcessDefinitionId());
            System.out.println("task.getId() = " + task.getId());
            System.out.println("task.getAssignee() = " + task.getAssignee());
            System.out.println("task.getName() = " + task.getName());
        }
    }
    /**
     * 完成任务
     */
    @Test
    public void testCompleteTask(){
        // 获取流程引擎对象
        ProcessEngine processEngine = cfg.buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
//        Task task = taskService.createTaskQuery()
//                .processDefinitionKey("parallel")
//                .taskAssignee("lisi")
//                .singleResult();
        // 添加流程变量
        Map<String,Object> variables = new HashMap<>();
        variables.put("assigneeList",Arrays.asList("admin", "qiudp", "testuser")) ;// 会签人员
        // 完成任务
        taskService.complete("712512",variables);
    }

    /**
     * 删除流程
     */
    @Test
    public void testDeleteProcess(){
        // 获取流程引擎对象
        ProcessEngine processEngine = cfg.buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 删除流程定义，如果该流程定义已经有了流程实例启动则删除时报错
        // repositoryService.deleteDeployment("1");
        // 设置为TRUE 级联删除流程定义，及时流程有实例启动，也可以删除，设置为false 非级联删除操作。
        repositoryService.deleteDeployment("585001",true);

    }

    /**
     * 查看历史
     */
    @Test
    public void testQueryHistory(){
        // 获取流程引擎对象
        ProcessEngine processEngine = cfg.buildProcessEngine();
        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
                .processDefinitionId("holidayRequest:1:10003")
                .finished()
                .orderByHistoricActivityInstanceEndTime().asc()
                .list();
        for (HistoricActivityInstance historicActivityInstance : list) {
            System.out.println(historicActivityInstance.getActivityId() + " took "
                    + historicActivityInstance.getDurationInMillis() + " milliseconds");
        }
    }

    /**
     * 普通串行路线上的退回（此流程中没有并行网关的退回时），此方法支持普通串行节点/会签多实例节点/排他网关节点
     */
    @Test
    public void moveActivityIdsToSingleActivityId(){
        ProcessEngine processEngine = cfg.buildProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        List<String> list = new ArrayList<>();
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId("")
                // 多实例会签退回
//                .moveActivityIdsToSingleActivityId(list, "")
                // 单实例退回
                .moveActivityIdTo("","")
                .changeState();
    }
    /**
     *  子流程中退回到主干流程中某一个节点/主干流程退回到子流程中某一个节点
     */
    public void backProcess(){
        ProcessEngine processEngine = cfg.buildProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        runtimeService.createChangeActivityStateBuilder().moveActivityIdToParentActivityId("", "");

        runtimeService.createChangeActivityStateBuilder().moveActivityIdToSubProcessInstanceActivityId("","","");

        runtimeService.createChangeActivityStateBuilder().moveActivityIdToSubProcessInstanceActivityId("","","");
    }


    @Test
    public void jumpTest(){
        BpmJumpForm bpmJumpForm =new BpmJumpForm();
//        bpmJumpForm.setTaskId("680007");
//        bpmJumpForm.setTargetTaskDefineKes(Arrays.asList("father"));
        ProcessEngine processEngine = cfg.buildProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        runtimeService.createChangeActivityStateBuilder().processInstanceId("702501").moveExecutionsToSingleActivityId(Arrays.asList("707503","707505"),"father").changeState();
//        FlowableBpmUserTaskServiceImpl bpmUserTaskService =new FlowableBpmUserTaskServiceImpl();
//        bpmUserTaskService.jump(bpmJumpForm,processEngine);
    }
    /**
     * 退回到并行网关分支之中的某一个节点上
     */
    @Test
    public void moveSingleActivityIdToActivityIds(){
        ProcessEngine processEngine = cfg.buildProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        List<String> targetTaskKeys = new ArrayList<>();
        targetTaskKeys.add("sid-D9B5A91E-23D4-4318-9866-115DFF9F60D1");
//        targetTaskKeys.add("sid-D9B5A91E-23D4-4318-9866-115DFF9F60D1");
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId("")
                .moveSingleExecutionToActivityIds("137501", targetTaskKeys)
                .changeState();
    }

    /**
     * 并行网关中的某一个分支节点上发起退回，退回到并行网关前面的某一个节点上
     */
    @Test
    public void moveExecutionsToSingleActivityId(){
        // 并行网关的退回
        ProcessEngine processEngine = cfg.buildProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
//        List<String> currentExecutionIds = new ArrayList<>();
        // 取出所有子节点(无论是否完成)
//        List<Execution> executions = runtimeService.createExecutionQuery().parentId("40001").list();
//        for (Execution execution : executions) {
////            System.out.println("并行网关节点数："+execution.getActivityId());
////            currentExecutionIds.add(execution.getId());
////        }
//        currentExecutionIds.add("140006");
//        currentExecutionIds.add("50002");
//        List<String> activityIds =  new ArrayList<>();
//        activityIds.add("sid-D9B5A91E-23D4-4318-9866-115DFF9F60D1");
        runtimeService.createChangeActivityStateBuilder()
//                .moveExecutionsToSingleActivityId(currentExecutionIds, "sid-E2172DE5-D1F3-4C58-86C4-CD60A90B96EA")
                .processInstanceId("162501")
                .moveActivityIdToParentActivityId("sid-D9B5A91E-23D4-4318-9866-115DFF9F60D1", "sid-E2172DE5-D1F3-4C58-86C4-CD60A90B96EA")
                .changeState();
    }
    // 主线两个并行
    //1.并行其中一个节点往回另一个并行其中一个节点驳回 使用runtimeService.processInstanceId.moveExecutionsToSingleActivityId方法且需要添加被驳回并行网关其它分支的数据（添加至execution表，状态均为已完成）

    // 主线一个并行，其中一个分支有并行
    // 1.主线节点往子并行其中一个节点驳回 使用runtimeService.processInstanceId.moveExecutionToActivityId方法且需要添加子流程其它分支已完成的数据
    // 1.子并行其中一个节点驳回到主流程
}

