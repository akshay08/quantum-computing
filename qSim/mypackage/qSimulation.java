package mypackage;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.*;

import mypackage.QGates;
import mypackage.PauliMatrix;

public class qSimulation {
	
	static int n;
	static PauliMatrix a[][];
	static int sign[];
	
	public static void main(String args[]) {

		// TODO: take input from file
		// Input circuit
		try {
			BufferedReader br = new BufferedReader(new FileReader("Input.txt"));
			
			n = Integer.parseInt(br.readLine().trim());  // number of qubits
			
			//String line1 = "H(1), H(2), H(3)";
			//String line2 = "C(1-3), S(2)";

			//ArrayList<String> lines = new ArrayList<String>();
			//lines.add(line1);
			//lines.add(line2);

			// I-Z matrix n X n
			a = new PauliMatrix[n][n];
			sign = new int[n];
			for (int i = 0; i < n; i++) {
				sign[i] = 1;
				for (int j = 0; j < n; j++) {
					if (i == j) {
						a[i][j] = PauliMatrix.Z;
					} else {
						a[i][j] = PauliMatrix.I;
					}
				}
			}

			// ArrayLists containing the positions where the gates are applied
			ArrayList<Integer> H = new ArrayList<Integer>();
			ArrayList<Integer> S = new ArrayList<Integer>();
			ArrayList<Integer[]> C = new ArrayList<Integer[]>();

			ArrayList<String> gates = new ArrayList<String>();
			String line;
			display();
			while ((line = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, ",");

				while (st.hasMoreElements()) {
					gates.add(st.nextElement().toString().toUpperCase().trim());
				}
				System.out.println("\n" + gates);

				for (String gate : gates) {
					//System.out.println(gate);
					if (gate.startsWith(QGates.H.name())) {
						String temp = gate.substring(gate.indexOf("(") + 1,
								gate.indexOf(")"));
						H.add(Integer.parseInt(temp));

					}
					if (gate.startsWith(QGates.S.name())) {
						String temp = gate.substring(gate.indexOf("(") + 1,
								gate.indexOf(")"));
						S.add(Integer.parseInt(temp));

					}
					if (gate.startsWith(QGates.C.name())) {
						String t = gate.substring(gate.indexOf("(") + 1,
								gate.indexOf(")"));
						//System.out.println(t);
						Integer c[] = new Integer[2];
						c[0] = (Integer.parseInt(t.substring(0, t.indexOf("-"))));
						c[1] = (Integer.parseInt(t.substring(t.indexOf("-") + 1)));
						C.add(c);

					}
					//System.out.println(H);
					//System.out.println(S);
					//System.out.println(C);
				}

				// TODO : stuff in the matrix

				// implement Hadamard
				for (int h : H) {
					hadamard(h);
				}

				// implement Phase Gate
				for (int s : S) {
					phase(s);
				}

				// implement C-NOT
				for (Integer[] c : C) {
					cnot(c[0], c[1]);
				}
				
				// display the state of stabilizer matrix
				display();
				
				
				// System.out.println(gates);
				gates.removeAll(gates);
				// System.out.println(H);
				H.removeAll(H);
				// System.out.println(S);
				S.removeAll(S);
				// System.out.println(C);
				C.removeAll(C);
				// System.out.println("\n");
			}
		}
		catch (FileNotFoundException e) {
			System.out.println("File Not Found!");
		}
		catch (IOException e) {
			;
		}
	}

	static void display() {
		for (int i = 0; i < n; i++) {
			if (sign[i] == 1) {
				System.out.print("+ ");
			}
			else if (sign[i] == -1) {
				System.out.print("- ");
			}
			for (int j = 0; j < n; j++) {
				System.out.print(a[i][j] + " ");
			}
			System.out.print("\n");
		}
	}
	
	static void hadamard(int h) {
		for (int i = 0; i < n; i++) {
			if (a[i][h - 1] == PauliMatrix.I) {
				a[i][h - 1] = PauliMatrix.I;
			} else if (a[i][h - 1] == PauliMatrix.X) {
				a[i][h - 1] = PauliMatrix.Z;
			} else if (a[i][h - 1] == PauliMatrix.Y) {
				a[i][h - 1] = PauliMatrix.Y;
				sign[i] *= -1;
			} else if (a[i][h - 1] == PauliMatrix.Z) {
				a[i][h - 1] = PauliMatrix.X;
			}
		}
	}
	
	static void phase(int s) {
		for (int i = 0; i < n; i++) {
			if (a[i][s - 1] == PauliMatrix.I) {
				a[i][s - 1] = PauliMatrix.I;
			} else if (a[i][s - 1] == PauliMatrix.X) {
				a[i][s - 1] = PauliMatrix.Y;
			} else if (a[i][s - 1] == PauliMatrix.Y) {
				a[i][s - 1] = PauliMatrix.X;
				sign[i] *= -1;
			} else if (a[i][s - 1] == PauliMatrix.Z) {
				a[i][s - 1] = PauliMatrix.Z;
			}
		}
	}

	static void cnot(int c1, int c2) {
		for (int i = 0; i < n; i++) {
			if (a[i][c1 - 1] == PauliMatrix.I) {
				if (a[i][c2 - 1] == PauliMatrix.I) {
					a[i][c1 - 1] = PauliMatrix.I;
					a[i][c2 - 1] = PauliMatrix.I;
				} else if (a[i][c2 - 1] == PauliMatrix.X) {
					a[i][c1 - 1] = PauliMatrix.I;
					a[i][c2 - 1] = PauliMatrix.X;
				} else if (a[i][c2 - 1] == PauliMatrix.Y) {
					a[i][c1 - 1] = PauliMatrix.Z;
					a[i][c2 - 1] = PauliMatrix.Y;
				} else if (a[i][c2 - 1] == PauliMatrix.Z) {
					a[i][c1 - 1] = PauliMatrix.Z;
					a[i][c2 - 1] = PauliMatrix.Z;
				}	
			}
			else if (a[i][c1 - 1] == PauliMatrix.X) {
				if (a[i][c2 - 1] == PauliMatrix.I) {
					a[i][c1 - 1] = PauliMatrix.X;
					a[i][c2 - 1] = PauliMatrix.X;
				} else if (a[i][c2 - 1] == PauliMatrix.X) {
					a[i][c1 - 1] = PauliMatrix.X;
					a[i][c2 - 1] = PauliMatrix.I;
				} else if (a[i][c2 - 1] == PauliMatrix.Y) {
					a[i][c1 - 1] = PauliMatrix.Y;
					a[i][c2 - 1] = PauliMatrix.Z;
				} else if (a[i][c2 - 1] == PauliMatrix.Z) {
					a[i][c1 - 1] = PauliMatrix.Y;
					a[i][c2 - 1] = PauliMatrix.Y;
					sign[i] *= -1;
				}	
			}
			else if (a[i][c1 - 1] == PauliMatrix.Y) {
				if (a[i][c2 - 1] == PauliMatrix.I) {
					a[i][c1 - 1] = PauliMatrix.Y;
					a[i][c2 - 1] = PauliMatrix.X;
				} else if (a[i][c2 - 1] == PauliMatrix.X) {
					a[i][c1 - 1] = PauliMatrix.Y;
					a[i][c2 - 1] = PauliMatrix.I;
				} else if (a[i][c2 - 1] == PauliMatrix.Y) {
					a[i][c1 - 1] = PauliMatrix.X;
					a[i][c2 - 1] = PauliMatrix.Z;
					sign[i] *= -1;
				} else if (a[i][c2 - 1] == PauliMatrix.Z) {
					a[i][c1 - 1] = PauliMatrix.X;
					a[i][c2 - 1] = PauliMatrix.Y;
				}	
			}
			else if (a[i][c1 - 1] == PauliMatrix.Z) {
				if (a[i][c2 - 1] == PauliMatrix.I) {
					a[i][c1 - 1] = PauliMatrix.Z;
					a[i][c2 - 1] = PauliMatrix.I;
				} else if (a[i][c2 - 1] == PauliMatrix.X) {
					a[i][c1 - 1] = PauliMatrix.Z;
					a[i][c2 - 1] = PauliMatrix.X;
				} else if (a[i][c2 - 1] == PauliMatrix.Y) {
					a[i][c1 - 1] = PauliMatrix.I;
					a[i][c2 - 1] = PauliMatrix.Y;
				} else if (a[i][c2 - 1] == PauliMatrix.Z) {
					a[i][c1 - 1] = PauliMatrix.I;
					a[i][c2 - 1] = PauliMatrix.Z;
				}	
			}
		}
	}
}
