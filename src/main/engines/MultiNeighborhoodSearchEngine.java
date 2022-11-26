package main.engines;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Date;

import main.managers.DateManager;
import main.managers.ParallelManager;
import main.managers.StationManager;
import main.model.Instance;
import main.solution.CeduledJob;
import main.solution.CeduledOperation;
import main.solution.Solution;

/**
 * Engin de recherche dans un voisinage multiple (Heuristique hybride)
 * @author Anas Neumann <anas.neumann.1@ulaval.ca>
 * @since 25/03/2019
 * @version 1.0
 */
public class MultiNeighborhoodSearchEngine {
	private static MultiNeighborhoodSearchEngine engine;

	/**
	 * Private constuctor
	 */
	private MultiNeighborhoodSearchEngine() {

	}

	/**
	 * Récupérer le singleton
	 * 
	 * @return the engine
	 */
	public static MultiNeighborhoodSearchEngine getEngine() {
		if (null == engine) {
			engine = new MultiNeighborhoodSearchEngine();
		}
		return engine;
	}

	/**
	 * Solve the problem
	 * @param problem
	 * @param MT
	 * @param LT
	 */
	public Solution solve(Instance problem, Double MT, Double LT) {
		Long startTime = System.currentTimeMillis();
		Solution s = buildInitialSolution(problem, MT, LT);
		for(int index = 0; index < s.getJobs().size() - 1; index++) {
			if(s.computeAverageDelay().equals(0.0)) {
				displaySolution(s, startTime, System.currentTimeMillis());
				return s;
			}
			Solution newSolution = localSearch(s, index);
			if(newSolution.computeAverageDelay() <= s.computeAverageDelay()) {
				s = newSolution;	
			}
		}
		displaySolution(s, startTime, System.currentTimeMillis());
		return s;
	}

	/**
	 * Build a first version of the solution
	 * @param i
	 * @param MT
	 * @param LT
	 * @return
	 */
	public Solution buildInitialSolution(Instance i, Double MT, Double LT) {
		Solution s = Solution.fromInstance(i, MT, LT);
		Collections.sort(s.getJobs());
		for(CeduledJob j : s.getJobs()) {
			for(CeduledOperation o : j.getCeduledOperations()) {
				o.setMode(o.getWeldingProcess().equals(1)? CeduledOperation.MODE_A : CeduledOperation.MODE_C);
			}
		}
		return amelioration(runSolution(s));
	}

	/**
	 * Améliorer une solution en cherchant à y intéger du paralélisme
	 * @param s
	 * @return
	 */
	public Solution amelioration(Solution s) {
		Double currentDelay = s.computeAverageDelay();
		Double newDelay = 0.0;
		for(CeduledJob j : s.getJobs()) {
			for(CeduledOperation o : j.getCeduledOperations()) {
				if(o.getMode().equals(CeduledOperation.MODE_A)) {
					o.setMode(CeduledOperation.MODE_B);
					newDelay = runSolution(s).computeAverageDelay();
					if(currentDelay < newDelay) {
						o.setMode(CeduledOperation.MODE_A);
					} else {
						currentDelay = newDelay;
					}
				}
			}
		}
		return runSolution(s);
	}

	/**
	 * chercher à intervetir deux pièces à partir d'une position (en améliorant la solution trouvée)
	 * @param s
	 * @param index
	 * @return
	 */
	public Solution localSearch(Solution s, int index) {
		Solution neighborhood = s.clone();
		CeduledJob j1 = neighborhood.getJobs().get(index);
		CeduledJob j2 = neighborhood.getJobs().get(index + 1);
		neighborhood.getJobs().add(index, j2);
		neighborhood.getJobs().add(index + 1, j1);
		neighborhood.getJobs().remove(index + 2);
		neighborhood.getJobs().remove(index + 2);
		return amelioration(runSolution(neighborhood));
	}

	/**
	 * Dérouler une solution et calculer tous les temps
	 * @param s
	 * @return the solution with informations
	 */
	public Solution runSolution(Solution s) {
		StationManager m = StationManager.createManager();
		DateManager d = DateManager.createManager(s.MT);
		ParallelManager p = ParallelManager.createManager();
		removeJobs(s,d,m);
		for(CeduledJob j : s.getJobs()) {
			d.date = Math.max(m.enterStation(j, s.LT), d.date);
			runJobFromOperation(m, j, 0, d, s, p);
		}
		return s;
	}

	/**
	 * Compute the informations of a job from a date and an operation
	 * @param m
	 * @param j
	 * @param index
	 * @param d
	 * @param s
	 * @param p
	 * @return ParallelManager
	 */
	public void runJobFromOperation(StationManager m, CeduledJob j, int index, DateManager d, Solution s, ParallelManager p) {
		for(int i=index; i<j.getCeduledOperations().size(); i++) {
			CeduledOperation o = j.getCeduledOperations().get(i);
			if(!alreadyReady(j, index)) {
				d.doAMove();
			}
			if(o.getMode().equals(CeduledOperation.MODE_B)) {
				if(!alreadyReady(j, index)) {
					d.date += j.getPositionTime();
				}
				p.bloc(j, o);
			}
			if(o.getMode().equals(CeduledOperation.MODE_C) && (i == 0) && null != p.job && !j.getId().equals(p.job.getId())) {
				d.date = Math.max(j.getLoadedDate(), p.operation.getBeginDate());
				if(alreadyReady(p.job, p.job.getCeduledOperations().indexOf(p.operation))) {
					d.doAMove();
				} else {
					d.moves(2);
				}
				o.setBeginDate(d.date);
				o.setEndDate(d.date + o.getProcessingTime());
				d.date = Math.max(p.operation.getEndDate(), o.getEndDate());
				if(i >= (j.getCeduledOperations().size() - 1)) {
					m.leaveStation(j, o.getEndDate() + s.MT, j.getLoadedStation(), s.LT);
					d.date = Math.max(o.getEndDate() + (2 * s.MT), d.date);
					runJobFromOperation(m, p.job, p.job.getCeduledOperations().indexOf(p.operation)+1, d, s, p);
					p.job = null;
					p.operation = null;
					return;
				} else {
					d.moves(2);
					runJobFromOperation(m, p.job, p.job.getCeduledOperations().indexOf(p.operation)+1, d, s, p);
					p.job = null;
					p.operation = null;
				}
			} else {
				if(!alreadyReady(j, index)) {
					o.setBeginDate(d.date);
				} else {
					o.setBeginDate(0.0);
				}
				d.date = Math.max(d.date, o.getBeginDate() + o.getProcessingTime());
				o.setEndDate(d.date);
			}
		}
		d.doAMove();
		m.leaveStation(j, d.date, j.getLoadedStation(), s.LT);
	}

	/**
	 * An opération d'ont need to wait for the robot and is already ready
	 * @param j
	 * @param index
	 * @return
	 */
	private boolean alreadyReady(CeduledJob j, int index) {
		boolean ready = false;
		if(index == 0 && !j.isRemoved() && j.getWeldingHistory().equals(3)) {
			ready = true;
		}
		return ready;
	}

	/**
	 * Retirer des jobs s'il ne sont pas prioritaire
	 * @param s
	 * @param d
	 * @param m
	 * @return
	 */
	private void removeJobs(Solution s, DateManager d, StationManager m) {
		boolean hasRemoved = false;
		Integer position = 0;
		boolean needStation2 = false;
		for(CeduledJob j : s.getJobs()) {
			if(position < 3 && (!needStation2 || !j.getLoadingHistory().equals(2))) {
				j.setRemoved(false);
			} else {
				if(j.getWeldingHistory() > 0) {
					j.setRemoved(true);
					if(!hasRemoved) {
						d.doAMove();
						m.leaveStation(j, d.date, j.getLoadingHistory()-1, s.LT);
						hasRemoved = true;
					} else {
						d.moves(2);
						m.leaveStation(j, d.date, j.getLoadingHistory()-1, s.LT);
					}
				} else if(j.getLoadingHistory() > 0) {
					j.setRemoved(true);
					m.leaveStation(j, 0.0, j.getLoadingHistory()-1, s.LT);
				}
			}
			needStation2 = (needStation2 || j.isSize());
			position++;
		}
	}

	/**
	 * Display a solution in details
	 * @param s
	 * @param start
	 * @param end
	 */
	public void displaySolution(Solution s, Long start, Long end) {
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		System.out.println("\n=== RESULTATS DE L'HEURISTIQUE ===");
		displayPerformance(start,end);
		System.out.println("=> Retard moyen final = "+df.format(s.computeAverageDelay()));
		for(CeduledJob j : s.getJobs()) {
			System.out.println("\n--- Pièce n°"+j.getId()+" chargée sur la station "+(j.getLoadedStation()+1)+" à "+df.format(j.getLoadedDate())+" minutes ---");
			if(j.isSize()) {
				System.out.println("-> Il s'agit d'une grande pièce !");
			}
			System.out.println("-> Fin (déchargée) à "+df.format(j.getEndDate())+" avec une due date à "+j.getDueDate());
			if(j.isRemoved()) {
				System.out.println("-> Cette pièce à été déchargée de la station "+j.getLoadingHistory()+" !");
			}
			System.out.println("\nOPERATIONS :");
			for(CeduledOperation o : j.getCeduledOperations()) {
				System.out.println("");
				System.out.println("Opération n°"+(j.getCeduledOperations().indexOf(o)+1)+" débutée à "+df.format(o.getBeginDate())+" finie à "+df.format(o.getEndDate()));
				System.out.println("--> Cette opération a été réalisée en mode : "+o.modeToString());
			}
			System.out.println("\n------------------------------------------------");
		}
	}

	/**
	 * Afficher la durée totale d'éxécution du programme
	 * @param start
	 * @param end
	 */
	public void displayPerformance(Long start, Long end) {
		Date dateDebut = new Date (start);
		Date dateFin = new Date (end);
		Date duree = new Date (end);
		duree.setTime (dateFin.getTime () - dateDebut.getTime());
		long secondes = duree.getTime () / 1000;
		long min = secondes / 60;
		long heures = min / 60;
		long mili = duree.getTime () % 1000;
		secondes %= 60;
		System.out.println ("Temps total de traitement : "+heures+" heures; " + min + " minutes;  " + secondes + " secondes; " + mili);
	}
}