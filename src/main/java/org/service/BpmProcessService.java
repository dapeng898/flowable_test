package org.service;

import org.flowable.engine.ProcessEngine;
import org.pojo.dto.UserTaskModelDTO;
import org.pojo.query.BpmTaskModelQuery;

/**
 * 流程管理
 *
 * @author guzt
 */
public interface BpmProcessService {

    /**
     * 查询流程所有的用户任务节点信息，分并行网关节点和非并行网关节点
     *
     * @param query 查询参数
     * @return List BpmTaskModelEntity
     */
    UserTaskModelDTO getUserTaskModelDto(BpmTaskModelQuery query, ProcessEngine processEngine);
}
