package com.ubitronix.uiem.execution.scheduling;

import java.text.ParseException;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class DlmsQuartzScheduler {

  private static final Log logger = LogFactory.getLog(DlmsQuartzScheduler.class);
  public static final String GROUP_NAME = "TEST_SCHEDULE";
  private static final String CRON_READ_EVENT_LOG_1 = "0/10 * * * * ?";
  private static final String ID_READ_EVENT_LOG_1 = "1";
  private static final String CRON_READ_EVENT_LOG_2 = "0/15 * * * * ?";
  private static final String ID_READ_EVENT_LOG_2 = "2";
  private static final String CRON_READ_LOAD_PROFILE_DATA_1 = "0/20 * * * * ?";
  private static final String ID_READ_LOAD_PROFILE_DATA_1 = "3";

  private Scheduler scheduler;

  public DlmsQuartzScheduler() throws SchedulerException, ParseException, InterruptedException {
    scheduler = new StdSchedulerFactory().getScheduler();
  }

  public void start() throws SchedulerException {
    if (this.scheduler != null) {
      this.scheduler.start();
    }
  }
  
  public void stop() throws SchedulerException {
    if (this.scheduler != null) {
      this.scheduler.shutdown(true);
    }
  }
  
  public void addJob(JobDetail job, CronTrigger trigger) throws SchedulerException {
    this.scheduler.scheduleJob(job, trigger);
  }
  
  public void deleteJob(String jobName, String groupName) throws SchedulerException {
    this.scheduler.deleteJob(jobName, groupName);
  }
  
  public static void main(String[] args) throws SchedulerException, ParseException, InterruptedException {
    DlmsQuartzScheduler scheduler = new DlmsQuartzScheduler();
    
    JobDetail job = new JobDetail(ID_READ_EVENT_LOG_1, GROUP_NAME, ReadEventLogQuartzJob.class);
    job.getJobDataMap().put("TYPE", ReadEventLogQuartzJob.TYPE);
    job.getJobDataMap().put("ID", ID_READ_EVENT_LOG_1);
    CronTrigger trigger = new CronTrigger(ID_READ_EVENT_LOG_1, GROUP_NAME);
    trigger.setCronExpression(CRON_READ_EVENT_LOG_1);
    scheduler.addJob(job, trigger);
    
    job = new JobDetail(ID_READ_EVENT_LOG_2, GROUP_NAME, ReadEventLogQuartzJob.class);
    job.getJobDataMap().put("TYPE", ReadEventLogQuartzJob.TYPE);
    job.getJobDataMap().put("ID", ID_READ_EVENT_LOG_2);
    trigger = new CronTrigger(ID_READ_EVENT_LOG_2, GROUP_NAME);
    trigger.setCronExpression(CRON_READ_EVENT_LOG_2);
    scheduler.addJob(job, trigger);

    job = new JobDetail(ID_READ_LOAD_PROFILE_DATA_1, GROUP_NAME, ReadLoadProfileDataQuartzJob.class);
    job.getJobDataMap().put("TYPE", ReadLoadProfileDataQuartzJob.TYPE);
    job.getJobDataMap().put("ID", ID_READ_LOAD_PROFILE_DATA_1);
    trigger = new CronTrigger(ID_READ_LOAD_PROFILE_DATA_1, GROUP_NAME);
    trigger.setCronExpression(CRON_READ_LOAD_PROFILE_DATA_1);
    scheduler.addJob(job, trigger);
    
    scheduler.start();

    Thread.sleep(40000);
    scheduler.deleteJob(ID_READ_EVENT_LOG_1, GROUP_NAME);
    
    Scanner sc = null;
    try {
      sc = new Scanner(System.in);
      System.out.printf("Enter 'stop' to halt: ");
      while (!sc.nextLine().toLowerCase().equals("stop")) {
      }
    } finally {
      if (sc != null) {
        sc.close();
      }
    }

    scheduler.stop();
  }
}
