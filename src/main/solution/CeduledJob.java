package main.solution;

import java.util.ArrayList;
import java.util.List;

import main.model.Job;
import main.model.Operation;

/**
 * Classe représentant une solution pour un job
 * @author Anas Neumann <anas.neumann.1@ulaval.ca>
 * @since 25/03/2019
 * @version 1.0
 */
public class CeduledJob extends Job implements Comparable<CeduledJob> {
	protected List<CeduledOperation> ceduledOperations = new ArrayList<CeduledOperation>();
	protected Integer loadedStation = 0;
	protected Double loadedDate = 0.0;
	protected Double endDate = 0.0;
	protected boolean removed = false;
	private Double totalTime = null;

	/**
	 * Construire un job cédulé à partir d'un job du problème
	 * @param j
	 * @return
	 */
	public static CeduledJob fromJob(Job j) {
		List<CeduledOperation> operations = new ArrayList<CeduledOperation>();
		for(Operation o : j.getOperations()) {
			operations.add(CeduledOperation.fromOperation(o));
		}
		return new CeduledJob()
				.setId(j.getId())
				.setLoadingHistory(j.getLoadingHistory())
				.setPositionTime(j.getPositionTime())
				.setSize(j.isSize())
				.setWeldingHistory(j.getWeldingHistory())
				.setDueDate(j.getDueDate())
				.setCeduledOperations(operations);
	}

	/**
	 * Redéfinition de la méthode de comparaison
	 * @param j
	 */
	@Override
	public int compareTo(CeduledJob j) {
		if (null != j) {
			if(!this.getDueDate().equals(j.getDueDate())) {
				return this.getDueDate().compareTo(j.getDueDate());
			} else {
				return this.computeTotalTime().compareTo(j.computeTotalTime());
			}
		}
		return 0;
	}
	
	/**
	 * clone a ceduled job
	 */
	public CeduledJob clone() {
		List<CeduledOperation> cloneOperations = new ArrayList<CeduledOperation>();
		for(CeduledOperation o : this.ceduledOperations) {
			cloneOperations.add(o.clone());
		}
		CeduledJob j = new CeduledJob().setId(this.getId())
				.setLoadingHistory(this.getLoadingHistory())
				.setPositionTime(this.getPositionTime())
				.setSize(this.isSize())
				.setWeldingHistory(this.getWeldingHistory())
				.setDueDate(this.getDueDate())
				.setLoadedDate(this.loadedDate)
				.setLoadedStation(this.loadedStation)
				.setRemoved(this.removed)
				.setEndDate(this.endDate)
				.setCeduledOperations(cloneOperations);
		j.totalTime = this.totalTime;
		return j;
	}

	/**
	 * compute the total processing time
	 * @return
	 */
	public Double computeTotalTime() {
		if(null != totalTime) {
			return totalTime;
		}
		for(CeduledOperation o : this.getCeduledOperations()) {
			totalTime += o.getProcessingTime();
		}
		return totalTime;
	}

	/**
	 * @return the operations
	 */
	public List<CeduledOperation> getCeduledOperations() {
		return ceduledOperations;
	}

	/**
	 * @param operations
	 *            the operations to set
	 */
	public CeduledJob setCeduledOperations(List<CeduledOperation> ceduledOperations) {
		this.ceduledOperations = ceduledOperations;
		return this;
	}

	/**
	 * @return the loadedStation
	 */
	public Integer getLoadedStation() {
		return loadedStation;
	}

	/**
	 * @param loadedStation
	 *            the loadedStation to set
	 */
	public CeduledJob setLoadedStation(Integer loadedStation) {
		this.loadedStation = loadedStation;
		return this;
	}

	/**
	 * @return the loadedDate
	 */
	public Double getLoadedDate() {
		return loadedDate;
	}

	/**
	 * @param loadedDate
	 *            the loadedDate to set
	 */
	public CeduledJob setLoadedDate(Double loadedDate) {
		this.loadedDate = loadedDate;
		return this;
	}

	/**
	 * @return the endDate
	 */
	public Double getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public CeduledJob setEndDate(Double endDate) {
		this.endDate = endDate;
		return this;
	}

	/**
	 * @return the removed
	 */
	public boolean isRemoved() {
		return removed;
	}

	/**
	 * @param removed
	 *            the removed to set
	 */
	public CeduledJob setRemoved(boolean removed) {
		this.removed = removed;
		return this;
	}

	/**
	 * @param id the id to set
	 */
	public CeduledJob setId(Long id) {
		this.id = id;
		return this;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public CeduledJob setSize(boolean size) {
		this.size = size;
		return this;
	}

	/**
	 * @param dueDate
	 *            the dueDate to set
	 */
	public CeduledJob setDueDate(Double dueDate) {
		this.dueDate = dueDate;
		return this;
	}

	/**
	 * @param positionTime
	 *            the positionTime to set
	 */
	public CeduledJob setPositionTime(Double positionTime) {
		this.positionTime = positionTime;
		return this;
	}

	/**
	 * @param loadingHistory the loadingHistory to set
	 */
	public CeduledJob setLoadingHistory(Integer loadingHistory) {
		this.loadingHistory = loadingHistory;
		return this;
	}

	/**
	 * @param weldingHistory the weldingHistory to set
	 */
	public CeduledJob setWeldingHistory(Integer weldingHistory) {
		this.weldingHistory = weldingHistory;
		return this;
	}
}
