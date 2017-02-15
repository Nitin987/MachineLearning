package com.td.scheduler;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class HelloJob implements Job{

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// Say Hello to the World and display the date/time
	    System.out.println("Hello World! - " + new Date());
		
	}

}
