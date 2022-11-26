package main.boot;

import main.engines.MultiNeighborhoodSearchEngine;
import main.engines.OptimalSchedulingEngineV2;
import main.engines.OptimalSchedulingEngineV3;
import main.engines.ParsingFileEngine;
import main.model.Instance;

/**
 * Classe principale du projet
 * @author Anas Neumann <anas.neumann.1@ulaval.ca>
 * @since 08/03/2019
 * @version 1.0
 */
public class Main {

	/**
	 * Main method
	 * @param args
	 */
	public static void main(String[] args) {
		Double MT = 0.3, LT = 0.2;

		Instance problem1 = ParsingFileEngine.BuildInstance("test/instance_3.xlsx");
		OptimalSchedulingEngineV2.getEngine().solve(problem1);
		MultiNeighborhoodSearchEngine.getEngine().solve(problem1, MT, LT);

		Instance problem2 = ParsingFileEngine.BuildInstance("test/instance_4_2.xlsx");
		OptimalSchedulingEngineV2.getEngine().solve(problem2);
		MultiNeighborhoodSearchEngine.getEngine().solve(problem2, MT, LT);

		Instance problem3 = ParsingFileEngine.BuildInstance("test/instance_dynamique.xlsx");
		OptimalSchedulingEngineV3.getEngine().solve(problem3);
		MultiNeighborhoodSearchEngine.getEngine().solve(problem3, MT, LT);
		
		Instance problemReel = ParsingFileEngine.BuildInstance("test/instanceBig2.xlsx");
		MultiNeighborhoodSearchEngine.getEngine().solve(problemReel, MT, LT);
	}
}