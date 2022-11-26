package main.model;

/**
 * Classe représentant une opération
 * @author Anas Neumann <anas.neumann.1@ulaval.ca>
 * @since 07/03/2019
 * @version 1.0
 */
public class Operation {
	protected Double processingTime = 0.0;
	protected Integer weldingProcess = 0;

	/**
	 * @return the processingTime
	 */
	public Double getProcessingTime() {
		return processingTime;
	}

	/**
	 * @param processingTime
	 *            the processingTime to set
	 */
	public Operation setProcessingTime(Double processingTime) {
		this.processingTime = processingTime;
		return this;
	}

	/**
	 * @return the weldingProcess
	 */
	public Integer getWeldingProcess() {
		return weldingProcess;
	}

	/**
	 * @param weldingProcess
	 *            the weldingProcess to set
	 */
	public Operation setWeldingProcess(Integer weldingProcess) {
		this.weldingProcess = weldingProcess;
		return this;
	}

}
