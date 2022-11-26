package main.solution;

import main.model.Operation;

/**
 * Classe représentant une solution pour une opération
 * @author Anas Neumann <anas.neumann.1@ulaval.ca>
 * @since 25/03/2019
 * @version 1.0
 */
public class CeduledOperation extends Operation {
	protected Integer position = 0;
	protected Double beginDate = 0.0;
	protected Double endDate = 0.0;
	protected Integer mode;
	
	/**
	 * Constantes
	 */
	public static final int MODE_A = 1;
	public static final int MODE_B = 2;
	public static final int MODE_C = 3;


	/**
	 * Construire une opération cédulée à partir d'une opération du problème
	 * @param o
	 * @return
	 */
	public static CeduledOperation fromOperation(Operation o) {
		return new CeduledOperation()
				.setWeldingProcess(o.getWeldingProcess())
				.setProcessingTime(o.getProcessingTime());
	}
	
	/**
	 * Clone a cedulled opération
	 */
	public CeduledOperation clone() {
		return new CeduledOperation()
				.setWeldingProcess(this.getWeldingProcess())
				.setProcessingTime(this.getProcessingTime())
				.setPosition(this.getPosition())
				.setBeginDate(this.beginDate)
				.setEndDate(this.endDate)
				.setMode(this.mode);
	}

	/**
	 * Affichage des informations sur les modes et procédés appliqués
	 * @return
	 */
	public String modeToString() {
		switch(this.mode){
			case MODE_A : 
				return "procédé 1 éxécutée seul";
			case MODE_B : 
				return "procédé 1 éxécutée sur le positionneur (possibilité de parallèle)";
			case MODE_C : 
				return "procédé 2";
		}
		return "";
	}

	/**
	 * @return the position
	 */
	public Integer getPosition() {
		return position;
	}

	/**
	 * @param position
	 *            the position to set
	 */
	public CeduledOperation setPosition(Integer position) {
		this.position = position;
		return this;
	}

	/**
	 * @return the beginDate
	 */
	public Double getBeginDate() {
		return beginDate;
	}

	/**
	 * @param beginDate
	 *            the beginDate to set
	 */
	public CeduledOperation setBeginDate(Double beginDate) {
		this.beginDate = beginDate;
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
	public CeduledOperation setEndDate(Double endDate) {
		this.endDate = endDate;
		return this;
	}
	
	/**
	 * @return the mode
	 */
	public Integer getMode() {
		return mode;
	}

	/**
	 * @param mode
	 *            the mode to set
	 */
	public CeduledOperation setMode(Integer mode) {
		this.mode = mode;
		return this;
	}

	/**
	 * @param processingTime
	 *            the processingTime to set
	 */
	public CeduledOperation setProcessingTime(Double processingTime) {
		this.processingTime = processingTime;
		return this;
	}

	/**
	 * @param weldingProcess
	 *            the weldingProcess to set
	 */
	public CeduledOperation setWeldingProcess(Integer weldingProcess) {
		this.weldingProcess = weldingProcess;
		return this;
	}
}