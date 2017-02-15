package com.td.scheduler;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.td.jdom.XMLExtractionJob;

public class QuartzXMLJobScheduler {

	public QuartzXMLJobScheduler(String dtdPath) {
		 try {
	            // Grab the Scheduler instance from the Factory
	            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

	            // and start it off
	            scheduler.start();
	            
	            JobDetail job = JobBuilder.newJob(XMLExtractionJob.class).withIdentity("XMLExtractionJob", "TD").usingJobData("DTD_PATH", dtdPath).build();
//	            job.getJobDataMap().put(XMLExtractionJob.DTD_PATH, dtdPath);
	            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("simpleTrigger", "group1").withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(5).repeatForever()).build();
//	            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("cronTrigger", "group1").withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 ? * *")).build();

	            	// Tell quartz to schedule the job using our trigger
	            System.out.println("execution started from quartz--->");
	            scheduler.scheduleJob(job, trigger);

//	            scheduler.shutdown(true);

	        } catch (SchedulerException se) {
	            se.printStackTrace();
	        }
	}
	
    public static void main(String[] args) {
    	new QuartzXMLJobScheduler("dtd");
       
    }
}
