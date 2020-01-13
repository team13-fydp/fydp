import ilog.concert.*;
import ilog.cplex.*;

public class version1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		modelConfig();
	}
	
	public static void modelConfig() {
	 //define parameters - subjects
		int n = 9;
		String [] subj = {"Math", "Language", "Science", "Art", "Social-Studies", "Phys-Ed", "French", "Away", "Prep"};
		int subjects = 7;
		int prepSubject = n-1;
		int awaySubject = n-2;
		
	//define parameters - teachers
		int n2 = 17;
		double [] FTE = {1.0,1.0,1.0,1.0,0.7,1.0,1.0,1.0,1.0,1.0,1.0,0.2,0.2,0.6,0.2,0.3,1.0};
		int frenchTeachlb = 15;
		int frenchTeachub = 16;
		int frenchTeach = 2;
	
	//define parameters - cohorts
		int n3 = 13;
		int teachingCohort = n3-2;
		int primaryUb = 3;
		int frenchCohortlb = 3;
		int frenchCohortub = 11;
		int frenchNum = 8;
		//int classUb = n3-2;
		int prepCohort = n3-1;
		int awayCohort = n3-2;
		
		int cohortRange = 12;
		int subjectRange = 8;
		
	//define parameters - time
		int n4 = 30;
		
	//period ranges for each day
		int day1s = 0;
		int day1f = 6;
				
		int day2s = 6;
		int day2f = 12;
				
		int day3s = 12;
		int day3f = 18;
				
		int day4s = 18;
		int day4f = 24;
				
		int day5s = 24;
		int day5f = 30;	
		
		int basePrepTime = 240;
		double totalTime = 1500;
		
	//initializing arrays
		double [] totalTeacherMin = new double [n2];
		double [] prep = new double [n2];
		double [] teachMin = new double [n2];
		
	//fill above arrays
		for (int j = 0; j<n2;j++) {
			totalTeacherMin[j] = FTE[j]*totalTime;
			prep[j] = FTE[j]*basePrepTime; //prep time allocation
			teachMin[j] = totalTeacherMin[j]-prep[j]; //teaching minute allocation
		}
		
	//time periods matrix
		int [][] availableTime = new int[n2][n4];
		//fill availableTime matrix
		
		//first nine rows are 1
		for(int a = 0; a<=3;a++) {
			for(int b=0;b<=29;b++) {
				availableTime[a][b]=1;
			}
		}
		
		for(int c=0;c<=20;c++) {
			availableTime[4][c]=1;
		}
		
		for(int a = 5; a<=10;a++) {
			for(int b=0;b<=29;b++) {
				availableTime[a][b]=1;
			}
		}
		
		for(int c=24;c<=29;c++) {
			availableTime[11][c]=1;
		}
		
		for(int c = 18; c<=23;c++) {
			availableTime[12][c] = 1;
		}
		
		for(int c = 0; c<=8;c++) {
			availableTime[13][c] = 1; 
		}
		for(int c = 21; c<=29;c++) {
			availableTime[13][c] = 1; 
		}
		
		for(int c = 0; c<=5;c++) {
			availableTime[14][c] = 1; 
		}
		for(int c = 0; c<=8;c++) {
			availableTime[15][c] = 1; 
		}
		for(int c = 0; c<=29;c++) {
			availableTime[16][c] = 1; 
		}
				
	//time periods array
		int [] lengtht = {40,60,50,50,60,40,40,60,50,50,60,40,40,60,50,50,60,40,40,60,50,50,60,40,40,60,50,50,60,40};
		
	//defining initial reward matrix
		int [][][] rewards = new int [teachingCohort][n2][subjects];
		
		//fill the initial reward matrix
		for (int k=0; k<teachingCohort;k++) {
			for(int j=0;j<n2;j++) {
				for(int i=0;i<subjects;i++) {
					if(k==0 && j==0) {
						rewards[k][j][i]=100;
					}else if (k==1 && j==1) {
						rewards[k][j][i] = 100;		
					}else if (k==2 && j==2) {
						rewards[k][j][i] = 100;
					}else if (k==3 && j==3) {
						rewards[k][j][i] = 100;
					}else if ( k==4 && j==4 && (i==3 || i==1)) {
						rewards[k][j][i] = 200;
					}else if((k>=2 && k<=5) && j==4 && i==6) {
						rewards[k][j][i] =200;
					}else if((k>=3 && k<=5) && j==5 && (i!=1 && i<=4)) {
						rewards[k][j][i] = 100;
					}else if((k>=3 && k<=5) && j ==6 &&i ==4) {
						rewards[k][j][i] = 100;
					}else if(k==6 & j==6 && ( i!= 2 && i<=5)) {
						rewards[k][j][i] = 100;
					}else if(k==0 & j ==6) {
						rewards[k][j][i] = 10;
					}else if(k==7 && j==7 && (( i==1) || i==0 || i ==3 || i==5)) {
						rewards[k][j][i] = 10;
					}else if ((k>=0 && k<=2) && j==7 && i==5) {
						rewards[k][j][i] = 10;
					}else if(k==8 && j ==8 && i<=2) {
						rewards[k][j][i] = 10;
					}else if((k>=0 && k<=2) && j==8 && i==5) {
						rewards[k][j][i]=10;
					}else if(k==9 && j==9 && i!= 4) {
						rewards[k][j][i] = 100;
					}else if((k>=8 && k<=10) && j==10 && i==4) {
						rewards[k][j][i] = 10;
					}else if(k==3 && j==9 && i==3) {
						rewards[k][j][i] = 10;
					}else if((k>=0 && k<=3) && j==11) {
						rewards[k][j][i] = 10;
					}else if((k>=0 && k<=2) && j==12) {
						rewards[k][j][i] = 10;
					}else if((k>=3 && k<=10) && j==13 && i==3) {
						rewards[k][j][i] = 200;
					}else if((k>=3 && k<=6) && j==14 && i==4) {
						rewards[k][j][i] = 200;
					}else if((k>=3 && k<=6) && j== 14 && i==3) {
						rewards[k][j][i] = 200;
					}else if((k>=3 && k<=6) && j==15 && i==6) {
						rewards[k][j][i] = 200;
					}else if((k>=6 && k<=10) && j==16 && i==6) {
						rewards[k][j][i] = 200;
					}
				}
			}
		}

			
		//misc parameters
		int pjd = 50; //penalty value
		int gymCap = 2;
		
		//Number of Days
		int numDays = 5;
		
		//Number of Primary Classes
		int primary = 3;
		int blockCount = 15;
		
		try {
			//define the model
			IloCplex cplex = new IloCplex();
	
			//variables
			
			//x is the binary location variable
			IloIntVar [][][][] x = new IloIntVar[n][n2][n3][n4];
			for(int i = 0;i<n;i++) {
				for(int j=0;j<n2;j++){
					for(int k=0;k<n3;k++) {
						for(int t=0;t<n4;t++) {
							String varName = "x" + i+j+k+t; 
							x[i][j][k][t] =cplex.boolVar(varName);
						}
					}
				}				
			}

			//Slack Variable for Prep
			IloIntVar [][] u = new IloIntVar[n2][numDays];
			
			for(int a=0;a<n2;a++) {
				for(int b=0;b<numDays;b++) {
					String varName = "u" + a+b;
					u[a][b] = cplex.intVar(0, Integer.MAX_VALUE, varName);
				}
			}
			
			//Surplus Variable for Prep
			IloIntVar [][] v = new IloIntVar[n2][numDays];
			
			for(int a = 0; a<n2;a++) {
				for(int b = 0;b<numDays; b++) {
					String varName = "v"+a+b;
					v[a][b] = cplex.intVar(0, Integer.MAX_VALUE, varName);
				}
			}
			
			//Indicator variable for Primary Classes Language
			IloIntVar [][] a = new IloIntVar[primary][blockCount];
			
			for(int i = 0; i<primary;i++) {
				for(int j = 0;j<blockCount;j++) {
					String varName = "a"+i+j;
					a[i][j] = cplex.boolVar(varName);
				}
			}
		
			//define objective
			IloLinearNumExpr objective = cplex.linearNumExpr();
			for(int i =0;i<subjects;i++) {
				for(int j=0;j<n2;j++) {
					for(int k=0;k<teachingCohort;k++) {
						for(int t=0;t<n4;t++) {
							objective.addTerm(rewards[k][j][i], x[i][j][k][t]);
						}
					}
				}
			}
			
			for(int j=0;j<n2;j++) {
				for(int d=0;d<numDays;d++) {
					objective.addTerm(-pjd, u[j][d]);
					objective.addTerm(-pjd, v[j][d]);
				}
			}
						
			cplex.addMaximize(objective);
			
//define constraints
			
//assignment 1
IloLinearNumExpr[][][] assign1 = new IloLinearNumExpr[subjects][teachingCohort][n4];

for(int i=0;i<subjects;i++) {
	for(int k=0; k<teachingCohort;k++) {
		for(int t=0; t<n4;t++) {
			assign1[i][k][t] = cplex.linearNumExpr();
			
			for(int j=0; j<n2;j++) {
				assign1[i][k][t].addTerm(1, x[i][j][k][t]);
			}
		}
	}
}
	
for(int i=0;i<subjects;i++) {
	for(int k=0;k<teachingCohort;k++) {
		for(int t =0;t<n4;t++) {
			cplex.addLe(assign1[i][k][t], 1);
		}
	}
}

//teacher can only teach one subject/ class at a time
IloLinearNumExpr[][] constr2 = new IloLinearNumExpr[n2][n4];
IloLinearNumExpr[][] constr3 = new IloLinearNumExpr[n2][n4];

for(int j=0; j<n2;j++) {
	for(int t=0; t<n4;t++) {
		constr2[j][t] = cplex.linearNumExpr();
		
		
		for(int i=0; i<n;i++) {
			for(int k=0; k<n3;k++) {
				constr2[j][t].addTerm(1, x[i][j][k][t]);
			}
		}
		
		
	}
}

for(int j=0; j<n2;j++) {
	for(int t=0;t<n4;t++) {
		constr3[j][t] = cplex.linearNumExpr();
		
		for(int i=0; i<subjects;i++) {
			for(int k=0; k<teachingCohort; k++) {
				constr3[j][t].addTerm(1, x[i][j][k][t]);
			}
		}
		 constr3[j][t].addTerm(1,x[subjectRange][j][cohortRange][t]);
		
	}
}

for(int j=0; j<n2;j++) {
	for(int t=0;t<n4;t++) {
		cplex.addEq(constr2[j][t], 1);
	
	}
}


for(int j=0; j<n2;j++) {
	for(int t=0;t<n4;t++) {
	
		cplex.addEq(constr3[j][t],availableTime[j][t]);
	}
}

//assignment #2
IloLinearNumExpr[][] constr4 = new IloLinearNumExpr[teachingCohort][n4];

for(int k=0;k<teachingCohort;k++) {
	for(int t = 0; t<n4;t++) {
		constr4[k][t] = cplex.linearNumExpr();
		for(int i=0;i<subjects;i++) {
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
	for(int i=0;i<subjects;i++) {
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

//schedule part time teachers away time
IloLinearNumExpr [] partTime = new IloLinearNumExpr[n2];

for(int j=0;j<n2;j++) {
	partTime[j] = cplex.linearNumExpr();
	for(int t=0;t<n4;t++) {
		partTime[j].addTerm(lengtht[t], x[awaySubject][j][awayCohort][t]);
	
	}
}

for(int j=0;j<n2;j++) {
	double rhs = totalTime - totalTeacherMin[j];
	cplex.addGe(partTime[j], rhs);
}

//constraint 7, math
IloLinearNumExpr[] math1 = new IloLinearNumExpr[teachingCohort];
IloLinearNumExpr[] math2 = new IloLinearNumExpr[teachingCohort];
IloLinearNumExpr[] math3 = new IloLinearNumExpr[teachingCohort];
IloLinearNumExpr[] math4 = new IloLinearNumExpr[teachingCohort];
IloLinearNumExpr[] math5 = new IloLinearNumExpr[teachingCohort];

for(int k=0;k<teachingCohort;k++) {
	math1[k] = cplex.linearNumExpr();
	math2[k] = cplex.linearNumExpr();
	math3[k] = cplex.linearNumExpr();
	math4[k] = cplex.linearNumExpr();
	math5[k] = cplex.linearNumExpr();
	
	for(int j=0;j<n2;j++) {
		for(int a1 =day1s;a1<day1f;a1++) {
			math1[k].addTerm(lengtht[a1], x[0][j][k][a1]);
			}
		for(int b1=day2s; b1<day2f;b1++){
			math2[k].addTerm(lengtht[b1], x[0][j][k][b1]);
			}
		for(int c=day3s; c<day3f;c++){
			math3[k].addTerm(lengtht[c], x[0][j][k][c]);
			}
		for(int d = day4s; d<day4f;d++){
			math4[k].addTerm(lengtht[d], x[0][j][k][d]);
			}
		for(int e=day5s; e<day5f; e++){
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
		for(int a1 =day1s;a1<day1f;a1++) {
			lang1[k].addTerm(lengtht[a1], x[1][j][k][a1]);
			}
		for(int b1= day2s; b1<day2f;b1++){
			lang2[k].addTerm(lengtht[b1], x[1][j][k][b1]);
			}
		for(int c=day3s; c<day3f;c++){
			lang3[k].addTerm(lengtht[c], x[1][j][k][c]);
			}
		for(int d = day4s; d<day4f;d++){
			lang4[k].addTerm(lengtht[d], x[1][j][k][d]);
			}
		for(int e=day5s; e<day5f; e++){
			lang5[k].addTerm(lengtht[e], x[1][j][k][e]);
			}
		}
	}

for(int k=0;k<primaryUb;k++) {
	cplex.addEq(lang1[k],100);
	cplex.addEq(lang2[k],100);
	cplex.addEq(lang3[k],100);
	cplex.addEq(lang4[k],100);
	cplex.addEq(lang5[k],100);
	}

//constraint 9, language for french applicable cohort
IloLinearNumExpr[] lang6 = new IloLinearNumExpr[frenchNum];

for(int k=0;k<frenchNum;k++){
	lang6[k] = cplex.linearNumExpr();
	for(int j=0;j<n2;j++) {
		for(int t =0;t<n4;t++) {
			lang6[k].addTerm(lengtht[t], x[1][j][k+frenchCohortlb][t]);
			}
		}
	}

for(int k = 0; k<frenchNum;k++){
	cplex.addGe(lang6[k], 300);
	}

//constraint 10, science 
IloLinearNumExpr[] sci1 = new IloLinearNumExpr[teachingCohort];
IloLinearNumExpr[] sci2 = new IloLinearNumExpr[teachingCohort];

for(int k = 0; k<teachingCohort; k++){
	sci1[k] = cplex.linearNumExpr();
	sci2[k] = cplex.linearNumExpr();
	for(int j = 0; j<n2;j++){
		for(int t = 0; t<n4;t++){
			sci1[k].addTerm(lengtht[t], x[2][j][k][t]);
			sci2[k].addTerm(lengtht[t], x[2][j][k][t]);

			}
		}
	}

for(int k = 0; k<teachingCohort;k++){
	cplex.addGe(sci1[k], 100);
	cplex.addLe(sci2[k], 150);
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
IloLinearNumExpr [] phys1 = new IloLinearNumExpr[teachingCohort];
IloLinearNumExpr [] phys2 = new IloLinearNumExpr[teachingCohort];

for(int k=0;k<teachingCohort;k++){
	phys1[k] = cplex.linearNumExpr();
	phys2[k] = cplex.linearNumExpr();
	for(int j=0;j<n2;j++){
		for(int t=0;t<n4;t++){
			phys1[k].addTerm(lengtht[t], x[5][j][k][t]);
			phys2[k].addTerm(lengtht[t], x[5][j][k][t]);
			}
		}
	}

for(int k=0;k<teachingCohort;k++){
	cplex.addGe(phys1[k], 150);
	cplex.addLe(phys2[k],200);
	}

//constraint 14, french for applicable classes
IloLinearNumExpr [] french2 = new IloLinearNumExpr[frenchNum];

for(int k =0; k<frenchNum;k++){
	french2[k] = cplex.linearNumExpr();
	for(int j=0;j<frenchTeach; j++){
		for(int t=0;t<n4;t++){
			french2[k].addTerm(lengtht[t], x[6][j+frenchTeachlb][k+frenchCohortlb][t]);
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
	for(int t=0;t<n4;t++){
		for(int i=0;i<subjects;i++){
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
	
	for(int a1 =day1s;a1<day1f;a1++) {
		prep1[j].addTerm(1, x[prepSubject][j][prepCohort][a1]);
		
   }
	prep1[j].addTerm(1, u[j][0]);
	prep1[j].addTerm(-1, v[j][0]);
	
	for(int b1=day2s; b1<day2f;b1++){
		prep2[j].addTerm(1, x[prepSubject][j][prepCohort][b1]);
		}
	prep2[j].addTerm(1, u[j][1]);
	prep2[j].addTerm(-1, v[j][1]);
	
	for(int c=day3s; c<day3f;c++){
		prep3[j].addTerm(1, x[prepSubject][j][prepCohort][c]);
	}
	prep3[j].addTerm(1, u[j][2]);
	prep3[j].addTerm(-1, v[j][2]);
	
	for(int d =day4s; d<day4f;d++){
		prep4[j].addTerm(1, x[prepSubject][j][prepCohort][d]);
	}
	prep4[j].addTerm(1, u[j][3]);
	prep4[j].addTerm(-1, v[j][3]);
	
	for(int e=day5s; e<day5f; e++){
		prep5[j].addTerm(1, x[prepSubject][j][prepCohort][e]);	
	}
	prep5[j].addTerm(1, u[j][4]);
	prep5[j].addTerm(-1, v[j][4]);
}

for(int j=0;j<n2;j++){
	cplex.addEq(prep1[j], 1);
	cplex.addEq(prep2[j], 1);
	cplex.addEq(prep3[j], 1);
	cplex.addEq(prep4[j], 1);
	cplex.addEq(prep5[j], 1);	
}

//slack and surplus variables
IloLinearNumExpr [][] slack = new IloLinearNumExpr [n2][numDays];
IloLinearNumExpr [][] surplus = new IloLinearNumExpr [n2][numDays];

for(int j = 0; j<n2; j++) {
	for(int d = 0; d<numDays;d++) {
		slack[j][d] = cplex.linearNumExpr();
		surplus[j][d] = cplex.linearNumExpr();
		
		slack[j][d].addTerm(1, u[j][d]);
		surplus[j][d].addTerm(1, v[j][d]);
	}
}

for(int j = 0; j<n2; j++) {
	for(int d = 0; d<numDays;d++) {
		cplex.addGe(slack[j][d], 0);
		cplex.addGe(surplus[j][d], 0);
	}
}


//constraint 19, language for primary has to be back to back
IloLinearNumExpr [][] prilan1 = new IloLinearNumExpr[numDays][primary];
IloLinearNumExpr [][] prilan2 = new IloLinearNumExpr[numDays][primary];
IloLinearNumExpr [][] prilan3 = new IloLinearNumExpr[numDays][primary];

for(int d=0; d<numDays;d++) {
	for(int k=0;k<primary;k++) {
		prilan1[d][k] = cplex.linearNumExpr();
		prilan2[d][k] = cplex.linearNumExpr();
		prilan3[d][k] = cplex.linearNumExpr();
		
		for(int j=0;j<n2;j++) {
			prilan1[d][k].addTerm(1,x[1][j][k][(d*6)]);
			prilan1[d][k].addTerm(1, x[1][j][k][1+(d*6)]);
			
			prilan2[d][k].addTerm(1, x[1][j][k][2+(d*6)]);
			prilan2[d][k].addTerm(1, x[1][j][k][3+(d*6)]);
			
			prilan3[d][k].addTerm(1, x[1][j][k][4+(d*6)]);
			prilan3[d][k].addTerm(1,x[1][j][k][5+(d*6)]);
			
		}
	}
}

for(int d=0;d<numDays;d++) {
	for(int k=0;k<primary;k++) {
		cplex.addEq(prilan1[d][k], cplex.prod(2, a[k][3*d]));
		cplex.addEq(prilan2[d][k], cplex.prod(2, a[k][1+(3*d)]));
		cplex.addEq(prilan3[d][k], cplex.prod(2, a[k][2+(3*d)]));
		
	}
}
cplex.exportModel("lpex1.lp");
//solve 
if(cplex.solve()) {

	System.out.println("Objective = "+cplex.getObjValue());
	System.out.println("Teacher, Cohort, Subject, Period, Day");
	
	for(int i =0; i<n4;i++) {
		
	}
	
}
else {
	System.out.println("Model not solved");
	//cplex.exportModel("lpex1.lp");
}



}
	
	
		catch (IloException exc) {
			exc.printStackTrace();
		}
 
	}
	
}
