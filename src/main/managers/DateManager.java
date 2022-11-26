package main.managers;

/**
 * Classe représentant la gestion du temps écoulé
 * @author Anas Neumann <anas.neumann.1@ulaval.ca>
 * @since 04/04/2019
 * @version 1.0
 */
public class DateManager {
	public Double date = 0.0;
	private Double MT = 0.0;
	
	/**
	 * Create a new manager
	 * @param MT
	 * @return
	 */
	public static DateManager createManager(Double MT) {
		DateManager m = new DateManager();
		m.MT = MT;
		m.date = 0.0;
		return m;
	}

	/**
	 * Do move
	 * - go to station
	 * - go to plateform 1 or 2
	 * @return
	 */
	public DateManager doAMove() {
		date += MT;
		return this;
	}
	
	/**
	 * Do moves
	 * @return
	 */
	public DateManager moves(int nbrOfMoves) {
		date += nbrOfMoves * MT;
		return this;
	}
}
