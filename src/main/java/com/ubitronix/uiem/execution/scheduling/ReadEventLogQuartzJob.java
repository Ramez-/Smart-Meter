package com.ubitronix.uiem.execution.scheduling;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


public class ReadEventLogQuartzJob implements Job {
  private static final Log logger = LogFactory.getLog(DlmsQuartzScheduler.class);
  public static final String TYPE = "READ_EVENT_LOG";

  
  public void execute(JobExecutionContext context) throws JobExecutionException {
    if (logger.isInfoEnabled()) {
      logger.info("DlmsQuartzScheduler triggered at " +  context.getFireTime());
      logger.info("Type = " + context.getJobDetail().getJobDataMap().getString("TYPE"));
      logger.info("Id = " + context.getJobDetail().getJobDataMap().getString("ID"));
    }
  }
}
