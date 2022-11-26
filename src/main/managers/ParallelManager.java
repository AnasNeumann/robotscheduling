package main.managers;

import main.solution.CeduledJob;
import main.solution.CeduledOperation;

/**
 * Classe représentant la gestion d'un job bloqué acause du paralélisme
 * @author Anas Neumann <anas.neumann.1@ulaval.ca>
 * @since 05/04/2019
 * @version 1.0
 */
public class ParallelManager {
	public CeduledJob job;
	public CeduledOperation operation;
	
	/**
	 * Creér un manager
	 * @return
	 */
	public static ParallelManager createManager() {
		return new ParallelManager();
	}
	
	/**
	 * bloquer un job
	 * @param job
	 * @param operation
	 * @return
	 */
	public ParallelManager bloc(CeduledJob job, CeduledOperation operation) {
		this.job = job;
		this.operation = operation;
		return this;
	}
}
