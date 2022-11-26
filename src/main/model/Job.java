package main.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant une pièce
 * @author Anas Neumann <anas.neumann.1@ulaval.ca>
 * @since 07/03/2019
 * @version 1.0
 */
public class Job {
	protected Long id = 0L;
	protected boolean size = false;
	protected Double dueDate = 0.0;
	protected Double positionTime = 0.0;
	protected Integer loadingHistory = 0;
	protected Integer weldingHistory = 0;
	private List<Operation> operations = new ArrayList<Operation>();

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public Job setId(Long id) {
		this.id = id;
		return this;
	}

	/**
	 * @return the size
	 */
	public boolean isSize() {
		return size;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public Job setSize(boolean size) {
		this.size = size;
		return this;
	}

	/**
	 * @return the dueDate
	 */
	public Double getDueDate() {
		return dueDate;
	}

	/**
	 * @param dueDate
	 *            the dueDate to set
	 */
	public Job setDueDate(Double dueDate) {
		this.dueDate = dueDate;
		return this;
	}

	/**
	 * @return the positionTime
	 */
	public Double getPositionTime() {
		return positionTime;
	}

	/**
	 * @param positionTime
	 *            the positionTime to set
	 */
	public Job setPositionTime(Double positionTime) {
		this.positionTime = positionTime;
		return this;
	}

	/**
	 * @return the operations
	 */
	public List<Operation> getOperations() {
		return operations;
	}

	/**
	 * @param operations
	 *            the operations to set
	 */
	public Job setOperations(List<Operation> operations) {
		this.operations = operations;
		return this;
	}

	/**
	 * @return the loadingHistory
	 */
	public Integer getLoadingHistory() {
		return loadingHistory;
	}

	/**
	 * @param loadingHistory the loadingHistory to set
	 */
	public Job setLoadingHistory(Integer loadingHistory) {
		this.loadingHistory = loadingHistory;
		return this;
	}

	/**
	 * @return the weldingHistory
	 */
	public Integer getWeldingHistory() {
		return weldingHistory;
	}

	/**
	 * @param weldingHistory the weldingHistory to set
	 */
	public Job setWeldingHistory(Integer weldingHistory) {
		this.weldingHistory = weldingHistory;
		return this;
	}
}
