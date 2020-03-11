import ilog.concert.*;
import ilog.cplex.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.ss.usermodel.FillPatternType;

public class version1 {

	public static void main(String[] args) throws IOException {
		modelConfig();
	}

	public static void modelConfig() throws IOException {
		// read in input data from Excel
		String excelFilePath = "orgA_1920_new_input.xlsx";
		FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
		Workbook workbook = new XSSFWorkbook(inputStream);

		// input sheet 1
		// number of teachers
		int n2 = -1;
		// list of teachers names
		ArrayList<String> teacherNames = new ArrayList<String>(0);
		// teacher allocation
		ArrayList<Double> FTE = new ArrayList<Double>(0);
		// Name of schedule
		String schedule_name = "empty";

		// Individual teacher allocation time
		ArrayList<int[]> availableTime = new ArrayList<int[]>();
		// index of first french teacher
		int first_french_teacher = -1;

		Sheet sheetIndex = workbook.getSheetAt(0);
		// getting schedule title
		int rowStart = sheetIndex.getFirstRowNum();
		Row r = sheetIndex.getRow(rowStart);
		int sheetTitleStart = 4;
		Cell titlecell = r.getCell(sheetTitleStart);
		if (titlecell == null || titlecell.getRichStringCellValue().getString() == "") {
			Date today = Calendar.getInstance().getTime();
			schedule_name = "Generated_Schedule " + today;
		} else {
			schedule_name = titlecell.getRichStringCellValue().getString();
		}

		// get teacher names, allocation, and FTE
		// index of start of teacher matrix
		int teacher_matrix_start = 10;
		// teacher name col
		int teacher_name_col = 0;
		// fulltime col
		int full_time_col = 3;
		// teacher allocation col
		int teacher_allocation_col = 10;
		// french certification col
		int french_certification_col = 2;
		// Creating french and english versions of names
		ArrayList<String> teacherNames_english = new ArrayList<String>(0);
		ArrayList<String> teacherNames_french = new ArrayList<String>(0);
		ArrayList<Double> FTE_english = new ArrayList<Double>(0);
		ArrayList<Double> FTE_french = new ArrayList<Double>(0);
		ArrayList<int[]> availableTime_english = new ArrayList<int[]>();
		ArrayList<int[]> availableTime_french = new ArrayList<int[]>();

		int q = teacher_matrix_start;
		String teacherName = sheetIndex.getRow(q).getCell(teacher_name_col).getStringCellValue();

		boolean isFrenchCertified = false;
		while (teacherName != "") {
			Row currRow = sheetIndex.getRow(q);
			boolean fullTime = false;
			isFrenchCertified = false;

			// alternating signal if it is the first row of a teacher
			// interate over two rows at a time,
			// then do a for loop for each two
			// Check if each row is a french certified teacher first
			String french_test = currRow.getCell(french_certification_col).getStringCellValue();
			if (french_test.matches("(.*)x(.*)")) {
				isFrenchCertified = true;
			}

			int lastColumn = Math.max(currRow.getLastCellNum(), 10);
			for (int k = 0; k < lastColumn; k++) {
				Cell currCell = currRow.getCell(k);

				switch (currCell.getCellType()) {
				case STRING:
					String cell = currCell.getStringCellValue();
					if (k == teacher_name_col) {
						if (isFrenchCertified == true) {
							teacherNames_french.add(currCell.getStringCellValue());
						} else {
							teacherNames_english.add(currCell.getStringCellValue());
						}
					}
					if (k == full_time_col) {

						if (cell.matches("(.*)x(.*)")) {
							// assign full time
							fullTime = true;
							// fill with 1s
							int[] fullTimeTeacher = new int[30];
							for (int m = 0; m < 30; m++) {
								fullTimeTeacher[m] = 1;

							}
							if (isFrenchCertified == true) {
								availableTime_french.add(fullTimeTeacher);
							} else {
								availableTime_english.add(fullTimeTeacher);
							}
						}
					}
					break;
				case NUMERIC:
					break;
				case FORMULA:
					FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
					if (k == teacher_allocation_col) {
						if (fullTime == false) {
							if (isFrenchCertified == true) {
								FTE_french.add(currCell.getNumericCellValue() / 10);
							} else {
								FTE_english.add(currCell.getNumericCellValue() / 10);
							}
						} else {
							if (isFrenchCertified == true) {
								FTE_french.add(1.0);
							} else {
								FTE_english.add(1.0);
							}
						}
					}
				}
			}
			// collecting the available allocated timeslot for part time teachers
			int day1_col = 5;
			int day5_col = 9;
			if (fullTime == false) {
				int[] allocation_times = new int[30];
				int timeslot = 0;
				// iterate over column then row
				for (int col = day1_col; col <= day5_col; col++) {
					for (int row = q; row < q + 2; row++) {
						Row smallRow = sheetIndex.getRow(row);
						String m = smallRow.getCell(col).getStringCellValue();
						if (m.matches("(.*)x(.*)")) {
							allocation_times[timeslot] = 1;
							allocation_times[timeslot + 1] = 1;
							allocation_times[timeslot + 2] = 1;
						}
						timeslot = timeslot + 3;

					}
				}
				if (isFrenchCertified == true) {
					availableTime_french.add(allocation_times);
				} else {
					availableTime_english.add(allocation_times);
				}
			}

			q = q + 2;
			teacherName = sheetIndex.getRow(q).getCell(teacher_name_col).getStringCellValue();

		}
		// index of first french teacher
		first_french_teacher = teacherNames_english.size();
		// append separated arraylists together
		availableTime = availableTime_english;
		teacherNames = teacherNames_english;
		FTE = FTE_english;
		for (int c = 0; c < availableTime_french.size(); c++) {
			availableTime.add(availableTime_french.get(c));
			teacherNames.add(teacherNames_french.get(c));
			FTE.add(FTE_french.get(c));
		}

		n2 = teacherNames.size();
		// output sorted teacher names with french last
		// french teach is the number of french teachers
		// french teach lb is the first index of a french teacher.
		int frenchTeach = n2 - first_french_teacher;
		int frenchTeachlb = n2 - frenchTeach;

		// input sheet 2
		String sheetName = workbook.getSheetName(1);
		XSSFSheet sheet = (XSSFSheet) workbook.getSheet(sheetName);
		int inputColumn = 1;

		// period preference selection
		// declaring rows for step 2
		int periodTimeStartRow = 2;
		int periodTimeEndRow = 3;
		int lengtht[] = new int[30];

		for (int rowNum = periodTimeStartRow; rowNum <= periodTimeEndRow; rowNum++) {
			Row row = sheet.getRow(rowNum);
			String value = row.getCell(inputColumn).getStringCellValue();

			if (value == "") {
				// cell is empty
			} else if (rowNum == periodTimeStartRow) {
				// first period option selected
				lengtht = new int[] { 60, 40, 50, 50, 40, 60, 60, 40, 50, 50, 40, 60, 60, 40, 50, 50, 40, 60, 60, 40,
						50, 50, 40, 60, 60, 40, 50, 50, 40, 60 };
			} else {
				// second period option selected
				lengtht = new int[] { 40, 60, 50, 50, 60, 40, 40, 60, 50, 50, 60, 40, 40, 60, 50, 50, 60, 40, 40, 60,
						50, 50, 60, 40, 40, 60, 50, 50, 60, 40 };
			}
		}
		// subject type array
		String[] subj = { "Math", "Language", "Science", "Art", "Social-Studies", "Phys-Ed", "French", "Music", "Drama",
				"Away", "Prep" };
		// input sheet 2 part 2
		Sheet inputSheet2 = workbook.getSheetAt(1);
		int cohortNameStartRow = 13;
		int cohortNameStartCol = 3;
		int subjects = subj.length - 2;
		ArrayList<String> cohortNames = new ArrayList<String>(0);
		int teachingCohortCountPage2 = 0;
		while (inputSheet2.getRow(cohortNameStartRow).getCell(cohortNameStartCol + teachingCohortCountPage2)
				.getStringCellValue() != "") {
			teachingCohortCountPage2++;
		}

		// read in cohort names and grade names stored as strings
		for (int k = 0; k < teachingCohortCountPage2; k++) {
			if (inputSheet2.getRow(cohortNameStartRow).getCell(cohortNameStartCol + k).getStringCellValue() == "") {
				break;
			}
			cohortNames
					.add(inputSheet2.getRow(cohortNameStartRow).getCell(cohortNameStartCol + k).getStringCellValue());
		}

		double[][][] rewards = new double[teachingCohortCountPage2][n2][subjects];

		int homeRoomTeacherStartRow = 15;
		int homeRoomTeacherStartCol = 3;
		int homeRoomTeacherNameCol = 1;

		String cellHomeRoomCohort;
		int homeRoomReward = 300;

		for (int j = 0; j < n2; j++) {
			// search for the teacher name. order of index is changed due to french being at
			// the bottom
			String teacherNameSearch = inputSheet2.getRow(homeRoomTeacherStartRow + j).getCell(homeRoomTeacherNameCol)
					.getStringCellValue();
			int teacherIndex = 0;
			for (int a = 0; a < teacherNames.size(); a++) {
				if (teacherNameSearch.equals(teacherNames.get(a))) {
					teacherIndex = a;
				}
			}
			for (int k = 0; k < teachingCohortCountPage2; k++) {
				for (int i = 0; i < subjects; i++) {
					cellHomeRoomCohort = inputSheet2.getRow(homeRoomTeacherStartRow + j)
							.getCell(homeRoomTeacherStartCol + k).getStringCellValue();

					if (cellHomeRoomCohort != "") {
						rewards[k][teacherIndex][i] = homeRoomReward;
					}

				}
			}
		}

		// input sheet 3
		Sheet inputSheet3 = workbook.getSheetAt(2);
		String title = inputSheet3.getSheetName();
		int specialtyTeacherStartRow = 12;
		int specialtyTeacherCol = 0;
		int subjectCol = 1;
		int ratingCol = 2;
		int cohortStartCol = 5;
		int numSpecialtyTeach = 0;
		int incr = 0;
		double rating;
		int numCohortsRow = 2;
		int numPrimaryRow = 3;
		int inputColSheet3 = 0;

		int teachingCohort = (int) inputSheet3.getRow(numCohortsRow).getCell(inputColSheet3).getNumericCellValue();
		int primary = (int) inputSheet3.getRow(numPrimaryRow).getCell(inputColSheet3).getNumericCellValue();

		String specialtyTeacherCell = inputSheet3.getRow(specialtyTeacherStartRow).getCell(specialtyTeacherCol)
				.getStringCellValue();
		while (specialtyTeacherCell != "") {
			specialtyTeacherCell = inputSheet3.getRow(specialtyTeacherStartRow + incr).getCell(specialtyTeacherCol)
					.getStringCellValue();
			incr++;
		}
		numSpecialtyTeach = incr - 1;

		String teacherNameSearch;
		String subjectName;
		int teacherIndex = 0;
		int subjectIndex = 0;

		for (int j = 0; j < numSpecialtyTeach; j++) {
			teacherNameSearch = inputSheet3.getRow(specialtyTeacherStartRow + j).getCell(specialtyTeacherCol)
					.getStringCellValue();
			for (int a = 0; a < teacherNames.size(); a++) {
				if (teacherNameSearch.equals(teacherNames.get(a))) {
					teacherIndex = a;
				}
			}

			subjectName = inputSheet3.getRow(specialtyTeacherStartRow + j).getCell(subjectCol).getStringCellValue();

			for (int i = 0; i < subj.length - 2; i++) {
				if (subj[i].equals(subjectName)) {
					subjectIndex = i;
				}
			}
			String cellSpecialtyCohort;
			rating = inputSheet3.getRow(specialtyTeacherStartRow + j).getCell(ratingCol).getNumericCellValue();
			for (int k = 0; k < teachingCohort; k++) {
				cellSpecialtyCohort = inputSheet3.getRow(specialtyTeacherStartRow + j).getCell(cohortStartCol + k)
						.getStringCellValue();
				if (!cellSpecialtyCohort.equals("")) {
					if (rating == 1) {
						rewards[k][teacherIndex][subjectIndex] = 350;

					} else {
						rewards[k][teacherIndex][subjectIndex] = 100;
					}
				}
			}
		}

		// assign colours to cohorts
		int startColour = 40;
		int cohortColours[] = new int[cohortNames.size()];
		int teacherColours[] = new int[teacherNames.size()];
		teacherColours[0]=26;
		teacherColours[1]=31;
		cohortColours[0] = 26;
		cohortColours[1] = 31;
		for (int i = 0; i < cohortColours.length-2; i++) {
			if(startColour+i >= 48) {
				cohortColours[i+2] = startColour + i + 1;
			}
			else {
				cohortColours[i+2] = startColour + i;
			}
		}
		
		for (int i = 0; i < teacherColours.length-2; i++) {
			if(startColour+i >= 48) {
				teacherColours[i+2] = startColour + i + 1;
			}
			else {
				teacherColours[i+2] = startColour + i;
			}
		}

		// define additional parameters
		int n = subj.length;
		int prepSubject = n - 1;
		int awaySubject = n - 2;

		cohortNames.add("Away");
		cohortNames.add("Prep");

		int n3 = cohortNames.size();
		int primaryUb = primary;
		int frenchCohortlb = primaryUb;
		int frenchNum = teachingCohort - frenchCohortlb;
		int prepCohort = n3 - 1;
		int awayCohort = n3 - 2;

		// only use these to get the max index of n and n3 to be used for a constraint
		int cohortRange = n3 - 1;
		int subjectRange = n - 1;
		int n4 = 30; // # of periods
		int pjd = 50; // penalty value
		int gymCap = 2;
		int numDays = 5;
		int blockCount = 15; // Number of period blocks in a day (3/day)

		// period ranges for each day
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

		// initializing arrays for minute availibility
		double[] totalTeacherMin = new double[n2];
		double[] prep = new double[n2];
		double[] teachMin = new double[n2];

		// fill above arrays
		for (int j = 0; j < n2; j++) {
			totalTeacherMin[j] = FTE.get(j) * totalTime;
			prep[j] = FTE.get(j) * basePrepTime; // prep time allocation
			teachMin[j] = totalTeacherMin[j] - prep[j]; // teaching minute allocation
		}

		// start model
		try {
			// define the model
			IloCplex cplex = new IloCplex();
			cplex.setParam(IloCplex.IntParam.Threads, 8);
			cplex.setParam(IloCplex.IntParam.TimeLimit, 3600);

			// variables
			// x is the binary location variable
			IloNumVar[][][][] x = new IloNumVar[n][n2][n3][n4];
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n2; j++) {
					for (int k = 0; k < n3; k++) {
						for (int t = 0; t < n4; t++) {
							String varName = "x" + i + j + k + t;
							x[i][j][k][t] = cplex.boolVar(varName);
						}
					}
				}
			}

			// y is binary for teacher to subject to cohort assigment
			IloNumVar[][][] y = new IloNumVar[subjects][n2][teachingCohort];
			for (int i = 0; i < subjects; i++) {
				for (int j = 0; j < n2; j++) {
					for (int k = 0; k < teachingCohort; k++) {
						String varName = "y" + i + j + k;
						y[i][j][k] = cplex.boolVar(varName);
					}
				}
			}

			// Slack Variable for Prep even distribution
			IloIntVar[][] u = new IloIntVar[n2][numDays];

			for (int a = 0; a < n2; a++) {
				for (int b = 0; b < numDays; b++) {
					String varName = "u" + a + b;
					u[a][b] = cplex.intVar(0, Integer.MAX_VALUE, varName);
				}
			}

			// Surplus Variable for Prep even distribution
			IloIntVar[][] v = new IloIntVar[n2][numDays];

			for (int a = 0; a < n2; a++) {
				for (int b = 0; b < numDays; b++) {
					String varName = "v" + a + b;
					v[a][b] = cplex.intVar(0, Integer.MAX_VALUE, varName);
				}
			}

			// Slack Variable for science even distribution
			IloIntVar[][] u2 = new IloIntVar[teachingCohort][numDays];

			for (int a = 0; a < teachingCohort; a++) {
				for (int b = 0; b < numDays; b++) {
					String varName = "u2" + a + b;
					u2[a][b] = cplex.intVar(0, Integer.MAX_VALUE, varName);
				}
			}

			// Surplus Variable for science even distribution
			IloIntVar[][] v2 = new IloIntVar[teachingCohort][numDays];

			for (int a = 0; a < teachingCohort; a++) {
				for (int b = 0; b < numDays; b++) {
					String varName = "v2" + a + b;
					v2[a][b] = cplex.intVar(0, Integer.MAX_VALUE, varName);
				}
			}

			// Slack Variable for gym even distribution
			IloIntVar[][] u3 = new IloIntVar[teachingCohort][numDays];

			for (int a = 0; a < teachingCohort; a++) {
				for (int b = 0; b < numDays; b++) {
					String varName = "u3" + a + b;
					u3[a][b] = cplex.intVar(0, Integer.MAX_VALUE, varName);
				}
			}

			// Surplus Variable for gym even distribution
			IloIntVar[][] v3 = new IloIntVar[teachingCohort][numDays];

			for (int a = 0; a < teachingCohort; a++) {
				for (int b = 0; b < numDays; b++) {
					String varName = "v3" + a + b;
					v3[a][b] = cplex.intVar(0, Integer.MAX_VALUE, varName);
				}
			}

			// Slack Variable for social studies even distribution
			IloIntVar[][] u4 = new IloIntVar[teachingCohort][numDays];

			for (int a = 0; a < teachingCohort; a++) {
				for (int b = 0; b < numDays; b++) {
					String varName = "u4" + a + b;
					u4[a][b] = cplex.intVar(0, Integer.MAX_VALUE, varName);
				}
			}

			// Surplus Variable for social studies even distribution
			IloIntVar[][] v4 = new IloIntVar[teachingCohort][numDays];

			for (int a = 0; a < teachingCohort; a++) {
				for (int b = 0; b < numDays; b++) {
					String varName = "v4" + a + b;
					v4[a][b] = cplex.intVar(0, Integer.MAX_VALUE, varName);
				}
			}

			// Indicator variable for Primary Classes Language back to back
			IloIntVar[][] a = new IloIntVar[primary][blockCount];

			for (int i = 0; i < primary; i++) {
				for (int j = 0; j < blockCount; j++) {
					String varName = "a" + i + j;
					a[i][j] = cplex.boolVar(varName);
				}
			}

			// define objective
			IloLinearNumExpr objective = cplex.linearNumExpr();
			for (int i = 0; i < subjects; i++) {
				for (int j = 0; j < n2; j++) {
					for (int k = 0; k < teachingCohort; k++) {
						for (int t = 0; t < n4; t++) {

							objective.addTerm(rewards[k][j][i], x[i][j][k][t]);
							// objective.addTerm(1, x[i][j][k][t]);
						}
					}
				}
			}

			for (int j = 0; j < n2; j++) {
				for (int d = 0; d < numDays; d++) {
					objective.addTerm(-pjd, u[j][d]); // prep even distribution
					objective.addTerm(-pjd, v[j][d]);
				}
			}

			// objective for slack and surplus weights for even distribution
			for (int k = 0; k < teachingCohort; k++) {
				for (int d = 0; d < numDays; d++) {
					objective.addTerm(-50, v2[k][d]); // science
					objective.addTerm(-50, v3[k][d]); // gym
					objective.addTerm(-50, v4[k][d]); // social studies
				}
			}

			cplex.addMaximize(objective);

			//define constraints

			//one teacher assigned to subject per cohort part1
			IloLinearNumExpr[][] assign2 = new IloLinearNumExpr[subjects][teachingCohort];
			for (int i = 0; i < subjects; i++) {
				for (int k = 0; k < teachingCohort; k++) {
					assign2[i][k] = cplex.linearNumExpr();

					for (int j = 0; j < n2; j++) {
						assign2[i][k].addTerm(1, y[i][j][k]);
					}
				}
			}

			for (int i = 0; i < subjects; i++) {
				for (int k = 0; k < teachingCohort; k++) {
					cplex.addEq(assign2[i][k], 1);
				}
			}

			//one teacher assigned to subject part2
			IloLinearNumExpr[][][][] assign4 = new IloLinearNumExpr[subjects][n2][teachingCohort][n4];

			for (int i = 0; i < subjects; i++) {
				for (int j = 0; j < n2; j++) {
					for (int k = 0; k < teachingCohort; k++) {
						for (int t = 0; t < n4; t++) {
							assign4[i][j][k][t] = cplex.linearNumExpr();
							assign4[i][j][k][t].addTerm(1, x[i][j][k][t]);
							assign4[i][j][k][t].addTerm(-1, y[i][j][k]);

						}
					}
				}
			}

			for (int i = 0; i < subjects; i++) {
				for (int j = 0; j < n2; j++) {
					for (int k = 0; k < teachingCohort; k++) {
						for (int t = 0; t < n4; t++) {
							cplex.addLe(assign4[i][j][k][t], 0);
						}
					}
				}
			}

			//assignment1- only 1 teacher assigned to a cohort and subject at a time-fixed constr
			IloLinearNumExpr[][][] assign1 = new IloLinearNumExpr[subjects][teachingCohort][n4];

			for (int i = 0; i < subjects; i++) {
				for (int k = 0; k < teachingCohort; k++) {
					for (int t = 0; t < n4; t++) {
						assign1[i][k][t] = cplex.linearNumExpr();

						for (int j = 0; j < n2; j++) {
							assign1[i][k][t].addTerm(1, x[i][j][k][t]);
						}
					}
				}
			}

			for (int i = 0; i < subjects; i++) {
				for (int k = 0; k < teachingCohort; k++) {
					for (int t = 0; t < n4; t++) {
						cplex.addLe(assign1[i][k][t], 1);
					}
				}
			}

			//teacher can only teach one subject/ class at a time
			IloLinearNumExpr[][] constr2 = new IloLinearNumExpr[n2][n4];
			IloLinearNumExpr[][] constr3 = new IloLinearNumExpr[n2][n4];

			for (int j = 0; j < n2; j++) {
				for (int t = 0; t < n4; t++) {
					constr2[j][t] = cplex.linearNumExpr();

					for (int i = 0; i < n; i++) {
						for (int k = 0; k < n3; k++) {
							constr2[j][t].addTerm(1, x[i][j][k][t]);
						}
					}

				}
			}
			//teacher can only teach one subject/class at a time - cant be assigned to teach or prep if not available
			for (int j = 0; j < n2; j++) {
				for (int t = 0; t < n4; t++) {
					constr3[j][t] = cplex.linearNumExpr();

					for (int i = 0; i < subjects; i++) {
						for (int k = 0; k < teachingCohort; k++) {
							constr3[j][t].addTerm(1, x[i][j][k][t]);
						}
					}
					constr3[j][t].addTerm(1, x[subjectRange][j][cohortRange][t]);

				}
			}

			for (int j = 0; j < n2; j++) {
				for (int t = 0; t < n4; t++) {
					cplex.addEq(constr2[j][t], 1);

				}
			}

			for (int j = 0; j < n2; j++) {
				for (int t = 0; t < n4; t++) {
					// was availableTime[j][t]
					cplex.addEq(constr3[j][t], availableTime.get(j)[t]);
				}
			}

			//assignment2 - at every time, each cohort needs only one teacher and one subject
			IloLinearNumExpr[][] constr4 = new IloLinearNumExpr[teachingCohort][n4];

			for (int k = 0; k < teachingCohort; k++) {
				for (int t = 0; t < n4; t++) {
					constr4[k][t] = cplex.linearNumExpr();
					for (int i = 0; i < subjects; i++) {
						for (int j = 0; j < n2; j++) {
							constr4[k][t].addTerm(1, x[i][j][k][t]);
						}
					}
				}
			}

			for (int k = 0; k < teachingCohort; k++) {
				for (int t = 0; t < n4; t++) {
					cplex.addEq(constr4[k][t], 1);
				}
			}

			//assignment3- each cohort assigned to 12 time periods
			IloLinearNumExpr[] assign3 = new IloLinearNumExpr[teachingCohort];

			for (int k = 0; k < teachingCohort; k++) {
				assign3[k] = cplex.linearNumExpr();
				for (int i = 0; i < subjects; i++) {
					for (int j = 0; j < n2; j++) {
						for (int t = 0; t < n4; t++) {
							assign3[k].addTerm(1, x[i][j][k][t]);
						}
					}
				}
			}

			for (int k = 0; k < teachingCohort; k++) {
				cplex.addEq(assign3[k], 30);
			}

			//schedule part time teachers away time
			IloLinearNumExpr[] partTime = new IloLinearNumExpr[n2];

			for (int j = 0; j < n2; j++) {
				partTime[j] = cplex.linearNumExpr();
				for (int t = 0; t < n4; t++) {
					partTime[j].addTerm(lengtht[t], x[awaySubject][j][awayCohort][t]);

				}
			}

			for (int j = 0; j < n2; j++) {
				double rhs = totalTime - totalTeacherMin[j];
				cplex.addGe(partTime[j], rhs);
			}

			//constraint 7, math
			IloLinearNumExpr[] math1 = new IloLinearNumExpr[teachingCohort];
			IloLinearNumExpr[] math2 = new IloLinearNumExpr[teachingCohort];
			IloLinearNumExpr[] math3 = new IloLinearNumExpr[teachingCohort];
			IloLinearNumExpr[] math4 = new IloLinearNumExpr[teachingCohort];
			IloLinearNumExpr[] math5 = new IloLinearNumExpr[teachingCohort];

			for (int k = 0; k < teachingCohort; k++) {
				math1[k] = cplex.linearNumExpr();
				math2[k] = cplex.linearNumExpr();
				math3[k] = cplex.linearNumExpr();
				math4[k] = cplex.linearNumExpr();
				math5[k] = cplex.linearNumExpr();

				for (int j = 0; j < n2; j++) {
					for (int a1 = day1s; a1 < day1f; a1++) {
						math1[k].addTerm(lengtht[a1], x[0][j][k][a1]);
					}
					for (int b1 = day2s; b1 < day2f; b1++) {
						math2[k].addTerm(lengtht[b1], x[0][j][k][b1]);
					}
					for (int c = day3s; c < day3f; c++) {
						math3[k].addTerm(lengtht[c], x[0][j][k][c]);
					}
					for (int d = day4s; d < day4f; d++) {
						math4[k].addTerm(lengtht[d], x[0][j][k][d]);
					}
					for (int e = day5s; e < day5f; e++) {
						math5[k].addTerm(lengtht[e], x[0][j][k][e]);
					}
				}
			}

			for (int k = 0; k < teachingCohort; k++) {
				cplex.addEq(math1[k], 60);
				cplex.addEq(math2[k], 60);
				cplex.addEq(math3[k], 60);
				cplex.addEq(math4[k], 60);
				cplex.addEq(math5[k], 60);
			}

			//constraint 8, language for primary cohorts
			IloLinearNumExpr[] lang1 = new IloLinearNumExpr[primary];
			IloLinearNumExpr[] lang2 = new IloLinearNumExpr[primary];
			IloLinearNumExpr[] lang3 = new IloLinearNumExpr[primary];
			IloLinearNumExpr[] lang4 = new IloLinearNumExpr[primary];
			IloLinearNumExpr[] lang5 = new IloLinearNumExpr[primary];

			for (int k = 0; k < primaryUb; k++) {
				lang1[k] = cplex.linearNumExpr();
				lang2[k] = cplex.linearNumExpr();
				lang3[k] = cplex.linearNumExpr();
				lang4[k] = cplex.linearNumExpr();
				lang5[k] = cplex.linearNumExpr();

				for (int j = 0; j < n2; j++) {
					for (int a1 = day1s; a1 < day1f; a1++) {
						lang1[k].addTerm(lengtht[a1], x[1][j][k][a1]);
					}
					for (int b1 = day2s; b1 < day2f; b1++) {
						lang2[k].addTerm(lengtht[b1], x[1][j][k][b1]);
					}
					for (int c = day3s; c < day3f; c++) {
						lang3[k].addTerm(lengtht[c], x[1][j][k][c]);
					}
					for (int d = day4s; d < day4f; d++) {
						lang4[k].addTerm(lengtht[d], x[1][j][k][d]);
					}
					for (int e = day5s; e < day5f; e++) {
						lang5[k].addTerm(lengtht[e], x[1][j][k][e]);
					}
				}
			}

			for (int k = 0; k < primaryUb; k++) {
				cplex.addEq(lang1[k], 100);
				cplex.addEq(lang2[k], 100);
				cplex.addEq(lang3[k], 100);
				cplex.addEq(lang4[k], 100);
				cplex.addEq(lang5[k], 100);
			}

			//constraint 9, language for french applicable cohort
			IloLinearNumExpr[] lang6 = new IloLinearNumExpr[frenchNum];

			for (int k = 0; k < frenchNum; k++) {
				lang6[k] = cplex.linearNumExpr();
				for (int j = 0; j < n2; j++) {
					for (int t = 0; t < n4; t++) {
						lang6[k].addTerm(lengtht[t], x[1][j][k + frenchCohortlb][t]);
					}
				}
			}

			for (int k = 0; k < frenchNum; k++) {
				cplex.addGe(lang6[k], 300);
			}

			//constraint 10, science 
			IloLinearNumExpr[] sci1 = new IloLinearNumExpr[teachingCohort];

			for (int k = 0; k < teachingCohort; k++) {
				sci1[k] = cplex.linearNumExpr();
				// sci2[k] = cplex.linearNumExpr();
				for (int j = 0; j < n2; j++) {
					for (int t = 0; t < n4; t++) {
						sci1[k].addTerm(lengtht[t], x[2][j][k][t]);
					}
				}
			}

			for (int k = 0; k < teachingCohort; k++) {
				cplex.addGe(sci1[k], 80);
			}

			//constraint 11, art
			IloLinearNumExpr[] art = new IloLinearNumExpr[teachingCohort];

			for (int k = 0; k < teachingCohort; k++) {
				art[k] = cplex.linearNumExpr();
				for (int j = 0; j < n2; j++) {
					for (int t = 0; t < n4; t++) {
						art[k].addTerm(lengtht[t], x[3][j][k][t]);
					}
				}
			}

			for (int k = 0; k < teachingCohort; k++) {
				cplex.addGe(art[k], 40);
			}

			//constraint 12, social studies
			IloLinearNumExpr[] soc = new IloLinearNumExpr[teachingCohort];

			for (int k = 0; k < teachingCohort; k++) {
				soc[k] = cplex.linearNumExpr();
				for (int j = 0; j < n2; j++) {
					for (int t = 0; t < n4; t++) {
						soc[k].addTerm(lengtht[t], x[4][j][k][t]);
					}
				}
			}

			for (int k = 0; k < teachingCohort; k++) {
				cplex.addGe(soc[k], 80);
			}

			//constraint 13, phys-ed
			IloLinearNumExpr[] phys = new IloLinearNumExpr[teachingCohort];

			for (int k = 0; k < teachingCohort; k++) {
				phys[k] = cplex.linearNumExpr();
				for (int j = 0; j < n2; j++) {
					for (int t = 0; t < n4; t++) {
						phys[k].addTerm(lengtht[t], x[5][j][k][t]);
					}
				}
			}

			for (int k = 0; k < teachingCohort; k++) {
				cplex.addGe(phys[k], 80);
			}

			//constraint 14, french for applicable classes
			IloLinearNumExpr[] french2 = new IloLinearNumExpr[frenchNum];

			for (int k = 0; k < frenchNum; k++) {
				french2[k] = cplex.linearNumExpr();
				for (int j = 0; j < frenchTeach; j++) {
					for (int t = 0; t < n4; t++) {
						french2[k].addTerm(lengtht[t], x[6][j + frenchTeachlb][k + frenchCohortlb][t]);
					}
				}
			}

			for (int k = 0; k < frenchNum; k++) {
				cplex.addEq(french2[k], 200);
			}

			//constraint 14 pt2- primary cant have french
			IloLinearNumExpr[] noFrench = new IloLinearNumExpr[primary];

			for (int k = 0; k < primary; k++) {
				noFrench[k] = cplex.linearNumExpr();
				for (int j = 0; j < n2; j++) {
					for (int t = 0; t < n4; t++) {
						noFrench[k].addTerm(lengtht[t], x[6][j][k][t]);
					}
				}
			}

			for (int k = 0; k < primary; k++) {
				cplex.addEq(noFrench[k], 0);
			}

			//constraint 20, music
			IloLinearNumExpr[] music = new IloLinearNumExpr[teachingCohort];

			for (int k = 0; k < teachingCohort; k++) {
				music[k] = cplex.linearNumExpr();
				for (int j = 0; j < n2; j++) {
					for (int t = 0; t < n4; t++) {
						music[k].addTerm(lengtht[t], x[7][j][k][t]);
					}
				}
			}

			for (int k = 0; k < teachingCohort; k++) {
				cplex.addGe(music[k], 40);
			}

			//constraint 21, drama
			IloLinearNumExpr[] drama = new IloLinearNumExpr[teachingCohort];

			for (int k = 0; k < teachingCohort; k++) {
				drama[k] = cplex.linearNumExpr();
				for (int j = 0; j < n2; j++) {
					for (int t = 0; t < n4; t++) {
						drama[k].addTerm(lengtht[t], x[8][j][k][t]);
					}
				}
			}

			for (int k = 0; k < teachingCohort; k++) {
				cplex.addGe(drama[k], 40);
			}

			//constraint 15, prep
			IloLinearNumExpr[] prepCon = new IloLinearNumExpr[n2];

			for (int j = 0; j < n2; j++) {
				prepCon[j] = cplex.linearNumExpr();
				for (int t = 0; t < n4; t++) {
					prepCon[j].addTerm(lengtht[t], x[prepSubject][j][prepCohort][t]);
				}
			}

			for (int j = 0; j < n2; j++) {
				cplex.addGe(prepCon[j], prep[j]);
			}

			//constraint 16, teaching mins 
			IloLinearNumExpr[] teach = new IloLinearNumExpr[n2];

			for (int j = 0; j < n2; j++) {
				teach[j] = cplex.linearNumExpr();
				for (int t = 0; t < n4; t++) {
					for (int i = 0; i < subjects; i++) {
						for (int k = 0; k < teachingCohort; k++) {
							teach[j].addTerm(lengtht[t], x[i][j][k][t]);
						}
					}
				}
			}

			for (int j = 0; j < n2; j++) {
				cplex.addLe(teach[j], teachMin[j]);
			}

			//constraint 17, gym capacity
			IloLinearNumExpr[] gymCapCon = new IloLinearNumExpr[n4];

			for (int t = 0; t < n4; t++) {
				gymCapCon[t] = cplex.linearNumExpr();
				for (int j = 0; j < n2; j++) {
					for (int k = 0; k < n3; k++) {
						gymCapCon[t].addTerm(1, x[5][j][k][t]);
					}
				}
			}

			for (int t = 0; t < n4; t++) {
				cplex.addLe(gymCapCon[t], gymCap);
			}

			//constraint 18, prep time objective
			IloLinearNumExpr[] prep1 = new IloLinearNumExpr[n2];
			IloLinearNumExpr[] prep2 = new IloLinearNumExpr[n2];
			IloLinearNumExpr[] prep3 = new IloLinearNumExpr[n2];
			IloLinearNumExpr[] prep4 = new IloLinearNumExpr[n2];
			IloLinearNumExpr[] prep5 = new IloLinearNumExpr[n2];

			for (int j = 0; j < n2; j++) {
				prep1[j] = cplex.linearNumExpr();
				prep2[j] = cplex.linearNumExpr();
				prep3[j] = cplex.linearNumExpr();
				prep4[j] = cplex.linearNumExpr();
				prep5[j] = cplex.linearNumExpr();

				for (int a1 = day1s; a1 < day1f; a1++) {
					prep1[j].addTerm(1, x[prepSubject][j][prepCohort][a1]);

				}
				prep1[j].addTerm(1, u[j][0]);
				prep1[j].addTerm(-1, v[j][0]);

				for (int b1 = day2s; b1 < day2f; b1++) {
					prep2[j].addTerm(1, x[prepSubject][j][prepCohort][b1]);
				}
				prep2[j].addTerm(1, u[j][1]);
				prep2[j].addTerm(-1, v[j][1]);

				for (int c = day3s; c < day3f; c++) {
					prep3[j].addTerm(1, x[prepSubject][j][prepCohort][c]);
				}
				prep3[j].addTerm(1, u[j][2]);
				prep3[j].addTerm(-1, v[j][2]);

				for (int d = day4s; d < day4f; d++) {
					prep4[j].addTerm(1, x[prepSubject][j][prepCohort][d]);
				}
				prep4[j].addTerm(1, u[j][3]);
				prep4[j].addTerm(-1, v[j][3]);

				for (int e = day5s; e < day5f; e++) {
					prep5[j].addTerm(1, x[prepSubject][j][prepCohort][e]);
				}
				prep5[j].addTerm(1, u[j][4]);
				prep5[j].addTerm(-1, v[j][4]);
			}

			for (int j = 0; j < n2; j++) {
				cplex.addEq(prep1[j], 1);
				cplex.addEq(prep2[j], 1);
				cplex.addEq(prep3[j], 1);
				cplex.addEq(prep4[j], 1);
				cplex.addEq(prep5[j], 1);
			}

			//slack and surplus variables for prep time dist
			IloLinearNumExpr[][] slack1 = new IloLinearNumExpr[n2][numDays];
			IloLinearNumExpr[][] surplus1 = new IloLinearNumExpr[n2][numDays];

			for (int j = 0; j < n2; j++) {
				for (int d = 0; d < numDays; d++) {
					slack1[j][d] = cplex.linearNumExpr();
					surplus1[j][d] = cplex.linearNumExpr();

					slack1[j][d].addTerm(1, u[j][d]);
					surplus1[j][d].addTerm(1, v[j][d]);
				}
			}

			for (int j = 0; j < n2; j++) {
				for (int d = 0; d < numDays; d++) {
					cplex.addGe(slack1[j][d], 0);
					cplex.addGe(surplus1[j][d], 0);
				}
			}

			//constraint 19, language for primary has to be back to back
			IloLinearNumExpr[][] prilan1 = new IloLinearNumExpr[numDays][primary];
			IloLinearNumExpr[][] prilan2 = new IloLinearNumExpr[numDays][primary];
			IloLinearNumExpr[][] prilan3 = new IloLinearNumExpr[numDays][primary];

			for (int d = 0; d < numDays; d++) {
				for (int k = 0; k < primary; k++) {
					prilan1[d][k] = cplex.linearNumExpr();
					prilan2[d][k] = cplex.linearNumExpr();
					prilan3[d][k] = cplex.linearNumExpr();

					for (int j = 0; j < n2; j++) {
						prilan1[d][k].addTerm(1, x[1][j][k][(d * 6)]);
						prilan1[d][k].addTerm(1, x[1][j][k][1 + (d * 6)]);

						prilan2[d][k].addTerm(1, x[1][j][k][2 + (d * 6)]);
						prilan2[d][k].addTerm(1, x[1][j][k][3 + (d * 6)]);

						prilan3[d][k].addTerm(1, x[1][j][k][4 + (d * 6)]);
						prilan3[d][k].addTerm(1, x[1][j][k][5 + (d * 6)]);

					}
				}
			}

			for (int d = 0; d < numDays; d++) {
				for (int k = 0; k < primary; k++) {
					cplex.addEq(prilan1[d][k], cplex.prod(2, a[k][3 * d]));
					cplex.addEq(prilan2[d][k], cplex.prod(2, a[k][1 + (3 * d)]));
					cplex.addEq(prilan3[d][k], cplex.prod(2, a[k][2 + (3 * d)]));

				}
			}

			//contraint 22- have to have french less than or eqaul to once a day
			IloLinearNumExpr[] fr1 = new IloLinearNumExpr[frenchNum];
			IloLinearNumExpr[] fr2 = new IloLinearNumExpr[frenchNum];
			IloLinearNumExpr[] fr3 = new IloLinearNumExpr[frenchNum];
			IloLinearNumExpr[] fr4 = new IloLinearNumExpr[frenchNum];
			IloLinearNumExpr[] fr5 = new IloLinearNumExpr[frenchNum];

			for (int k = 0; k < frenchNum; k++) {
				fr1[k] = cplex.linearNumExpr();
				fr2[k] = cplex.linearNumExpr();
				fr3[k] = cplex.linearNumExpr();
				fr4[k] = cplex.linearNumExpr();
				fr5[k] = cplex.linearNumExpr();

				for (int j = 0; j < frenchTeach; j++) {
					for (int t = day1s; t < day1f; t++) {
						fr1[k].addTerm(1, x[6][j + frenchTeachlb][k + frenchCohortlb][t]);
					}
					for (int t = day2s; t < day2f; t++) {
						fr2[k].addTerm(1, x[6][j + frenchTeachlb][k + frenchCohortlb][t]);
					}
					for (int t = day3s; t < day3f; t++) {
						fr3[k].addTerm(1, x[6][j + frenchTeachlb][k + frenchCohortlb][t]);
					}
					for (int t = day4s; t < day4f; t++) {
						fr4[k].addTerm(1, x[6][j + frenchTeachlb][k + frenchCohortlb][t]);
					}
					for (int t = day5s; t < day5f; t++) {
						fr5[k].addTerm(1, x[6][j + frenchTeachlb][k + frenchCohortlb][t]);
					}
				}
			}

			for (int k = 0; k < frenchNum; k++) {
				cplex.addLe(fr1[k], 1);
				cplex.addLe(fr2[k], 1);
				cplex.addLe(fr3[k], 1);
				cplex.addLe(fr4[k], 1);
				cplex.addLe(fr5[k], 1);
			}

			//contraint 23- have to have language at least once a day for junior/intermediate
			IloLinearNumExpr[] lg1 = new IloLinearNumExpr[frenchNum];
			IloLinearNumExpr[] lg2 = new IloLinearNumExpr[frenchNum];
			IloLinearNumExpr[] lg3 = new IloLinearNumExpr[frenchNum];
			IloLinearNumExpr[] lg4 = new IloLinearNumExpr[frenchNum];
			IloLinearNumExpr[] lg5 = new IloLinearNumExpr[frenchNum];

			for (int k = 0; k < frenchNum; k++) {
				lg1[k] = cplex.linearNumExpr();
				lg2[k] = cplex.linearNumExpr();
				lg3[k] = cplex.linearNumExpr();
				lg4[k] = cplex.linearNumExpr();
				lg5[k] = cplex.linearNumExpr();

				for (int j = 0; j < n2; j++) {
					for (int t = day1s; t < day1f; t++) {
						lg1[k].addTerm(1, x[1][j][k + frenchCohortlb][t]);
					}
					for (int t = day2s; t < day2f; t++) {
						lg2[k].addTerm(1, x[1][j][k + frenchCohortlb][t]);
					}
					for (int t = day3s; t < day3f; t++) {
						lg3[k].addTerm(1, x[1][j][k + frenchCohortlb][t]);
					}
					for (int t = day4s; t < day4f; t++) {
						lg4[k].addTerm(1, x[1][j][k + frenchCohortlb][t]);
					}
					for (int t = day5s; t < day5f; t++) {
						lg5[k].addTerm(1, x[1][j][k + frenchCohortlb][t]);
					}
				}
			}

			for (int k = 0; k < frenchNum; k++) {
				cplex.addGe(lg1[k], 1);
				cplex.addGe(lg2[k], 1);
				cplex.addGe(lg3[k], 1);
				cplex.addGe(lg4[k], 1);
				cplex.addGe(lg5[k], 1);
			}

			//constraint 24 minimize # of times cohorts have gym, science, and social studies on the same day- not included in pull request- keep for testing

			IloLinearNumExpr[] gym1 = new IloLinearNumExpr[teachingCohort];
			IloLinearNumExpr[] gym2 = new IloLinearNumExpr[teachingCohort];
			IloLinearNumExpr[] gym3 = new IloLinearNumExpr[teachingCohort];
			IloLinearNumExpr[] gym4 = new IloLinearNumExpr[teachingCohort];
			IloLinearNumExpr[] gym5 = new IloLinearNumExpr[teachingCohort];
			IloLinearNumExpr[] sc1 = new IloLinearNumExpr[teachingCohort];
			IloLinearNumExpr[] sc2 = new IloLinearNumExpr[teachingCohort];
			IloLinearNumExpr[] sc3 = new IloLinearNumExpr[teachingCohort];
			IloLinearNumExpr[] sc4 = new IloLinearNumExpr[teachingCohort];
			IloLinearNumExpr[] sc5 = new IloLinearNumExpr[teachingCohort];
			IloLinearNumExpr[] ss1 = new IloLinearNumExpr[teachingCohort];
			IloLinearNumExpr[] ss2 = new IloLinearNumExpr[teachingCohort];
			IloLinearNumExpr[] ss3 = new IloLinearNumExpr[teachingCohort];
			IloLinearNumExpr[] ss4 = new IloLinearNumExpr[teachingCohort];
			IloLinearNumExpr[] ss5 = new IloLinearNumExpr[teachingCohort];

			for (int k = 0; k < teachingCohort; k++) {

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

				for (int t = day1s; t < day1f; t++) {
					for (int j = 0; j < n2; j++) {
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

				for (int t = day2s; t < day2f; t++) {
					for (int j = 0; j < n2; j++) {
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

				for (int t = day3s; t < day3f; t++) {
					for (int j = 0; j < n2; j++) {
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

				for (int t = day4s; t < day4f; t++) {
					for (int j = 0; j < n2; j++) {
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

				for (int t = day5s; t < day5f; t++) {
					for (int j = 0; j < n2; j++) {
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

			for (int k = 0; k < teachingCohort; k++) {
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
			IloLinearNumExpr[][] slack3 = new IloLinearNumExpr[teachingCohort][numDays];
			IloLinearNumExpr[][] surplus3 = new IloLinearNumExpr[teachingCohort][numDays];
			IloLinearNumExpr[][] slack2 = new IloLinearNumExpr[teachingCohort][numDays];
			IloLinearNumExpr[][] surplus2 = new IloLinearNumExpr[teachingCohort][numDays];
			IloLinearNumExpr[][] slack4 = new IloLinearNumExpr[teachingCohort][numDays];
			IloLinearNumExpr[][] surplus4 = new IloLinearNumExpr[teachingCohort][numDays];

			for (int k = 0; k < teachingCohort; k++) {
				for (int d = 0; d < numDays; d++) {
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

			for (int k = 0; k < teachingCohort; k++) {
				for (int d = 0; d < numDays; d++) {
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
			cplex.setParam(IloCplex.Param.MIP.Tolerances.MIPGap, 2e-2);

			if (cplex.solve()) {

				System.out.println("Objective = " + cplex.getObjValue());
				System.out.println("Teacher, Cohort, Subject, Period, Day");

				int mathTime[] = new int[teachingCohort];
				int langTime[] = new int[teachingCohort];
				int artTime[] = new int[teachingCohort];
				int socTime[] = new int[teachingCohort];
				int sciTime[] = new int[teachingCohort];
				int gymTime[] = new int[teachingCohort];
				int musTime[] = new int[teachingCohort];
				int dramTime[] = new int[teachingCohort];
				int frenchTime[] = new int[teachingCohort];
				int totalMinTime[] = new int[n2];
				int prepTime[] = new int[n2];
				int teachTime[] = new int[n2];
				int awayTime[] = new int[n2];

				for (int t = 0; t < n4; t++) {
					for (int i = 0; i < n; i++) {
						for (int j = 0; j < n2; j++) {
							for (int k = 0; k < n3; k++) {
								if ((cplex.getValue(x[i][j][k][t])) > 0.5) {
									totalMinTime[k] = totalMinTime[k] + lengtht[t];
									System.out.print("Teacher: " + j + " Cohort: " + k + " Subject: " + subj[i]
											+ " Period: " + (t + 1));
									if (t == 0 || t == 1 || t == 2 || t == 3 || t == 4 || t == 5) {
										System.out.println(" Day 1");
									} else if (t == 6 || t == 7 || t == 8 || t == 9 || t == 10 || t == 11) {
										System.out.println(" Day 2");
									} else if (t == 12 || t == 13 || t == 14 || t == 15 || t == 16 || t == 17) {
										System.out.println(" Day 3");
									} else if (t == 18 || t == 19 || t == 20 || t == 21 || t == 22 || t == 23) {
										System.out.println(" Day 4");
									} else {
										System.out.println(" Day 5");
									}
									if (i == 0) {

										mathTime[k] = mathTime[k] + lengtht[t];
									}
									if (i == 1) {
										langTime[k] = langTime[k] + lengtht[t];
									}
									if (i == 2) {
										sciTime[k] = sciTime[k] + lengtht[t];
									}
									if (i == 3) {
										artTime[k] = artTime[k] + lengtht[t];
									}
									if (i == 4) {
										socTime[k] = socTime[k] + lengtht[t];
									}
									if (i == 5) {
										gymTime[k] = gymTime[k] + lengtht[t];
									}
									if (i == 6) {
										frenchTime[k] = frenchTime[k] + lengtht[t];
									}
									if (i == 7) {
										musTime[k] = musTime[k] + lengtht[t];
									}
									if (i == 8) {
										dramTime[k] = dramTime[k] + lengtht[t];
									}
									if (i == prepSubject) {
										prepTime[j] = prepTime[j] + lengtht[t];
									} else if (i == awaySubject) {
										awayTime[j] = awayTime[j] + lengtht[t];
									} else {
										teachTime[j] = teachTime[j] + lengtht[t];
									}

								}
							}
						}
					}

				}

				// output to Excel - simple master list printout
				Sheet Outsheet = workbook.getSheetAt(3);
				int beginRowIndex = 1;
				int beginColumn = 0;

				for (int t = 0; t < n4; t++) {
					for (int i = 0; i < n; i++) {
						for (int j = 0; j < n2; j++) {
							for (int k = 0; k < n3; k++) {
								if ((cplex.getValue(x[i][j][k][t])) > 0.5) {
									Row beginRow = Outsheet.createRow(++beginRowIndex);
									// print teacher names
									beginRow.createCell(beginColumn).setCellValue(teacherNames.get(j));
									// print cohort name
									beginRow.createCell(beginColumn + 1).setCellValue(cohortNames.get(k));
									// print subject
									beginRow.createCell(beginColumn + 2).setCellValue(subj[i]);
									if (t == 0 || t == 1 || t == 2 || t == 3 || t == 4 || t == 5) {
										// if in first set print day 1 and default period number
										beginRow.createCell(beginColumn + 3).setCellValue("Day 1");
										beginRow.createCell(beginColumn + 4).setCellValue(t + 1);
									} else if (t == 6 || t == 7 || t == 8 || t == 9 || t == 10 || t == 11) {
										beginRow.createCell(beginColumn + 3).setCellValue("Day 2");
										// only print period time as 1-6
										beginRow.createCell(beginColumn + 4).setCellValue(t - 5);
									} else if (t == 12 || t == 13 || t == 14 || t == 15 || t == 16 || t == 17) {
										beginRow.createCell(beginColumn + 3).setCellValue("Day 3");
										// only print period time as 1-6
										beginRow.createCell(beginColumn + 4).setCellValue(t - 11);
									} else if (t == 18 || t == 19 || t == 20 || t == 21 || t == 22 || t == 23) {
										beginRow.createCell(beginColumn + 3).setCellValue("Day 4"); // only print period
																									// time as 1-6
										beginRow.createCell(beginColumn + 4).setCellValue(t - 17);
									} else {
										beginRow.createCell(beginColumn + 3).setCellValue("Day 5");
										// only print period time as 1-6
										beginRow.createCell(beginColumn + 4).setCellValue(t - 23);
									}
								}
							}
						}
					}
				}
 
				// second output sheet
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				Sheet outputSheet = workbook.createSheet("MasterSchedule" + sdf.format(timestamp));

				// headers
				int teacherStartRow = 2;
				Row teacherStart = outputSheet.createRow(teacherStartRow);
				teacherStart.createCell(0).setCellValue("Teacher");
				int day = 0;
				Row periodRow = outputSheet.createRow(1);
				periodRow.createCell(1).setCellValue("Period");
				Row dayRow = outputSheet.createRow(0);
				for (int t = 0; t < n4; t++) {
					if(t>=6 && t<12) {
						day=1;
					}
					else if(t>=12 && t<18) {
						day=2;
					}
					else if(t>=18 && t<24) {
						day=3;
					}
					else if(t>=24 && t<30) {
						day=4;
					}
					periodRow.createCell(t + 2).setCellValue((t + 1)-6*day);
				}

				XSSFFont defaultFont = (XSSFFont) workbook.createFont();

				defaultFont.setFontHeightInPoints((short) 10);
				defaultFont.setFontName("Arial");
				defaultFont.setColor(IndexedColors.BLACK.getIndex());
				defaultFont.setBold(false);
				defaultFont.setItalic(false);

				XSSFFont font = (XSSFFont) workbook.createFont();
				font.setFontHeightInPoints((short) 10);
				font.setFontName("Arial");
				font.setColor(IndexedColors.BLACK.getIndex());
				font.setBold(true);
				font.setItalic(false);

				CellStyle cs = workbook.createCellStyle();
				cs.setWrapText(true);
				cs.setAlignment(HorizontalAlignment.CENTER);

				outputSheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 7));
				Cell day1 = CellUtil.createCell(dayRow, 2, "Day 1");
				CellUtil.setAlignment(day1, HorizontalAlignment.CENTER);
				CellUtil.setFont(day1, font);

				outputSheet.addMergedRegion(new CellRangeAddress(0, 0, 8, 13));
				Cell day2 = CellUtil.createCell(dayRow, 8, "Day 2");
				CellUtil.setAlignment(day2, HorizontalAlignment.CENTER);
				CellUtil.setFont(day2, font);

				outputSheet.addMergedRegion(new CellRangeAddress(0, 0, 14, 19));
				Cell day3 = CellUtil.createCell(dayRow, 14, "Day 3");
				CellUtil.setAlignment(day3, HorizontalAlignment.CENTER);
				CellUtil.setFont(day3, font);

				outputSheet.addMergedRegion(new CellRangeAddress(0, 0, 20, 25));
				Cell day4 = CellUtil.createCell(dayRow, 20, "Day 4");
				CellUtil.setAlignment(day4, HorizontalAlignment.CENTER);
				CellUtil.setFont(day4, font);

				outputSheet.addMergedRegion(new CellRangeAddress(0, 0, 26, 31));
				Cell day5 = CellUtil.createCell(dayRow, 26, "Day 5");
				CellUtil.setAlignment(day5, HorizontalAlignment.CENTER);
				CellUtil.setFont(day5, font); // this should bold it
				
				int periodStartCol = 2;
				for (int j = 0; j < n2; j++) {
					Row teacherRow = outputSheet.createRow(teacherStartRow + j);
					teacherRow.createCell(1).setCellValue(teacherNames.get(j));
					for (int t = 0; t < n4; t++) {
						for (int i = 0; i < n; i++) {
							for (int k = 0; k < n3; k++) {
								if ((cplex.getValue(x[i][j][k][t])) > 0.5) {
									if (k == n3 - 1) {
										teacherRow.createCell(periodStartCol + t).setCellValue(cohortNames.get(k));
									} else if (k == n3 - 2) {
										teacherRow.createCell(periodStartCol + t).setCellValue(cohortNames.get(k));
									} else {

										Cell cell_style = teacherRow.createCell(periodStartCol + t);
										cell_style.setCellValue(cohortNames.get(k) + " - " + subj[i]);
										cs.setFillForegroundColor(IndexedColors.fromInt(cohortColours[k]).getIndex());
										cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);

										CellStyle csColour = workbook.createCellStyle();
										csColour.cloneStyleFrom(cs);
										cell_style.setCellStyle(csColour);
									}
								}
							}
						}
					}
				}

				//output sheet for each cohort
				String sheetNames [] = new String[n3-2];
				
				for(int k=0;k<n3-2;k++) {
					if(cohortNames.get(k).contains("/")) {
						sheetNames[k] = cohortNames.get(k).replace("/", "-");
					}else if(cohortNames.get(k).contains("\\")) {
						sheetNames[k] = cohortNames.get(k).replace("\\", "-");
					}else {
						sheetNames[k] = cohortNames.get(k);
					}
					
				}
				for(int k=0; k<sheetNames.length;k++) {
					Sheet cohortOutput = workbook.createSheet(sheetNames[k]);
					
					int counter = 0;
					Row beginRow1 = cohortOutput.createRow(0);
					Row periodRowS1 = cohortOutput.createRow(1);
					Row periodRowS2 = cohortOutput.createRow(2);
					Row periodRowS3 = cohortOutput.createRow(3);
					Row periodRowS4 = cohortOutput.createRow(4);
					Row periodRowS5 = cohortOutput.createRow(5);
					Row periodRowS6 = cohortOutput.createRow(6);
									
					for(int d=1;d<6;d++) {
											
						beginRow1.createCell(d).setCellValue("Day "+d);
						System.out.println("Day "+d);
					}

					periodRowS1.createCell(0).setCellValue(1);
					periodRowS2.createCell(0).setCellValue(2);
					periodRowS3.createCell(0).setCellValue(3);
					periodRowS4.createCell(0).setCellValue(4);
					periodRowS5.createCell(0).setCellValue(5);
					periodRowS6.createCell(0).setCellValue(6);

					for (int t = 0; t < n4; t++) {
						int cohortDay=1;
						
						if(t>=6 && t<12) {
							cohortDay =2;
						}else if(t>=12 && t<18) {
							cohortDay=3;
						}else if(t>=18 && t<24) {
							cohortDay=4;
						}else if(t>=24 && t<30) {
							cohortDay=5;
						}
						
						
						for (int i = 0; i < n; i++) {
							for (int j = 0; j < n2; j++) {
									if ((cplex.getValue(x[i][j][k][t])) > 0.5) {									
										if (t == 0 || t == 6 || t == 12 || t == 18 || t == 24) {
											
											Cell cell_style = periodRowS1.createCell(cohortDay);
											cell_style.setCellValue(subj[i] + " - " + teacherNames.get(j));
											
											cs.setFillForegroundColor(IndexedColors.fromInt(teacherColours[j]).getIndex());
											cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);

											CellStyle csColour = workbook.createCellStyle();
											csColour.cloneStyleFrom(cs);
											cell_style.setCellStyle(csColour);
											
										} else if (t == 1 || t == 7 || t == 13 || t == 19 || t == 25) {
	
											Cell cell_style = periodRowS2.createCell(cohortDay);
											cell_style.setCellValue(subj[i] + " - " + teacherNames.get(j));
											
											cs.setFillForegroundColor(IndexedColors.fromInt(teacherColours[j]).getIndex());
											cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);

											CellStyle csColour = workbook.createCellStyle();
											csColour.cloneStyleFrom(cs);
											cell_style.setCellStyle(csColour);
											
										} else if (t == 2 || t == 8 || t == 14 || t == 20 || t == 26) {
											
											Cell cell_style = periodRowS3.createCell(cohortDay);
											cell_style.setCellValue(subj[i] + " - " + teacherNames.get(j));
											
											cs.setFillForegroundColor(IndexedColors.fromInt(teacherColours[j]).getIndex());
											cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);

											CellStyle csColour = workbook.createCellStyle();
											csColour.cloneStyleFrom(cs);
											cell_style.setCellStyle(csColour);
											
										} else if (t == 3 || t == 9 || t == 15 || t == 21 || t == 27) {
											
											Cell cell_style = periodRowS4.createCell(cohortDay);
											cell_style.setCellValue(subj[i] + " - " + teacherNames.get(j));
											
											cs.setFillForegroundColor(IndexedColors.fromInt(teacherColours[j]).getIndex());
											cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);

											CellStyle csColour = workbook.createCellStyle();
											csColour.cloneStyleFrom(cs);
											cell_style.setCellStyle(csColour);
											
										} else if(t == 4 || t == 10 || t == 16 || t == 22 || t == 28) {
										
											Cell cell_style = periodRowS5.createCell(cohortDay);
											cell_style.setCellValue(subj[i] + " - " + teacherNames.get(j));	
											
											cs.setFillForegroundColor(IndexedColors.fromInt(teacherColours[j]).getIndex());
											cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);

											CellStyle csColour = workbook.createCellStyle();
											csColour.cloneStyleFrom(cs);
											cell_style.setCellStyle(csColour);
											
										} else {
											
											Cell cell_style = periodRowS6.createCell(cohortDay);
											cell_style.setCellValue(subj[i] + " - " + teacherNames.get(j));
											
											cs.setFillForegroundColor(IndexedColors.fromInt(teacherColours[j]).getIndex());
											cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);

											CellStyle csColour = workbook.createCellStyle();
											csColour.cloneStyleFrom(cs);
											cell_style.setCellStyle(csColour);
										
										}
									}
								}
							}						
					}			

			}
				
				
				FileOutputStream fileOut = new FileOutputStream(excelFilePath);
				workbook.write(fileOut);

				System.out.println("Prep Time: ");
				for (int j = 0; j < n2; j++) {
					System.out.print(j + ":  " + prepTime[j] + ", ");
				}

				System.out.println("");
				System.out.println("Away Time: ");
				for (int j = 0; j < n2; j++) {
					System.out.print(j + ":  " + awayTime[j] + ", ");
				}

				System.out.println("");
				System.out.println("Teach Time: ");
				for (int j = 0; j < n2; j++) {
					System.out.print(j + ":  " + teachTime[j] + ", ");
				}

				System.out.println("");
				System.out.println("Math: ");
				for (int k = 0; k < teachingCohort; k++) {
					System.out.print(k + ":  " + mathTime[k] + ", ");
				}
				System.out.println("");
				System.out.println("Language: ");
				for (int k = 0; k < teachingCohort; k++) {
					System.out.print(k + ":  " + langTime[k] + ", ");
				}
				System.out.println("");
				System.out.println("Science: ");
				for (int k = 0; k < teachingCohort; k++) {
					System.out.print(k + ":  " + sciTime[k] + ", ");
				}
				System.out.println("");
				System.out.println("Art: ");
				for (int k = 0; k < teachingCohort; k++) {
					System.out.print(k + ": " + artTime[k] + ", ");
				}
				System.out.println("");
				System.out.println("Social Studies: ");
				for (int k = 0; k < teachingCohort; k++) {
					System.out.print(k + ": " + socTime[k] + ", ");
				}
				System.out.println("");
				System.out.println("Gym: ");
				for (int k = 0; k < teachingCohort; k++) {
					System.out.print(k + ": " + gymTime[k] + ", ");
				}
				System.out.println("");
				System.out.println("French: ");
				for (int k = 0; k < teachingCohort; k++) {
					System.out.print(k + ": " + frenchTime[k] + ", ");
				}
				System.out.println("");
				System.out.println("Music: ");
				for (int k = 0; k < teachingCohort; k++) {
					System.out.print(k + ": " + musTime[k] + ", ");
				}
				System.out.println("");
				System.out.println("Drama: ");
				for (int k = 0; k < teachingCohort; k++) {
					System.out.print(k + ": " + dramTime[k] + ", ");
				}
				System.out.println("");
				System.out.println("Total mins per class: ");
				for (int k = 0; k < teachingCohort; k++) {
					System.out.print(k + ": " + totalMinTime[k] + ", ");
				}

			} else {
				System.out.println("Model not solved");
				cplex.exportModel("lpex1.lp");
			}

		}

		catch (IloException exc) {
			exc.printStackTrace();
		}

	}

}
