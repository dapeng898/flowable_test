package org.service;

import org.flowable.engine.ProcessEngine;
import org.pojo.entity.BpmTaskEntity;
import org.pojo.form.BpmJumpForm;
import org.pojo.query.BpmTaskQuery;

import javax.xml.ws.ServiceMode;
import java.util.Date;
import java.util.List;

/**
 * 流程中的任务管理
 *
 * @author guzt
 */
public interface BpmUserTaskService {
    /**
     * 【本任务节点】跳转指定的【任务节点】 例如驳回等操作，前跳后跳等操作
     * <p>
     * A（本任务节点）    B（目标任务节点集合）
     * <p>
     * （1）如果B有多个节点
     * 必须为同一个并行网关内的任务节点（网关开始、合并节点必须一致）
     * 必须不是同一条流程线上的任务节点
     * <p>
     * （2）如果A和B为同一条顺序流程线上，则可以直接跳转
     * <p>
     * （3）如果A非并行分支上的任务节点
     * B是为并行网关上节点，需要创建其B所在并行网关内其他任务节点已完成日志
     * <p>
     * （4）如果A是并行分支上的任务节点
     * <p>
     * 4.1 从外向里面跳转（父并网关 》子并网关）
     * B是为并行网关上节点，需要创建其B所在并行网关内其他任务节点已完成日志
     * <p>
     * <p>
     * 4.2 从里向外面跳转 （子并网关 》父并网关 【或】 非并行网关上的节点 【或】 其他非父子关系的并行网关节点）
     * 需要清除本任务节点并行网关上（包括父网关）所有的其他未完成的用户任务
     * B是为并行网关上节点，需要创建其B所在并行网关内其他任务节点已完成日志
     *
     * @param form ignore
     */
    void jump(BpmJumpForm form, ProcessEngine processEngine);
    /**
     * 设置某个任务的超时截止日期
     *
     * @param taskId  任务id
     * @param dueDate 超时截止日期
     */
    void setDueDate(String taskId, Date dueDate);

    /**
     * 查询待处理任务
     *
     * @param query ignore
     * @return ignore
     */
    List<BpmTaskEntity> listTodoTask(BpmTaskQuery query);

    /**
     * 根据flowable 表达式获得表达式的最终值
     *
     * @param taskId     当前任务编号
     * @param expression uel表达式 例如 ${userName1}
     * @return 表达式的值
     */
    Object getExpressionValue(String taskId, String expression);

}
