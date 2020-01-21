import ilog.concert.*;
import ilog.cplex.*;

public class version1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		modelConfig();
	}
	
	public static void modelConfig() {
	 //define parameters - subjects
		int n = 11;
		String [] subj = {"Math", "Language", "Science", "Art", "Social-Studies", "Phys-Ed", "French", "Music", "Drama", "Away", "Prep"};
		int subjects = 9;
		int prepSubject = n-1;
		int awaySubject = n-2;
		
	//define parameters - teachers
		int n2 = 16;
		double [] FTE = {1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,0.2,0.2,0.6,0.2,1.0,1.0};
		int frenchTeachlb = 14;
		int frenchTeachub = 15;
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
		int subjectRange = 10;
		
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
		
	//available time matrix
		int [][] availableTime = {{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,1,1,1},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,1,1,1,0,0,0,0,0,0},
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,1,1,1},
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}};
		
	//time periods matrix
	/*	int [][] availableTime = new int[n2][n4];
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
		}*/
				
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
					}else if((k>=3 && k<=10) && j==13 && i==3) {
						rewards[k][j][i]=200;
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
		int blocks = 3;
		
		try {
			//define the model
			IloCplex cplex = new IloCplex();
	
			//variables
			
			//x is the binary location variable
			IloNumVar [][][][] x = new IloNumVar[n][n2][n3][n4];
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
			
			//y is binary for teacher to subject to cohort assigment
			IloNumVar [][][] y = new IloNumVar[subjects][n2][teachingCohort];
			for(int i = 0;i<subjects;i++) {
				for(int j=0;j<n2;j++){
					for(int k=0;k<teachingCohort;k++) {
							String varName = "y" + i+j+k; 
							y[i][j][k] =cplex.boolVar(varName);
					}
				}				
			}
			
			//Slack Variable for Prep even distribution
			IloIntVar [][] u = new IloIntVar[n2][numDays];
			
			for(int a=0;a<n2;a++) {
				for(int b=0;b<numDays;b++) {
					String varName = "u" + a+b;
					u[a][b] = cplex.intVar(0, Integer.MAX_VALUE, varName);
				}
			}
			
			//Surplus Variable for Prep even distribution
			IloIntVar [][] v = new IloIntVar[n2][numDays];
			
			for(int a = 0; a<n2;a++) {
				for(int b = 0;b<numDays; b++) {
					String varName = "v"+a+b;
					v[a][b] = cplex.intVar(0, Integer.MAX_VALUE, varName);
				}
			}
			
			//Slack Variable for science even distribution
			IloIntVar [][] u2 = new IloIntVar[teachingCohort][numDays];
			
			for(int a=0;a<teachingCohort;a++) {
				for(int b=0;b<numDays;b++) {
					String varName = "u2" + a+b;
					u2[a][b] = cplex.intVar(0, Integer.MAX_VALUE, varName);
				}
			}
			
			//Surplus Variable for science even distribution
			IloIntVar [][] v2 = new IloIntVar[teachingCohort][numDays];
			
			for(int a = 0; a<teachingCohort;a++) {
				for(int b = 0;b<numDays; b++) {
					String varName = "v2"+a+b;
					v2[a][b] = cplex.intVar(0, Integer.MAX_VALUE, varName);
				}
			}
			
			//Slack Variable for gym even distribution
			IloIntVar [][] u3 = new IloIntVar[teachingCohort][numDays];
			
			for(int a=0;a<teachingCohort;a++) {
				for(int b=0;b<numDays;b++) {
					String varName = "u3" + a+b;
					u3[a][b] = cplex.intVar(0, Integer.MAX_VALUE, varName);
				}
			}
			
			//Surplus Variable for gym even distribution
			IloIntVar [][] v3 = new IloIntVar[teachingCohort][numDays];
			
			for(int a = 0; a<teachingCohort;a++) {
				for(int b = 0;b<numDays; b++) {
					String varName = "v3"+a+b;
					v3[a][b] = cplex.intVar(0, Integer.MAX_VALUE, varName);
				}
			}
			
			//Slack Variable for social studies even distribution
			IloIntVar [][] u4 = new IloIntVar[teachingCohort][numDays];
			
			for(int a=0;a<teachingCohort;a++) {
				for(int b=0;b<numDays;b++) {
					String varName = "u4" + a+b;
					u4[a][b] = cplex.intVar(0, Integer.MAX_VALUE, varName);
				}
			}
			
			//Surplus Variable for social studies even distribution
			IloIntVar [][] v4 = new IloIntVar[teachingCohort][numDays];
			
			for(int a = 0; a<teachingCohort;a++) {
				for(int b = 0;b<numDays; b++) {
					String varName = "v4"+a+b;
					v4[a][b] = cplex.intVar(0, Integer.MAX_VALUE, varName);
				}
			}
			
			//Indicator variable for Primary Classes Language back to back
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
					objective.addTerm(-pjd, u[j][d]); //prep even distribution 
					objective.addTerm(-pjd, v[j][d]);
				}
			}
			
			//objective for slack and surplus weights for even distribution
			for(int k=0;k<teachingCohort;k++) {
				for(int d=0;d<numDays;d++) {
					objective.addTerm(-150, v2[k][d]); //science
					objective.addTerm(-150, v3[k][d]); //gym
					objective.addTerm(-150, v4[k][d]); //social studies
				}
			}
						
			cplex.addMaximize(objective);
			
//define constraints
			
//one teacher assigned to subject per cohort part1
IloLinearNumExpr[][] assign2 = new IloLinearNumExpr[subjects][teachingCohort];
for(int i=0;i<subjects;i++) {
	for(int k=0; k<teachingCohort;k++) {	
		assign2[i][k] = cplex.linearNumExpr();
		
		for(int j=0; j<n2; j++) {
			assign2[i][k].addTerm(1, y[i][j][k]);
		}
	}
}

for(int i=0;i<subjects;i++) {
	for(int k=0;k<teachingCohort;k++) {
		cplex.addEq(assign2[i][k], 1);
	}
}

//one teacher assigned to subject part2
IloLinearNumExpr[][][][] assign4 = new IloLinearNumExpr[subjects][n2][teachingCohort][n4];

for(int i=0;i<subjects;i++) {
	for(int j=0; j<n2; j++) {
		for(int k=0; k<teachingCohort;k++) {
			for(int t=0; t<n4;t++) {
				assign4[i][j][k][t] = cplex.linearNumExpr();
				assign4[i][j][k][t].addTerm(1, x[i][j][k][t]);
				assign4[i][j][k][t].addTerm(-1, y[i][j][k]);
				
			}
		}
	}
}

for(int i=0;i<subjects;i++) {
	for(int j=0; j<n2; j++) {
		for(int k=0;k<teachingCohort;k++) {
			for(int t =0;t<n4;t++) {
				cplex.addLe(assign4[i][j][k][t], 0);
			}
		}
	}
}
	

//assignment1- only 1 teacher assigned to a cohort and subject at a time-fixed constr
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
//teacher can only teach one subject/class at a time - if you're assigned to teach, cant be assigned away/prep and vice versa
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

//assignment2 - at every time, each cohort needs only one teacher and one subject
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


//assignment3- each cohort assigned to 12 time periods
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
IloLinearNumExpr[] lang1 = new IloLinearNumExpr[primary];
IloLinearNumExpr[] lang2 = new IloLinearNumExpr[primary];
IloLinearNumExpr[] lang3 = new IloLinearNumExpr[primary];
IloLinearNumExpr[] lang4 = new IloLinearNumExpr[primary];
IloLinearNumExpr[] lang5 = new IloLinearNumExpr[primary];

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

for(int k = 0; k<teachingCohort; k++){
	sci1[k] = cplex.linearNumExpr();
	//sci2[k] = cplex.linearNumExpr();
	for(int j = 0; j<n2;j++){
		for(int t = 0; t<n4;t++){
			sci1[k].addTerm(lengtht[t], x[2][j][k][t]);
			}
		}
	}

for(int k = 0; k<teachingCohort;k++){
	cplex.addGe(sci1[k], 80);
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
	cplex.addGe(art[k], 40);
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
	cplex.addGe(soc[k],80);
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
	cplex.addGe(phys[k], 80);
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
	cplex.addEq(french2[k], 200);
	}

//constraint 14 pt2- primary cant have french
IloLinearNumExpr [] noFrench = new IloLinearNumExpr[primary];

for(int k =0; k<primary;k++){
	noFrench[k] = cplex.linearNumExpr();
	for(int j=0;j<n2; j++){
		for(int t=0;t<n4;t++){
			noFrench[k].addTerm(lengtht[t], x[6][j][k][t]);
			}
		}
	}

for(int k=0;k<primary;k++){
	cplex.addEq(noFrench[k],0);
	}

//constraint 20, music
IloLinearNumExpr [] music = new IloLinearNumExpr[teachingCohort];

for(int k=0;k<teachingCohort;k++){
	music[k] = cplex.linearNumExpr();
	for(int j=0;j<n2;j++){
		for(int t=0;t<n4;t++){
			music[k].addTerm(lengtht[t], x[7][j][k][t]);
			}
		}
	}

for(int k=0;k<teachingCohort;k++){
	cplex.addGe(music[k], 40);
}

//constraint 21, drama
IloLinearNumExpr [] drama = new IloLinearNumExpr[teachingCohort];

for(int k=0;k<teachingCohort;k++){
	drama[k] = cplex.linearNumExpr();
	for(int j=0;j<n2;j++){
		for(int t=0;t<n4;t++){
			drama[k].addTerm(lengtht[t], x[8][j][k][t]);
			}
		}
	}

for(int k=0;k<teachingCohort;k++){
	cplex.addGe(drama[k], 40);
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

//slack and surplus variables for prep time dist
IloLinearNumExpr [][] slack1 = new IloLinearNumExpr [n2][numDays];
IloLinearNumExpr [][] surplus1 = new IloLinearNumExpr [n2][numDays];

for(int j = 0; j<n2; j++) {
	for(int d = 0; d<numDays;d++) {
		slack1[j][d] = cplex.linearNumExpr();
		surplus1[j][d] = cplex.linearNumExpr();
		
		slack1[j][d].addTerm(1, u[j][d]);
		surplus1[j][d].addTerm(1, v[j][d]);
	}
}

for(int j = 0; j<n2; j++) {
	for(int d = 0; d<numDays;d++) {
		cplex.addGe(slack1[j][d], 0);
		cplex.addGe(surplus1[j][d], 0);
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

//contraint 22- have to have french less than or eqaul to once a day
IloLinearNumExpr[] fr1 = new IloLinearNumExpr[frenchNum];
IloLinearNumExpr[] fr2 = new IloLinearNumExpr[frenchNum];
IloLinearNumExpr[] fr3 = new IloLinearNumExpr[frenchNum];
IloLinearNumExpr[] fr4 = new IloLinearNumExpr[frenchNum];
IloLinearNumExpr[] fr5 = new IloLinearNumExpr[frenchNum];

for(int k=0;k<frenchNum;k++) {
	fr1[k] = cplex.linearNumExpr();
	fr2[k] = cplex.linearNumExpr();
	fr3[k] = cplex.linearNumExpr();
	fr4[k] = cplex.linearNumExpr();
	fr5[k] = cplex.linearNumExpr();
	
	for(int j=0;j<frenchTeach;j++) {
		for(int t =day1s;t<day1f;t++) {
			fr1[k].addTerm(1, x[6][j+frenchTeachlb][k+frenchCohortlb][t]);
			}
		for(int t=day2s; t<day2f;t++){
			fr2[k].addTerm(1, x[6][j+frenchTeachlb][k+frenchCohortlb][t]);
			}
		for(int t=day3s; t<day3f;t++){
			fr3[k].addTerm(1, x[6][j+frenchTeachlb][k+frenchCohortlb][t]);
			}
		for(int t = day4s; t<day4f;t++){
			fr4[k].addTerm(1, x[6][j+frenchTeachlb][k+frenchCohortlb][t]);
			}
		for(int t=day5s; t<day5f; t++){
			fr5[k].addTerm(1, x[6][j+frenchTeachlb][k+frenchCohortlb][t]);
			}
		}
	}

for(int k=0;k<frenchNum;k++) {
	cplex.addLe(fr1[k],1);
	cplex.addLe(fr2[k],1);
	cplex.addLe(fr3[k],1);
	cplex.addLe(fr4[k],1);
	cplex.addLe(fr5[k],1);
}

//contraint 23- have to have language at least once a day for junior/intermediate
IloLinearNumExpr[] lg1 = new IloLinearNumExpr[frenchNum];
IloLinearNumExpr[] lg2 = new IloLinearNumExpr[frenchNum];
IloLinearNumExpr[] lg3 = new IloLinearNumExpr[frenchNum];
IloLinearNumExpr[] lg4 = new IloLinearNumExpr[frenchNum];
IloLinearNumExpr[] lg5 = new IloLinearNumExpr[frenchNum];

for(int k=0;k<frenchNum;k++) {
	lg1[k] = cplex.linearNumExpr();
	lg2[k] = cplex.linearNumExpr();
	lg3[k] = cplex.linearNumExpr();
	lg4[k] = cplex.linearNumExpr();
	lg5[k] = cplex.linearNumExpr();
	
	for(int j=0;j<n2;j++) {
		for(int t =day1s;t<day1f;t++) {
			lg1[k].addTerm(1, x[1][j][k+frenchCohortlb][t]);
			}
		for(int t=day2s; t<day2f;t++){
			lg2[k].addTerm(1, x[1][j][k+frenchCohortlb][t]);
			}
		for(int t=day3s; t<day3f;t++){
			lg3[k].addTerm(1, x[1][j][k+frenchCohortlb][t]);
			}
		for(int t = day4s; t<day4f;t++){
			lg4[k].addTerm(1, x[1][j][k+frenchCohortlb][t]);
			}
		for(int t=day5s; t<day5f; t++){
			lg5[k].addTerm(1, x[1][j][k+frenchCohortlb][t]);
			}
		}
	}

for(int k=0;k<frenchNum;k++) {
	cplex.addGe(lg1[k],1);
	cplex.addGe(lg2[k],1);
	cplex.addGe(lg3[k],1);
	cplex.addGe(lg4[k],1);
	cplex.addGe(lg5[k],1);
}

//constraint 24 minimize # of times cohorts have gym, science, and social studies on the same day- not included in pull request- keep for testing

IloLinearNumExpr [] gym1 = new IloLinearNumExpr[teachingCohort];
IloLinearNumExpr [] gym2 = new IloLinearNumExpr[teachingCohort];
IloLinearNumExpr [] gym3 = new IloLinearNumExpr[teachingCohort];
IloLinearNumExpr [] gym4 = new IloLinearNumExpr[teachingCohort];
IloLinearNumExpr [] gym5 = new IloLinearNumExpr[teachingCohort];
IloLinearNumExpr [] sc1 = new IloLinearNumExpr[teachingCohort];
IloLinearNumExpr [] sc2 = new IloLinearNumExpr[teachingCohort];
IloLinearNumExpr [] sc3 = new IloLinearNumExpr[teachingCohort];
IloLinearNumExpr [] sc4 = new IloLinearNumExpr[teachingCohort];
IloLinearNumExpr [] sc5 = new IloLinearNumExpr[teachingCohort];
IloLinearNumExpr [] ss1 = new IloLinearNumExpr[teachingCohort];
IloLinearNumExpr [] ss2 = new IloLinearNumExpr[teachingCohort];
IloLinearNumExpr [] ss3 = new IloLinearNumExpr[teachingCohort];
IloLinearNumExpr [] ss4 = new IloLinearNumExpr[teachingCohort];
IloLinearNumExpr [] ss5 = new IloLinearNumExpr[teachingCohort];


for(int k=0;k<teachingCohort;k++){

	gym1[k] = cplex.linearNumExpr();
	gym2[k] = cplex.linearNumExpr();
	gym3[k] = cplex.linearNumExpr();
	gym4[k] = cplex.linearNumExpr();
	gym5[k] = cplex.linearNumExpr();
	sc1[k] = cplex.linearNumExpr();
	sc2[k] = cplex.linearNumExpr();
	sc3[k] = cplex.linearNumExpr();
	sc4[k] = cplex.linearNumExpr();
	sc5[k] = cplex.linearNumExpr();
	ss1[k] = cplex.linearNumExpr();
	ss2[k] = cplex.linearNumExpr();
	ss3[k] = cplex.linearNumExpr();
	ss4[k] = cplex.linearNumExpr();
	ss5[k] = cplex.linearNumExpr();
	
	for(int t =day1s;t<day1f;t++) {
		for(int j=0; j<n2; j++) {
			gym1[k].addTerm(1, x[5][j][k][t]);
			sc1[k].addTerm(1, x[2][j][k][t]);
			ss1[k].addTerm(1, x[4][j][k][t]);
		}
	}
	gym1[k].addTerm(1, u3[k][0]);
	gym1[k].addTerm(-1, v3[k][0]);
	sc1[k].addTerm(1, u2[k][0]);
	sc1[k].addTerm(-1, v2[k][0]);
	ss1[k].addTerm(1, u4[k][0]);
	ss1[k].addTerm(-1, v4[k][0]);
	
	for(int t =day2s;t<day2f;t++) {
		for(int j=0; j<n2; j++) {
			gym2[k].addTerm(1, x[5][j][k][t]);
			sc2[k].addTerm(1, x[2][j][k][t]);
			ss2[k].addTerm(1, x[4][j][k][t]);
		}
	}
	gym2[k].addTerm(1, u3[k][1]);
	gym2[k].addTerm(-1, v3[k][1]);
	sc2[k].addTerm(1, u2[k][1]);
	sc2[k].addTerm(-1, v2[k][1]);
	ss2[k].addTerm(1, u4[k][1]);
	ss2[k].addTerm(-1, v4[k][1]);
	
	for(int t =day3s;t<day3f;t++) {
		for(int j=0; j<n2; j++) {
			gym3[k].addTerm(1, x[5][j][k][t]);
			sc3[k].addTerm(1, x[2][j][k][t]);
			ss3[k].addTerm(1, x[4][j][k][t]);
		}
	}
	gym3[k].addTerm(1, u3[k][2]);
	gym3[k].addTerm(-1, v3[k][2]);
	sc3[k].addTerm(1, u2[k][2]);
	sc3[k].addTerm(-1, v2[k][2]);
	ss3[k].addTerm(1, u4[k][2]);
	ss3[k].addTerm(-1, v4[k][2]);
	
	for(int t =day4s;t<day4f;t++) {
		for(int j=0; j<n2; j++) {
			gym4[k].addTerm(1, x[5][j][k][t]);
			sc4[k].addTerm(1, x[2][j][k][t]);
			ss4[k].addTerm(1, x[4][j][k][t]);
		}
	}
	gym4[k].addTerm(1, u3[k][3]);
	gym4[k].addTerm(-1, v3[k][3]);
	sc4[k].addTerm(1, u2[k][3]);
	sc4[k].addTerm(-1, v2[k][3]);
	ss4[k].addTerm(1, u4[k][3]);
	ss4[k].addTerm(-1, v4[k][3]);
	
	for(int t =day5s;t<day5f;t++) {
		for(int j=0; j<n2; j++) {
			gym5[k].addTerm(1, x[5][j][k][t]);
			sc5[k].addTerm(1, x[2][j][k][t]);
			ss5[k].addTerm(1, x[4][j][k][t]);
		}
	}
	gym5[k].addTerm(1, u3[k][4]);
	gym5[k].addTerm(-1, v3[k][4]);
	sc5[k].addTerm(1, u2[k][4]);
	sc5[k].addTerm(-1, v2[k][4]);
	ss5[k].addTerm(1, u4[k][4]);
	ss5[k].addTerm(-1, v4[k][4]);
 }
	
for(int k=0;k<teachingCohort;k++){
	cplex.addEq(gym1[k], 1);
	cplex.addEq(gym2[k], 1);
	cplex.addEq(gym3[k], 1);
	cplex.addEq(gym4[k], 1);
	cplex.addEq(gym5[k], 1);	
	cplex.addEq(sc1[k], 1);
	cplex.addEq(sc2[k], 1);
	cplex.addEq(sc3[k], 1);
	cplex.addEq(sc4[k], 1);
	cplex.addEq(sc5[k], 1);
	cplex.addEq(ss1[k], 1);
	cplex.addEq(ss2[k], 1);
	cplex.addEq(ss3[k], 1);
	cplex.addEq(ss4[k], 1);
	cplex.addEq(ss5[k], 1);
}

//slack and surplus variables for sci, gym, social studies even distribution
IloLinearNumExpr [][] slack3 = new IloLinearNumExpr [teachingCohort][numDays];
IloLinearNumExpr [][] surplus3 = new IloLinearNumExpr [teachingCohort][numDays];
IloLinearNumExpr [][] slack2 = new IloLinearNumExpr [teachingCohort][numDays];
IloLinearNumExpr [][] surplus2 = new IloLinearNumExpr [teachingCohort][numDays];
IloLinearNumExpr [][] slack4 = new IloLinearNumExpr [teachingCohort][numDays];
IloLinearNumExpr [][] surplus4 = new IloLinearNumExpr [teachingCohort][numDays];

for(int k = 0; k<teachingCohort; k++) {
	for(int d = 0; d<numDays;d++) {
		slack3[k][d] = cplex.linearNumExpr();
		surplus3[k][d] = cplex.linearNumExpr();
		slack2[k][d] = cplex.linearNumExpr();
		surplus2[k][d] = cplex.linearNumExpr();
		slack4[k][d] = cplex.linearNumExpr();
		surplus4[k][d] = cplex.linearNumExpr();
		
		slack3[k][d].addTerm(1, u3[k][d]);
		surplus3[k][d].addTerm(1, v3[k][d]);
		slack2[k][d].addTerm(1, u2[k][d]);
		surplus2[k][d].addTerm(1, v2[k][d]);
		slack4[k][d].addTerm(1, u4[k][d]);
		surplus4[k][d].addTerm(1, v4[k][d]);
	}
}

for(int k = 0; k<teachingCohort; k++) {
	for(int d = 0; d<numDays;d++) {
		cplex.addGe(slack3[k][d], 0);
		cplex.addGe(surplus3[k][d], 0);
		cplex.addGe(slack2[k][d], 0);
		cplex.addGe(surplus2[k][d], 0);
		cplex.addGe(slack4[k][d], 0);
		cplex.addGe(surplus4[k][d], 0);
	}
}

cplex.exportModel("lpex1.lp");
//tolerance
cplex.setParam(IloCplex.Param.MIP.Tolerances.MIPGap, 8.0e-2);
//solve 
if(cplex.solve()) {

	System.out.println("Objective = "+cplex.getObjValue());
	System.out.println("Teacher, Cohort, Subject, Period, Day");
		
	int math = 0;
	int lan = 0;
	int art1 = 0;
	int soc1 = 0;
	int sci = 0;
	int gym = 0;
	int mus = 0;
	int dram = 0;
	int french3 = 0;
	int totalMin = 0;
	
	
	for(int t =0; t<n4;t++) {
		for(int i =0;i<n;i++) {
			for(int j =0; j<n2;j++) {
				for(int k=0;k<n3;k++) {
					if((cplex.getValue(x[i][j][k][t])) >0.5) {
						System.out.print("Teacher: "+ j +" Cohort: " + k + " Subject: " + subj[i] +" Period: "+ (t+1));
						if(t==0 || t== 1 || t==2|| t==3|| t==4|| t==5) {
						System.out.println(" Day 1");
						}else if(t==6|| t==7|| t==8|| t==9|| t==10|| t==11) {
							System.out.println(" Day 2");
						}else if(t==12|| t==13|| t==14|| t==15|| t==16|| t==17) {
							System.out.println(" Day 3");
						}else if(t==18|| t==19|| t==20|| t==21|| t==22|| t==23) {
							System.out.println(" Day 4");
						}else {
							System.out.println(" Day 5");
						}
						if(i==0 && k==8) {
							
							math = math + lengtht[t];
						}
						if(i==1 && k==8) {
							lan = lan+ lengtht[t];
						}
						if(i==2 && k==8) {
							sci = sci+lengtht[t];
						}
						if(i==3 && k==8) {
							art1 = art1 + lengtht[t];
						}
						if(i==4 && k==8) {
							soc1 = soc1 + lengtht[t];
						}
						if(i==5 && k==8) {
							gym = gym+lengtht[t];
						}
						if(i==6 && k==8) {
							french3 = french3 + lengtht[t];
						}
						if(i==7 && k==8) {
							mus = mus + lengtht[t];
						}
						if(i==8 && k==8) {
							dram = dram + lengtht[t];
						}
						
					}
				}
			}
		}
		
	}
	totalMin = math+lan+sci+art1+soc1+gym+french3+mus+dram;
	
	System.out.println("Math: " + math);
	System.out.println("Language: " + lan);
	System.out.println("Science: " + sci);
	System.out.println("Art: " + art1);
	System.out.println("Social Studies: " + soc1);
	System.out.println("Gym: " + gym);
	System.out.println("French: " + french3);
	System.out.println("Music: " + mus);
	System.out.println("Drama: " + drama);
	System.out.println("Total Teaching Minutes: " + totalMin);
	
	
}
else {
	System.out.println("Model not solved");
	cplex.exportModel("lpex1.lp");
}

}
	
		catch (IloException exc) {
			exc.printStackTrace();
		}
 
	}
	
}
