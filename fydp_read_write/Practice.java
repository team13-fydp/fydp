package fydp_read_write;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.FileOutputStream;
 
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
 
public class Practice {

	public static void main(String[] args) throws IOException{
		//read
		String excelFilePath = "test.xlsx";
        FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
        
        Workbook workbook = new XSSFWorkbook(inputStream);
        int numberOfSheets = workbook.getNumberOfSheets();
        ArrayList<String> readData = new ArrayList<String>(0);
        
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheetIndex = workbook.getSheetAt(i);
            Iterator<Row> iterator = sheetIndex.iterator();
            while (iterator.hasNext()) { 
            Row nextRow = iterator.next();
            Iterator<Cell> cellIterator = nextRow.cellIterator();
             
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
               readData.add(cell.getStringCellValue());
             
            }
            }
        }
        
        for (int i=0; i<readData.size(); i++) {
            System.out.println(readData.get(i));
        }
         
        Sheet sheet = workbook.createSheet("output_2");
        Row headerRow = sheet.createRow(0);
        for(int i = 0; i < readData.size(); i++) {
        	Row row = sheet.createRow(i+1);
        	row.createCell(0).setCellValue(readData.get(i));
        }
        
        FileOutputStream fileOut = new FileOutputStream("test.xlsx");
        workbook.write(fileOut);

        
	}

}