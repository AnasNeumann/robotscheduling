package main.engines;

import java.text.DecimalFormat;

import main.model.Instance;
import main.model.Job;
import main.model.Model;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

/**
 * Classe de résolution d'une instance à l'aide de Cplex
 * @author Anas Neumann <anas.neumann.1@ulaval.ca>
 * @since 08/03/2019
 * @version 1.0
 */
public class OptimalSchedulingEngineV3 {
	private static OptimalSchedulingEngineV3 engine;

	private OptimalSchedulingEngineV3() {

	}

	/**
	 * Récupérer le singleton
	 * 
	 * @return
	 */
	public static OptimalSchedulingEngineV3 getEngine() {
		if (null == engine) {
			engine = new OptimalSchedulingEngineV3();
		}
		return engine;
	}

	/**
	 * Création de l'ensemble des variables
	 * 
	 * @param cplex
	 * @param m
	 * @throws IloException
	 */
	private void createVariables(IloCplex cplex, Model m) throws IloException {
		String[] namesDelay = new String[m.nbrJobs];
		for (int j = 0; j < m.nbrJobs; j++) {
			namesDelay[j] = "D" + j;
			Integer nbrOpJ = m.jobs.get(j).getOperations().size();
			String[] namesLoads = new String[m.nbrLoadStations];
			String[] namesBegin = new String[m.nbrLoadStations];
			String[] namesRemove = new String[m.nbrLoadStations];
			String[] namesWeld = new String[nbrOpJ];
			String[] namesParallel = new String[nbrOpJ];
			m.varMode[j] = new IloIntVar[nbrOpJ][m.nbrModes];
			m.varPrecedence[j] = new IloIntVar[nbrOpJ][m.nbrJobs][];
			for (int q = 0; q < nbrOpJ; q++) {
				namesWeld[q] = "W(" + j + "," + q + ")";
				namesParallel[q] = "PA(" + j + "," + q + ")";
				String[] namesModes = new String[m.nbrModes];
				for (int o = 0; o < m.nbrModes; o++) {
					namesModes[o] = "M(" + j + "," + q + "," + o + ")";
				}
				for (int i = 0; i < m.nbrJobs; i++) {
					Integer nbrOpI = m.jobs.get(i).getOperations().size();
					String[] namesPrecedences = new String[nbrOpI];
					for (int k = 0; k < nbrOpI; k++) {
						namesPrecedences[k] = "P(" + j + "," + q + "," + i + "," + k + ")";
					}
					m.varPrecedence[j][q][i] = cplex.boolVarArray(nbrOpI, namesPrecedences);
				}
				m.varMode[j][q] = cplex.boolVarArray(m.nbrModes, namesModes);
			}
			for (int l = 0; l < m.nbrLoadStations; l++) {
				namesLoads[l] = "L(" + j + "," + l + ")";
				namesBegin[l] = "B(" + j + "," + l + ")";
				namesRemove[l] = "R(" + j + "," + l + ")";
			}
			m.varWeld[j] = cplex.numVarArray(nbrOpJ, 0.0, Double.MAX_VALUE, namesWeld);
			m.varParralel[j] = cplex.boolVarArray(nbrOpJ, namesParallel);
			m.varLoad[j] = cplex.boolVarArray(m.nbrLoadStations, namesLoads);
			m.varRemove[j] = cplex.boolVarArray(m.nbrLoadStations, namesRemove);
			m.varBegin[j] = cplex.numVarArray(m.nbrLoadStations, 0.0, Double.MAX_VALUE, namesBegin);
		}
		m.varDelay = cplex.numVarArray(m.nbrJobs, 0.0, Double.MAX_VALUE, namesDelay);
	}

	/**
	 * Création de l'ensemble des contraintes
	 * 
	 * @param cplex
	 * @param m
	 * @throws IloException
	 */
	private void createConstraints(IloCplex cplex, Model m) throws IloException {
		for (int j = 0; j < m.nbrJobs; j++) {
			Job job = m.jobs.get(j);
			m.C13[j] = cplex.addGe(m.varDelay[j], 0, "C13(" + j + ")");
			Integer nbrOpJ = job.getOperations().size();
			m.C1[j] = new IloRange[nbrOpJ][][];
			m.C2[j] = new IloRange[nbrOpJ];
			m.C3[j] = new IloRange[nbrOpJ][][];
			m.C4[j] = new IloRange[nbrOpJ][][];			
			m.C5[j] = new IloRange[nbrOpJ][][];
			m.C7[j] = new IloRange[nbrOpJ];
			m.C8[j] = new IloRange[nbrOpJ];
			m.C14[j] = new IloRange[nbrOpJ];
			m.C15[j] = new IloRange[nbrOpJ][][];
			m.C16[j] = new IloRange[nbrOpJ][][];
			m.C17[j] = new IloRange[nbrOpJ];
			for (int q = 0; q < nbrOpJ; q++) {
				m.C1[j][q] = new IloRange[m.nbrJobs][];
				m.C3[j][q] = new IloRange[m.nbrJobs][];
				m.C4[j][q] = new IloRange[m.nbrJobs][];
				m.C5[j][q] = new IloRange[m.nbrJobs][];
				m.C15[j][q] = new IloRange[m.nbrJobs][];
				m.C16[j][q] = new IloRange[m.nbrJobs][];
				for (int i = 0; i < m.nbrJobs; i++) {
					if (i != j) {
						Job job2 = m.jobs.get(i);
						Integer nbrOpI = job2.getOperations().size();
						m.C1[j][q][i] = new IloRange[nbrOpI];
						m.C3[j][q][i] = new IloRange[nbrOpI];
						m.C4[j][q][i] = new IloRange[nbrOpI];
						m.C5[j][q][i] = new IloRange[nbrOpI];
						m.C15[j][q][i] = new IloRange[nbrOpI];
						m.C16[j][q][i] = new IloRange[nbrOpI];
						for (int k = 0; k < nbrOpI; k++) {
							IloLinearNumExpr c1 = cplex.linearNumExpr();
							c1.addTerm(1.0, m.varPrecedence[j][q][i][k]);
							c1.addTerm(1.0, m.varPrecedence[i][k][j][q]);
							m.C1[j][q][i][k] = cplex.addEq(c1, 1, "C1(" + j + "," + q + "," + i + "," + k + ")");

							IloLinearNumExpr c3 = cplex.linearNumExpr();
							c3.addTerm(1.0, m.varWeld[j][q]);
							c3.addTerm(-1.0, m.varWeld[i][k]);
							if(k > 0 || !job2.getWeldingHistory().equals(3)) {
								c3.addTerm(-1.0 * job2.getPositionTime(), m.varMode[i][k][1]);
							}
							c3.addTerm(-m.I, m.varPrecedence[i][k][j][q]);
							c3.addTerm(-m.I, m.varMode[i][k][0]);
							c3.addTerm(-m.I, m.varMode[i][k][2]);
							m.C3[j][q][i][k] = cplex.addGe(c3,
									job2.getOperations().get(k).getProcessingTime() - (2 * m.I) + 2 * m.MT,
									"C3(" + j + "," + q + "," + i + "," + k + ")");

							IloLinearNumExpr c4 = cplex.linearNumExpr();
							c4.addTerm(1.0, m.varWeld[j][q]);
							c4.addTerm(-1.0, m.varWeld[i][k]);
							if(k > 0 || !job2.getWeldingHistory().equals(3)) {
								c4.addTerm(-1.0 * job2.getPositionTime(), m.varMode[i][k][1]);
							}
							c4.addTerm(-m.I, m.varPrecedence[i][k][j][q]);
							c4.addTerm(-m.I, m.varMode[j][q][0]);
							c4.addTerm(-m.I, m.varMode[j][q][1]);
							m.C4[j][q][i][k] = cplex.addGe(c4,
									job2.getOperations().get(k).getProcessingTime() - (2 * m.I) + 2 * m.MT,
									"C4(" + j + "," + q + "," + i + "," + k + ")");
							
							IloLinearNumExpr c16 = cplex.linearNumExpr();
							c16.addTerm(1.0, m.varWeld[j][q]);
							c16.addTerm(-1.0, m.varWeld[i][k]);
							if(k > 0 || !job2.getWeldingHistory().equals(3)) {
								c16.addTerm(-1.0 * job2.getPositionTime(), m.varMode[i][k][1]);
							}
							c16.addTerm(-m.I, m.varPrecedence[i][k][j][q]);
							c16.addTerm(+m.I, m.varParralel[j][q]);
							m.C16[j][q][i][k] = cplex.addGe(c16,
									job2.getOperations().get(k).getProcessingTime() - m.I + 2 * m.MT,
									"C16(" + j + "," + q + "," + i + "," + k + ")");

							IloLinearNumExpr c5 = cplex.linearNumExpr();
							c5.addTerm(1.0, m.varWeld[j][q]);
							c5.addTerm(-1.0, m.varWeld[i][k]);						
							c5.addTerm(-1.0 * m.I, m.varPrecedence[i][k][j][q]);
							if(k>0 || !job2.getWeldingHistory().equals(3)) {
								c5.addTerm(-1.0 * job2.getPositionTime(), m.varMode[i][k][1]);
								m.C5[j][q][i][k] = cplex.addGe(c5, -m.I + 2 * m.MT, "C5(" + j + "," + q + "," + i + "," + k + ")");
							} else {
								m.C5[j][q][i][k] = cplex.addGe(c5, -m.I, "C5(" + j + "," + q + "," + i + "," + k + ")");
							}

							if (q == (nbrOpJ - 1)) {
								IloLinearNumExpr c15 = cplex.linearNumExpr();
								c15.addTerm(1.0, m.varDelay[j]);
								c15.addTerm(-1.0, m.varWeld[i][k]);
								if(k > 0 || !job2.getWeldingHistory().equals(3)) {
									c15.addTerm(-job2.getPositionTime(), m.varMode[i][k][1]);
								}
								c15.addTerm(-m.I, m.varPrecedence[j][q][i][k]);
								c15.addTerm(-m.I, m.varMode[j][q][1]);
								c15.addTerm(-m.I, m.varMode[i][k][2]);
								c15.addTerm(-m.I, m.varParralel[i][k]);
								for (int y = 0; y < m.nbrJobs; y++) {
									if (y != j && y != i) {
										Job job3 = m.jobs.get(y);
										Integer nbrOpY = job3.getOperations().size();
										for (int v = 0; v < nbrOpY; v++) {
											int a = job3.getOperations().get(v).getWeldingProcess().equals(1) ? 1 : 0;
											c15.addTerm(m.I * a, m.varPrecedence[j][q][y][v]);
											c15.addTerm(m.I * a, m.varPrecedence[y][v][i][k]);
											c15.addTerm(-m.I * a, m.varPrecedence[j][q][i][k]);
										}
									}
								}
								m.C15[j][q][i][k] = cplex.addGe(c15,
										job2.getOperations().get(k).getProcessingTime() - job.getDueDate() - 4 * m.I + m.LT + 3 * m.MT,
										"C15(" + j + "," + q + "," + i + "," + k + ")");
							}
						}
					}
				}
				if (q > 0) {
					IloLinearNumExpr c2 = cplex.linearNumExpr();
					c2.addTerm(1.0, m.varWeld[j][q]);
					c2.addTerm(-1.0, m.varWeld[j][q - 1]);
					if(!job.getWeldingHistory().equals(3)) {
						c2.addTerm(-job.getPositionTime(), m.varMode[j][q - 1][1]);
					}
					c2.addTerm(-3 * m.MT, m.varParralel[j][q - 1]);
					m.C2[j][q] = cplex.addGe(c2, job.getOperations().get(q - 1).getProcessingTime() + m.MT,
							"C2(" + j + "," + q + ")");
				}
				IloLinearNumExpr c7 = cplex.linearNumExpr();
				for (int o = 0; o < m.nbrModes; o++) {
					c7.addTerm(1.0, m.varMode[j][q][o]);
				}
				m.C7[j][q] = cplex.addEq(c7, 1, "C7(" + j + "," + q + ")");
				m.C8[j][q] = cplex.addEq(m.varMode[j][q][2],
						job.getOperations().get(q).getWeldingProcess().equals(2) ? 1 : 0, "C8(" + j + "," + q + ")");
				
				m.C17[j][q] = cplex.addLe(m.varParralel[j][q],
						job.getOperations().get(q).getWeldingProcess().equals(2) ? 1 : 0, "C17(" + j + "," + q + ")");
				
				if (q == (nbrOpJ - 1)) {
					IloLinearNumExpr c14 = cplex.linearNumExpr();
					c14.addTerm(1.0, m.varDelay[j]);
					c14.addTerm(-1.0, m.varWeld[j][q]);
					if(q > 0 || !job.getWeldingHistory().equals(3)) {
						c14.addTerm(-job.getPositionTime(), m.varMode[j][q][1]);
					}
					m.C14[j][q] = cplex.addGe(c14, job.getOperations().get(q).getProcessingTime() - job.getDueDate() + m.LT + m.MT,
							"C14(" + j + "," + q + ")");
				}
			}
			IloLinearNumExpr c6 = cplex.linearNumExpr();
			double totalMTC6 = 0;
			IloLinearNumExpr c9 = cplex.linearNumExpr();
			c6.addTerm(1.0, m.varWeld[j][0]);
			m.C11[j] = new IloRange[m.nbrLoadStations][][];
			m.C12[j] = new IloRange[m.nbrLoadStations][][][][];
			for (int l = 0; l < m.nbrLoadStations; l++) {
				c9.addTerm(1.0, m.varLoad[j][l]);
				c6.addTerm(-1.0, m.varBegin[j][l]);
				c6.addTerm(- m.MT * (job.getLoadingHistory().equals(l+1)? 1 : 0) * (job.getWeldingHistory() > 0? 1 : 0), m.varRemove[j][l]);
				totalMTC6 += (m.MT * (job.getLoadingHistory().equals(l+1)? 1 : 0) * (job.getWeldingHistory() > 0? 1 : 0));
				m.C11[j][l] = new IloRange[m.nbrJobs][];
				m.C12[j][l] = new IloRange[m.nbrJobs][][][];
				for (int i = 0; i < m.nbrJobs; i++) {
					if (i != j) {
						Job job2 = m.jobs.get(i);
						Integer nbrOpI = job2.getOperations().size();
						m.C11[j][l][i] = new IloRange[nbrOpI];
						m.C12[j][l][i] = new IloRange[nbrOpI][m.nbrJobs][];
						for (int q = 0; q < nbrOpI; q++) {
							IloLinearNumExpr c11 = cplex.linearNumExpr();
							c11.addTerm(1.0, m.varBegin[j][l]);
							c11.addTerm(-1.0, m.varWeld[i][q]);
							if(q > 0 || !job2.getWeldingHistory().equals(3)) {
								c11.addTerm(-job2.getPositionTime(), m.varMode[i][q][1]);
							}
							c11.addTerm(-m.I, m.varPrecedence[i][0][j][0]);
							c11.addTerm(-m.I, m.varLoad[i][l]);
							c11.addTerm(-m.I, m.varLoad[j][l]);
							m.C11[j][l][i][q] = cplex.addGe(c11,
									job2.getOperations().get(q).getProcessingTime() - 3 * m.I + 2 * m.LT + m.MT,
									"C11(" + j + "," + l + "," + i + "," + q + ")");

							if (q == (nbrOpI - 1)) {
								for (int y = 0; y < m.nbrJobs; y++) {
									if (y != j && y != i) {
										Job job3 = m.jobs.get(y);
										Integer nbrOpY = job3.getOperations().size();
										m.C12[j][l][i][q][y] = new IloRange[nbrOpY];
										for (int k = 0; k < nbrOpY; k++) {
											IloLinearNumExpr c12 = cplex.linearNumExpr();
											c12.addTerm(1.0, m.varBegin[j][l]);
											c12.addTerm(-m.I, m.varPrecedence[i][0][j][0]);
											c12.addTerm(-m.I, m.varLoad[i][l]);
											c12.addTerm(-m.I, m.varLoad[j][l]);
											c12.addTerm(-m.I, m.varPrecedence[i][q][y][k]);
											c12.addTerm(-m.I, m.varMode[i][q][1]);
											c12.addTerm(-m.I, m.varMode[y][k][2]);
											if(k > 0 || !job3.getWeldingHistory().equals(3)) {
												c12.addTerm(-job3.getPositionTime(), m.varMode[y][k][1]);
											}
											c12.addTerm(-1.0, m.varWeld[y][k]);
											c12.addTerm(-m.I, m.varParralel[y][k]);
											for (int v = 0; v < m.nbrJobs; v++) {
												if (v != y && v != i && y != j) {
													Job job4 = m.jobs.get(v);
													Integer nbrOpV = job4.getOperations().size();
													for (int t = 0; t < nbrOpV; t++) {
														int a = job4.getOperations().get(t).getWeldingProcess().equals(1) ? 1 : 0;
														c12.addTerm(m.I * a, m.varPrecedence[i][q][v][t]);
														c12.addTerm(m.I * a, m.varPrecedence[v][t][y][k]);
														c12.addTerm(-m.I * a, m.varPrecedence[i][q][y][k]);
													}
												}
											}
											m.C12[j][l][i][q][y][k] = cplex.addGe(c12,job3.getOperations().get(k).getProcessingTime() - 7 * m.I + 2 * m.LT + 3 * m.MT, 
													"C12(" + j + "," + l + "," + i + "," + q + "," + y + "," + k + ")");
										}
									}
								}
							}
						}
						if(m.jobs.get(j).getLoadingHistory().equals(l+1)) {												
							IloLinearNumExpr c20 = cplex.linearNumExpr();
							c20.addTerm(1, m.varRemove[j][l]);
							c20.addTerm(- m.I, m.varLoad[i][l]);
							c20.addTerm(- m.I, m.varPrecedence[i][0][j][0]);
							m.C20[j][l][i] = cplex.addGe(c20, 1 - 2 * m.I, "C20(" + j + "," + l + "," + i + ")");
						}
						if(m.jobs.get(i).getLoadingHistory().equals(l+1)) {	
							IloLinearNumExpr c19 = cplex.linearNumExpr();
							c19.addTerm(1, m.varBegin[j][l]);
							c19.addTerm(- m.I, m.varLoad[j][l]);
							m.C19[j][l][i] = cplex.addGe(c19, 2*m.LT + m.MT * m.jobs.get(i).getWeldingHistory() -m.I, "C19(" + j + "," + l + "," + i + ")");
						}
					}
				}
				IloLinearNumExpr c18 = cplex.linearNumExpr();
				c18.addTerm(1, m.varBegin[j][l]);
				c18.addTerm(- m.I, m.varLoad[j][l]);
				for (int l2 = 0; l2 < m.nbrLoadStations; l2++) {
					c18.addTerm(-2*m.LT - m.MT * m.jobs.get(j).getWeldingHistory(), m.varRemove[j][l2]);
				}
				m.C18[j][l] = cplex.addGe(c18, -m.I, "C18(" + j + "," + l + ")");
			}
			System.out.println("Total pour le job " + j +" : "+totalMTC6);
			m.C6[j] = cplex.addGe(c6, m.MT - totalMTC6, "C6(" + j + ")");
			m.C9[j] = cplex.addEq(c9, 1, "C9(" + j + ")");
			m.C10[j] = cplex.addGe(m.varLoad[j][1], job.isSize() ? 1 : 0, "C10(" + j + ")");
		}
	}

	/**
	 * Création de la fonction objectif
	 * 
	 * @param cplex
	 * @param m
	 * @throws IloException
	 */
	private void createObjectifFunction(IloCplex cplex, Model m) throws IloException {
		IloLinearNumExpr objectif = cplex.linearNumExpr();
		for (IloNumVar D : m.varDelay) {
			objectif.addTerm(1.0 / m.nbrJobs, D);
		}
		cplex.addMinimize(objectif);
	}

	/**
	 * Afficher en détail les résultats de la résoltion
	 * 
	 * @param cplex
	 * @param m
	 * @throws IloException
	 */
	private void displayResult(IloCplex cplex, Model m) throws IloException {
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		cplex.output().println("Solution status = " + cplex.getStatus());
		cplex.output().println("Solution value  = " + cplex.getObjValue());
		cplex.output().println("Infinity value  = " + m.I);
		double[] varDelay = cplex.getValues(m.varDelay);
		System.out.println();
		System.out.println("=== Choix en termes de précédence ===");
		for (int j = 0; j < m.nbrJobs; j++) {
			Job job = m.jobs.get(j);
			for (int q = 0; q < job.getOperations().size(); q++) {
				for (int i = 0; i < m.nbrJobs; i++) {
					if (i != j) {
						Job job2 = m.jobs.get(i);
						for (int k = 0; k < job2.getOperations().size(); k++) {
							System.out.println("- Opération n°" + (q + 1) + ", pièce " + (j + 1) + " avant opération "
									+ (k + 1) + ", pièce " + (i + 1) + " = "
									+ Math.round(cplex.getValue(m.varPrecedence[j][q][i][k])));
						}
					}
				}
			}
		}
		System.out.println();
		for (int j = 0; j < varDelay.length; j++) {
			double[] varWeld = cplex.getValues(m.varWeld[j]);
			double[] varLoad = cplex.getValues(m.varLoad[j]);
			int load = 0;
			for (int l = 0; l < varLoad.length; l++) {
				if (varLoad[l] > 0) {
					load = l;
				}
				
			}
			double begin = cplex.getValue(m.varBegin[j][load]);
			System.out.println("=== Pièce n°" + (j + 1) + " (chargée sur la station n°" + (load + 1) + " à "
					+ df.format(begin) + " minutes) ===");
			int l=0;
			for(double r : cplex.getValues(m.varRemove[j])){
				l++;
				if(r > 0) {
					System.out.println("Cette pièce a été déchargée de la station "+l);
				}
			}
			System.out.println("Date de réalisation des opérations :");
			for (int q = 0; q < varWeld.length; q++) {
				double[] varMode = cplex.getValues(m.varMode[j][q]);
				int mode = 0;
				for (int o = 0; o < varMode.length; o++) {
					if (varMode[o] > 0) {
						mode = o;
					}
				}
				double parrallele = cplex.getValue(m.varParralel[j][q]);
				System.out.println("- Opération n°" + (q + 1) + " (executée en mode " + (mode + 1) + (parrallele > 0? " en parrallèle" : "") + ") a débutée à "
						+ df.format(varWeld[q]));
			}
			System.out.println("=> Ainsi le retard de la pièce n°" + (j + 1) + " = " + df.format(varDelay[j]));
			System.out.println();
		}

	}

	/**
	 * Solve the problem with Cplex
	 * 
	 * @param problem
	 */
	public void solve(Instance problem) {
		try {
			IloCplex cplex = new IloCplex();
			Model m = new Model(problem, true);
			createVariables(cplex, m);
			createObjectifFunction(cplex, m);
			createConstraints(cplex, m);
			cplex.exportModel("lpex1.lp");
			if (cplex.solve()) {
				displayResult(cplex, m);
			}
			cplex.end();
		} catch (IloException e) {
			e.printStackTrace();
		}
	}
}
