package io.metersphere.track.service;

import io.metersphere.base.domain.TestPlanExecutionQueue;
import io.metersphere.base.mapper.ext.ExtTestPlanExecutionQueueMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;

@Service
public class TestPlanExecutionQueueService {

    @Resource
    private ExtTestPlanExecutionQueueMapper extTestPlanExecutionQueueMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void batchSave(List<TestPlanExecutionQueue> planExecutionQueues) {
        if (!planExecutionQueues.isEmpty()) {
            extTestPlanExecutionQueueMapper.sqlInsert(planExecutionQueues);
        }
    }
}
