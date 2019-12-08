import ilog.concert.*;
import ilog.cplex.*;

public class version1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static void modelConfig() {
	 //define parameters - subjects
		int n = 9;
		String [] subj = {"Math", "Language", "Science", "Art", "Social-Studies", "Phys-Ed", "French", "Away", "Prep"};
		int prepSubject = n;
		int awaySubject = (n-1);
		
	//define parameters - teachers
		int n2 = 11;
		double [] FTE = {1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,0.7,1.0};
		int frenchInd = n2-1;
	
	//define parameters - cohorts
		int n3 = 8;
		int teachingCohort = n3-2;
		int primaryUb = n3-5;
		int frenchCohortlb = n3-4;
		int frenchCohortub = n3-2;
		int frenchNum = frenchCohortub - frenchCohortlb;
		//int classUb = n3-2;
		int prepCohort = n3;
		int awayCohort = n3-1;
		
	//define parameters - time
		int n4 = 30;
		int basePrepTime = 240;
		int totalTime = 1500;
		
	//initializing arrays
		double [] totalTeacherMin = new double [n2];
		double [] prep = new double [n2];
		double [] teachMin = new double [n2];
		
	//fill above arrays
		for (int j = 0; j<=n2-1;j++) {
			totalTeacherMin[j] = FTE[j]*totalTime;
			prep[j] = FTE[j]*basePrepTime; //prep time allocation
			teachMin[j] = totalTeacherMin[j]-prep[j]; //teaching minute allocation
		}
		
	//time periods matrix
		int [][] availableTime = new int[n2][n4];
		//fill availableTime matrix
		
		//first nine rows are 1
		for(int a = 0; a<=8;a++) {
			for(int b=0;b<=29;b++) {
				availableTime[a][b]=1;
			}
		}
		
		//last 9 entries of 10th row are 0
		for(int c=0;c<=20;c++) {
			availableTime[8][c]=1;
		}
		
		//last row is all 1's
		for(int d=0;d<=29;d++) {
			availableTime[10][d]=1;
		}
				
	//time periods array
		int [] lengtht = {40,60,50,50,60,40,40,60,50,50,60,40,40,60,50,50,60,40,40,60,50,50,60,40,40,60,50,50,60,40};
		
	//defining initial reward matrix
		int [][][] rewards = new int [teachingCohort][n2][n-2];
		
		//fill the initial reward matrix
		for (int k=0; k<=teachingCohort-1;k++) {
			for(int j=0;j<=n2-1;j++) {
				for(int i=0;i<=n-3;i++) {
					if(k==j) {
						rewards[k][j][i]=100;
					}else if((k==1 || k==2 || k==3) && j==7) {
						//teacher 7 teachers art specialty
						if(i==4) {
							rewards[k][j][i]=200;
						}else {
							rewards[k][j][i]=10;
						}
					}else if(( k==1 || k==2 || k==3)&& j==7) {
						//teacher 8 teaches gym specialty
						if(i==6) {
							rewards[k][j][i]=200;
						}else {
							rewards[k][j][i]=10;
						}
					}else if (( k==4|| k==5 || k==6)&& j==9) {
						//9 is a generalist
						rewards [k][j][i]=10;
					}else {
						rewards[k][j][i]=0;
					}
				}
			}
		}
			
		//misc parameters
		int pjd = 50; //penalty value
		int gymCap = 2;
		
		try {
			//define the model
			IloCplex cplex = new IloCplex();
			
			//ranges - subjects
			IloRange r1 = cplex.addRange(1, n);
			IloRange subjects = cplex.addRange(1, 7);
			IloRange subjectRange = cplex.addRange(n,n);
			
			//French indicator
			IloRange french = cplex.addRange(frenchInd, n2);
			
			//ranges - teachers
			IloRange r2 = cplex.addRange(1, n2);
			
			//ranges - cohorts
			IloRange r3 = cplex.addRange(1,n3);
			IloRange teaching_class = cplex.addRange(1, teachingCohort);
			IloRange primary = cplex.addRange(1, primaryUb);
			IloRange frenchCohorts = cplex.addRange(frenchCohortlb, frenchCohortub);
			IloRange classGroup = cplex.addRange(1,teachingCohort);
			IloRange cohortRange = cplex.addRange(n3, n3);
			
			//ranges - time
			IloRange day1 = cplex.addRange(1, 6);
			IloRange numDays = cplex.addRange(1, 5);
			IloRange day2 = cplex.addRange(7, 12);
			IloRange day3 = cplex.addRange(13, 18);
			IloRange day4 = cplex.addRange(19, 24);
			IloRange day5 = cplex.addRange(25, 30);
			IloRange r4 = cplex.addRange(1, n4);
			IloRange blockCount = cplex.addRange(1,15);
		
			
			//variables
			
			//x is the binary location variable, lol this is v questionable
			IloIntVar [][][][] x = new IloIntVar[n][][][];
			for(int i = 0;i<n;i++) {
				x[i] = new IloIntVar[n2][][];
				for(int j=0; j<x[i].length;j++) {
					x[i][j] = new IloIntVar[n3][];
					for(int k =0;k<x[i][j].length; k++) {
					//	x[i][j][k] = new IloIntVar[n4];
						x[i][j][k] = cplex.boolVarArray(n4);
					}
				}
			}
			
			//y binary decision variable
			IloIntVar [][][] y = new IloIntVar[7][][];
			for(int i = 0;i<7;i++) {
				y[i] = new IloIntVar[n2][];
				for(int j=0;j<y[i].length;j++) {
					//y[i][j] = new IloIntVar[teachingCohort];
					y[i][j] = cplex.boolVarArray(teachingCohort);
				}
			}
			
			//slack variable for prep
			IloNumVar [][] u1 = new IloNumVar[n2][];
			for (int i=0; i<n2;i++) {
				u1[i] = cplex.numVarArray(5, 0, Double.MAX_VALUE);
			}
			
			//surplus variable for prep
			IloNumVar [][] v1 = new IloNumVar[n2][];
			for (int i=0; i<n2;i++) {
				v1[i] = cplex.numVarArray(5, 0, Double.MAX_VALUE);
			}
			
			//binary a variable
			IloIntVar [][] a = new IloIntVar[primaryUb][];
			for(int i = 0;i<primaryUb;i++) {
				a[i] = cplex.boolVarArray(15);
			}
			
			//binary b variable
			IloIntVar [][] b = new IloIntVar[0][];
			b[0] = cplex.boolVarArray(n4);
		
			//slack variable for prep time objective to schedule prep **weird because index of 1 only
			IloNumVar [][] u2 = new IloNumVar[0][];
			u2[0]= cplex.numVarArray(n4, 0, Double.MAX_VALUE);
			
			//surplus variable, same as above
			IloNumVar [][] v2 = new IloNumVar[0][];
			v2[0]= cplex.numVarArray(n4, 0, Double.MAX_VALUE);
			
			IloNumVar [] u3 = new IloNumVar[teachingCohort];
			u3 = cplex.numVarArray(teachingCohort, 0, Double.MAX_VALUE);
			
			IloNumVar [] u4 = new IloNumVar[teachingCohort];
			u4 = cplex.numVarArray(teachingCohort, 0, Double.MAX_VALUE);
			
			IloNumVar [] u5 = new IloNumVar[teachingCohort];
			u5 = cplex.numVarArray(teachingCohort, 0, Double.MAX_VALUE);
			
			IloNumVar [] u6 = new IloNumVar[teachingCohort];
			u6 = cplex.numVarArray(teachingCohort, 0, Double.MAX_VALUE);
		
			//define objective
			IloLinearNumExpr objective = cplex.linearNumExpr();
			for(int i =0;i<(n-2);i++) {
				for(int j=0;j<n2;j++) {
					for(int k=0;k<teachingCohort;k++) {
						for(int t=0;t<n4;t++) {
							objective.addTerm(rewards[k][j][i], x[i][j][k][t]);
						}
					}
				}
			}
			
			for(int j=0;j<n2;j++) {
				for(int d=0;d<5;d++) {
					objective.addTerm(-pjd, u1[j][d]);
				}
			}
			
			for(int j=0;j<n2;j++) {
				for(int d=0;d<5;d++) {
					objective.addTerm(pjd, v1[j][d]);
				}
			}
						
			for(int t=0;t<n4;t++) {
				objective.addTerm(-50, u2[0][t]);
			}
			
			for(int t=0;t<n4;t++) {
				objective.addTerm(50, v2[0][t]);
			}
			
			for(int k=0;k<teachingCohort;k++) {
				objective.addTerm(-50, u3[k]);
				objective.addTerm(-50, u4[k]);
				objective.addTerm(-50, u5[k]);
				objective.addTerm(-50, u6[k]);
			}
			
			cplex.addMaximize(objective);
			
			//define constraints
//assignment 1
IloLinearNumExpr[][][] assign1 = new IloLinearNumExpr[(n-2)][teachingCohort][n4];

for(int i=0;i<(n-2);i++) {
	for(int k=0; k<n2;k++) {
		for(int t=0; t<n4;t++) {
			assign1[i][k][t] = cplex.linearNumExpr();
			
			for(int j=0; j<n2;j++) {
				assign1[i][k][t].addTerm(1, x[i][j][k][t]);
			}
		}
	}
}
	
for(int i=0;i<(n-2);i++) {
	for(int k=0;k<n2;k++) {
		for(int t =0;t<n4;t++) {
			cplex.addLe(assign1[i][k][t], 1);
		}
	}
}
IloLinearNumExpr[][] constr2 = new IloLinearNumExpr[n2][n4];
IloLinearNumExpr[][] constr3 = new IloLinearNumExpr[n2][n4];
//do not actually need this, add second sum to the first linear expression
//IloLinearNumExpr[][] constr3b = new IloLinearNumExpr[n2][n4];

for(int j=0; j<n2;j++) {
	for(int t=0; t<n4;t++) {
		constr2[j][t] = cplex.linearNumExpr();
		for(int i=0; i<n2;i++) {
			for(int k=0; k<n3;k++) {
				constr2[j][t].addTerm(1, x[i][j][k][t]);
			}
		}
		
		for(int i=0; i<n-2;i++) {
			for(int k=0; k<teachingCohort; k++) {
				constr3[j][t].addTerm(1, x[i][j][k][t]);
			}
		}
		
		for(int i=n;i<n;i++) {
			for(int k=n3;k<n3;k++) {
				constr3[j][t].addTerm(1,x[i][j][k][t]);
			}
		}
	}
}


for(int j=0; j<n2;j++) {
	for(int t=0;t<n4;t++) {
		cplex.addEq(constr2[j][t], 1);
		cplex.addEq(constr3[j][t],availableTime[j][t]);
	}
}

//assignment #2
IloLinearNumExpr[][] constr4 = new IloLinearNumExpr[teachingCohort][n4];

for(int k=0;k<teachingCohort;k++) {
	for(int t = 0; t<n4;t++) {
		constr4[k][t] = cplex.linearNumExpr();
		for(int i=0;i<n-2;i++) {
			for(int j=0;j<n2;j++) {
				constr4[k][t].addTerm(1, x[i][j][k][t]);
			}
		}
	}
}

for(int k=0; k<teachingCohort;k++) {
	for(int t=0;t<n4;t++) {
		cplex.addEq(constr4[k][t], 1);
	}
}


//assignment #3
IloLinearNumExpr[] assign3 = new IloLinearNumExpr[teachingCohort];

for(int k=0;k<teachingCohort;k++) {
	assign3[k] = cplex.linearNumExpr();
	for(int i=0;i<n-2;i++) {
		for(int j=0;j<n2;j++) {
			for(int t=0;t<n4;t++) {
				assign3[k].addTerm(1, x[i][j][k][t]);				
			}
		}
	}
}

for(int k=0;k<teachingCohort;k++) {
	cplex.addEq(assign3[k],30);
}

//constraint 7, math
IloLinearNumExpr[] math1 = new IloLinearNumExpr[teachingCohort];
IloLinearNumExpr[] math2 = new IloLinearNumExpr[teachingCohort];
IloLinearNumExpr[] math3 = new IloLinearNumExpr[teachingCohort];
IloLinearNumExpr[] math4 = new IloLinearNumExpr[teachingCohort];
IloLinearNumExpr[] math5 = new IloLinearNumExpr[teachingCohort];

for(int k=0;k<teachingCohort;k++) {
math1[k] = cplex.linearNumExpr();
 for(int j=0;j<n2;j++) {
	 for(int a1 =1;a1<=6;a1++) {
		 math1[k].addTerm(lengtht[a1], x[0][j][k][a1]);
     }
 for(int b1=7; b1<=12;b1++){
	 math2[k].addTerm(lengtht[b1], x[0][j][k][b1]);
	 }
 for(int c=13; c<=18;c++){
	 math3[k].addTerm(lengtht[c], x[0][j][k][c]);
	 }
 for(int d = 19; d<=24;d++){
	 math4[k].addTerm(lengtht[d], x[0][j][k][d]);
	 }
 for(int e=25; e<=30; e++){
	 math5[k].addTerm(lengtht[e], x[0][j][k][e]);
	 }
 }
 
}

for(int k=0;k<teachingCohort;k++) {
cplex.addEq(math1[k],60);
cplex.addEq(math2[k],60);
cplex.addEq(math3[k],60);
cplex.addEq(math4[k],60);
cplex.addEq(math5[k],60);
}

//constraint 8, language for primary cohorts
IloLinearNumExpr[] lang1 = new IloLinearNumExpr[primaryUb];
IloLinearNumExpr[] lang2 = new IloLinearNumExpr[primaryUb];
IloLinearNumExpr[] lang3 = new IloLinearNumExpr[primaryUb];
IloLinearNumExpr[] lang4 = new IloLinearNumExpr[primaryUb];
IloLinearNumExpr[] lang5 = new IloLinearNumExpr[primaryUb];

for(int k=0;k<primaryUb;k++) {
	lang1[k] = cplex.linearNumExpr();
	lang2[k] = cplex.linearNumExpr();
	lang3[k] = cplex.linearNumExpr();
	lang4[k] = cplex.linearNumExpr();
	lang5[k] = cplex.linearNumExpr();
	
	for(int j=0;j<n2;j++) {
		for(int a1 =1;a1<=6;a1++) {
			lang1[k].addTerm(lengtht[a1], x[1][j][k][a1]);
			}
		for(int b1=7; b1<=12;b1++){
			lang2[k].addTerm(lengtht[b1], x[1][j][k][b1]);
			}
		for(int c=13; c<=18;c++){
			lang3[k].addTerm(lengtht[c], x[1][j][k][c]);
			}
		for(int d = 19; d<=24;d++){
			lang4[k].addTerm(lengtht[d], x[1][j][k][d]);
			}
		for(int e=25; e<=30; e++){
			lang5[k].addTerm(lengtht[e], x[1][j][k][e]);
			}
		}
	}

for(int k=0;k<teachingCohort;k++) {
	cplex.addEq(lang1[k],100);
	cplex.addEq(lang2[k],100);
	cplex.addEq(lang3[k],100);
	cplex.addEq(lang4[k],100);
	cplex.addEq(lang5[k],100);
	}

//constraint 9, language for french applicable cohorts
IloLinearNumExpr[] lang6 = new IloLinearNumExpr[frenchNum];

for(int k=0;k<frenchNum;k++){
	lang6[k] = cplex.linearNumExpr();
	for(int j=0;j<n2;j++) {
		for(int t =0;t<n4;t++) {
			lang6[k].addTerm(lengtht[t], x[1][j][k][t]);
			}
		}
	}

for(int k = 0; k<frenchNum;k++){
	cplex.addGe(lang6[k], 300);
	}

//constraint 10, science 
IloLinearNumExpr[] sci = new IloLinearNumExpr[teachingCohort];

for(int k = 0; k<teachingCohort; k++){
	sci[k] = cplex.linearNumExpr();
	for(int j = 0; j<n2;j++){
		for(int t = 0; t<n4;t++){
			sci[k].addTerm(lengtht[t], x[2][j][k][t]);
			}
		}
	}

for(int k = 0; k<teachingCohort;k++){
	cplex.addGe(sci[k], 100);
	cplex.addLe(sci[k], 150);
	}

//constraint 11, art
IloLinearNumExpr[] art = new IloLinearNumExpr[teachingCohort];

for(int k = 0; k<teachingCohort; k++){
	art[k] = cplex.linearNumExpr();
	for(int j=0;j<n2;j++){
		for(int t=0;t<n4;t++){
			art[k].addTerm(lengtht[t], x[3][j][k][t]);
			}
		}
	}

for(int k = 0;k<teachingCohort; k++){
	cplex.addGe(art[k], 300);
	}

//constraint 12, social studies
IloLinearNumExpr [] soc = new IloLinearNumExpr[teachingCohort];

for(int k=0; k<teachingCohort;k++){
	soc[k] = cplex.linearNumExpr();
	for(int j=0; j<n2;j++){
		for(int t=0;t<n4;t++){
			soc[k].addTerm(lengtht[t], x[4][j][k][t]);
			}
		}
	}

for(int k = 0;k<teachingCohort;k++){
	cplex.addGe(soc[k],100);
	}

//constraint 13, phys-ed
IloLinearNumExpr [] phys = new IloLinearNumExpr[teachingCohort];

for(int k=0;k<teachingCohort;k++){
	phys[k] = cplex.linearNumExpr();
	for(int j=0;j<n2;j++){
		for(int t=0;t<n4;t++){
			phys[k].addTerm(lengtht[t], x[5][j][k][t]);
			}
		}
	}

for(int k=0;k<teachingCohort;k++){
	cplex.addGe(phys[k], 150);
	cplex.addLe(phys[k],300);
	}

//constraint 14, french for applicable classes
IloLinearNumExpr [] french2 = new IloLinearNumExpr[frenchNum];

for(int k =0; k<frenchNum;k++){
	french2[k] = cplex.linearNumExpr();
	for(int j=0;j<frenchNum; j++){
		for(int t=0;t<n4;t++){
			french2[k].addTerm(lengtht[t], x[6][j][k][t]);
			}
		}
	}

for(int k=0;k<frenchNum;k++){
	cplex.addGe(french2[k], 200);
	}


//constraint 15, prep
IloLinearNumExpr [] prepCon = new IloLinearNumExpr[n2];

for(int j = 0;j<n2;j++){
	prepCon[j] = cplex.linearNumExpr();
	for(int t=0;t<n4;t++){
		prepCon[j].addTerm(lengtht[t], x[prepSubject][j][prepCohort][t]);
		}
	}

for(int j=0;j<n2;j++){
	cplex.addGe(prepCon[j], prep[j]);
	}

//constraint 16, teaching mins 
IloLinearNumExpr [] teach = new IloLinearNumExpr[n2];

for(int j=0;j<n2;j++){
	teach[j] = cplex.linearNumExpr();
	for(int t=0;t<n2;t++){
		for(int i=0;i<n;i++){
			for(int k=0;k<teachingCohort;k++){
				teach[j].addTerm(lengtht[t], x[i][j][k][t]);
				}
			}
		}
	}

for(int j=0;j<n2;j++){
	cplex.addLe(teach[j], teachMin[j]);
	}

//constraint 17, gym capacity
IloLinearNumExpr [] gymCapCon = new IloLinearNumExpr[n4];

for(int t=0;t<n4;t++){
	gymCapCon[t] = cplex.linearNumExpr();
	for(int j=0;j<n2;j++){
		for(int k=0;k<n3;k++){
			gymCapCon[t].addTerm(1, x[5][j][k][t]);
			}
		}
	}

for(int t=0;t<n4;t++){
	cplex.addLe(gymCapCon[t], gymCap);
	}

//constraint 18, prep time objective
IloLinearNumExpr [] prep1 = new IloLinearNumExpr[n2];
IloLinearNumExpr [] prep2 = new IloLinearNumExpr[n2];
IloLinearNumExpr [] prep3 = new IloLinearNumExpr[n2];
IloLinearNumExpr [] prep4 = new IloLinearNumExpr[n2];
IloLinearNumExpr [] prep5 = new IloLinearNumExpr[n2];

for(int j=0;j<n2;j++){
	prep1[j] = cplex.linearNumExpr();
	prep2[j] = cplex.linearNumExpr();
	prep3[j] = cplex.linearNumExpr();
	prep4[j] = cplex.linearNumExpr();
	prep5[j] = cplex.linearNumExpr();
	for(int a1 =1;a1<=6;a1++) {
		prep1[j].addTerm(1, x[1][j][prepCohort][a1]);
   }
	for(int b1=7; b1<=12;b1++){
		prep2[j].addTerm(lengtht[b1], x[1][j][prepCohort][b1]);
		}
	for(int c=13; c<=18;c++){
		prep3[j].addTerm(lengtht[c], x[1][j][prepCohort][c]);
		}
	for(int d = 19; d<=24;d++){
		prep4[j].addTerm(lengtht[d], x[1][j][prepCohort][d]);
		}
	for(int e=25; e<=30; e++){
		prep5[j].addTerm(lengtht[e], x[1][j][prepCohort][e]);
		}
	}

//constraint 19, language for primary has to be back to back

}
		
		catch (IloException exc) {
			exc.printStackTrace();
		}
 
	}
	
}
