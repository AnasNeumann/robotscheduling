package main.model;

import java.util.ArrayList;
import java.util.List;

import ilog.concert.IloIntVar;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;

/**
 * Classe représentant notre modèle de précédence à résoudre
 * @author Anas Neumann <anas.neumann.1@ulaval.ca>
 * @since 10/03/2019
 * @version 1.0
 */
public class Model {
	public final Integer nbrLoadStations = 3;
	public final Integer nbrModes = 3;
	public final Double MT = 0.3;
	public final Double LT = 0.2;

	public List<Job> jobs = new ArrayList<Job>();
	public Integer nbrJobs;
	public Double I = 0.0;
	
	public IloIntVar[][] varLoad;
	public IloNumVar[][] varBegin;
	public IloNumVar[][] varWeld;
	public IloIntVar[][][] varMode;
	public IloIntVar[][][][] varPrecedence;
	public IloNumVar[] varDelay;
	public IloNumVar[][] varParralel;
	public IloNumVar[][] varRemove;

	public IloRange[][][][] C1;
	public IloRange[][] C2;
	public IloRange[][][][] C3;
	public IloRange[][][][] C4;
	public IloRange[][][][] C5;
	public IloRange[] C6;
	public IloRange[][] C7;
	public IloRange[][] C8;
	public IloRange[] C9;
	public IloRange[] C10;
	public IloRange[][][][] C11;
	public IloRange[][][][][][] C12;
	public IloRange[] C13;
	public IloRange[][] C14;
	public IloRange[][][][] C15;
	public IloRange[][][][] C16;
	public IloRange[][] C17;
	public IloRange[][] C18;
	public IloRange[][][] C19;
	public IloRange[][][] C20;

	/**
	 * Constructeur à partir d'un probleme
	 * @param problem
	 */
	public Model(Instance problem, boolean withMoveTimes) {
		this.jobs = problem.getJobs();
		nbrJobs = this.jobs.size();

		// Calcul d'une borne superieur
		for(Job j : jobs) {
			for(Operation o : j.getOperations()) {
				I += o.getProcessingTime() + j.getPositionTime() + (withMoveTimes? 3*MT + 2*LT : 0);
			}
		}

		varLoad = new IloIntVar[nbrJobs][nbrLoadStations];
		varBegin = new IloNumVar[nbrJobs][nbrLoadStations];
		varWeld = new IloNumVar[nbrJobs][];
		varMode = new IloIntVar[nbrJobs][][];
		varPrecedence = new IloIntVar[nbrJobs][][][];
		varDelay = new IloNumVar[nbrJobs];
		varParralel = new IloNumVar[nbrJobs][];
		varRemove = new IloNumVar[nbrJobs][nbrLoadStations];

		C1 = new IloRange[nbrJobs][][][];
		C2 = new IloRange[nbrJobs][];
		C3 = new IloRange[nbrJobs][][][];
		C4 = new IloRange[nbrJobs][][][];
		C5 = new IloRange[nbrJobs][][][];
		C6 = new IloRange[nbrJobs];
		C7 = new IloRange[nbrJobs][];
		C8 = new IloRange[nbrJobs][];
		C9 = new IloRange[nbrJobs];
		C10 = new IloRange[nbrJobs];
		C11 = new IloRange[nbrJobs][][][];
		C12 = new IloRange[nbrJobs][][][][][];
		C13 = new IloRange[nbrJobs];
		C14 = new IloRange[nbrJobs][];
		C15 = new IloRange[nbrJobs][][][];
		C16 = new IloRange[nbrJobs][][][];
		C17 = new IloRange[nbrJobs][];
		C18 = new IloRange[nbrJobs][nbrLoadStations];
		C19 = new IloRange[nbrJobs][nbrLoadStations][nbrJobs];
		C20 = new IloRange[nbrJobs][nbrLoadStations][nbrJobs];
	}
}
