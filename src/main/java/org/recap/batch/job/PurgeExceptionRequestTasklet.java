package org.recap.batch.job;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.batch.service.PurgeExceptionRequestsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created by rajeshbabuk on 23/3/17.
 */
public class PurgeExceptionRequestTasklet extends JobCommonTasklet implements Tasklet {

    private static final Logger logger = LoggerFactory.getLogger(PurgeExceptionRequestTasklet.class);

    @Autowired
    private PurgeExceptionRequestsService purgeExceptionRequestsService;

    /**
     * This method starts the execution of purging exception requests job.
     *
     * @param contribution StepContribution
     * @param chunkContext ChunkContext
     * @return RepeatStatus
     * @throws Exception Exception Class
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        logger.info("Executing PurgeExceptionRequestTasklet");
        StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext executionContext = jobExecution.getExecutionContext();
        try {
            updateJob(jobExecution, "PurgeExceptionRequestTasklet", Boolean.FALSE);
            Map<String, String> resultMap = purgeExceptionRequestsService.purgeExceptionRequests(scsbCoreUrl);
            String status = resultMap.get(ScsbCommonConstants.STATUS);
            String message = resultMap.get(ScsbCommonConstants.MESSAGE);
            logger.info("Purge Exception Requests status : {}", status);
            logger.info("Purge Exception Requests status message : {}", message);
            executionContext.put(ScsbConstants.JOB_STATUS, status);
            executionContext.put(ScsbConstants.JOB_STATUS_MESSAGE, message);
            stepExecution.setExitStatus(new ExitStatus(status, message));
        } catch (Exception ex) {
            logger.error("{} {}", ScsbCommonConstants.LOG_ERROR, ExceptionUtils.getMessage(ex));
            executionContext.put(ScsbConstants.JOB_STATUS, ScsbConstants.FAILURE);
            executionContext.put(ScsbConstants.JOB_STATUS_MESSAGE, ExceptionUtils.getMessage(ex));
            stepExecution.setExitStatus(new ExitStatus(ScsbConstants.FAILURE, ExceptionUtils.getFullStackTrace(ex)));
        }
        return RepeatStatus.FINISHED;
    }
}
