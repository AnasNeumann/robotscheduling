package main.solution;

import java.util.ArrayList;
import java.util.List;

import main.model.Instance;
import main.model.Job;

/**
 * Classe représentant une solution pour un problème complet
 * @author Anas Neumann <anas.neumann.1@ulaval.ca>
 * @since 25/03/2019
 * @version 1.0
 */
public class Solution {
	public final Integer nbrLoadStations = 3;
	public final Integer nbrModes = 3;
	public Double MT = 0.3;
	public Double LT = 0.2;
	protected List<CeduledJob> jobs = new ArrayList<CeduledJob>();

	/**
	 * Calculer la valeur de la fonction objectif d'une solution
	 * @return
	 */
	public Double computeAverageDelay() {
		Double delay = 0.0;
		for(CeduledJob j : getJobs()) {
			if(j.getEndDate() > j.getDueDate()) {
				delay += (j.getEndDate() - j.getDueDate());
			}
		}
		if(delay > 0) {
			delay /= getJobs().size();
		}
		return delay;
	}
	
	/**
	 * Build a solution from an instance
	 * @param i
	 * @return
	 */
	public static Solution fromInstance(Instance i, Double MT, Double LT) {
		List<CeduledJob> jobs = new ArrayList<CeduledJob>();
		for(Job j : i.getJobs()) {
			jobs.add(CeduledJob.fromJob(j));
		}
		Solution s = new Solution().setJobs(jobs);
		s.MT = MT;
		s.LT = LT;
		return s;
	}

	/**
	 * clone a complete solution
	 */
	public Solution clone() {
		List<CeduledJob> cloneJobs = new ArrayList<CeduledJob>();
		for(CeduledJob j : this.jobs) {
			cloneJobs.add(j.clone());
		}
		Solution s = new Solution()
				.setJobs(cloneJobs);
		s.MT = this.MT;
		s.LT = this.LT;
		return s;
	}

	/**
	 * @return the jobs
	 */
	public List<CeduledJob> getJobs() {
		return jobs;
	}

	/**
	 * @param jobs the jobs to set
	 */
	public Solution setJobs(List<CeduledJob> jobs) {
		this.jobs = jobs;
		return this;
	}
}
