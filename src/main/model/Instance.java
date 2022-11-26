package main.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant une instance du problème
 * @author Anas Neumann <anas.neumann.1@ulaval.ca>
 * @since 07/03/2019
 * @version 1.0
 */
public class Instance {
	private List<Job> jobs = new ArrayList<Job>();

	/**
	 * @return the jobs
	 */
	public List<Job> getJobs() {
		return jobs;
	}

	/**
	 * @param jobs the jobs to set
	 */
	public Instance setJobs(List<Job> jobs) {
		this.jobs = jobs;
		return this;
	}
}
