package libchecker.app.util.excel;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import libchecker.app.util.Log;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelOperation {

	private String mExcelFile = null;
//	private HSSFWorkbook mWorkbook = null;
//	private HSSFSheet mWorkSheet = null;
	private Workbook mWorkbook = null;
	private Sheet mWorkSheet = null;

	private boolean newExcel = false;

	public ExcelOperation(String excelFile) {
		mExcelFile = excelFile;
		if (!new File(mExcelFile).exists()) {
			try {
				String destinationFilePath = mExcelFile;
				FileOutputStream fout = new FileOutputStream(
						destinationFilePath);
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				
				HSSFWorkbook workBook = new HSSFWorkbook();
				HSSFSheet spreadSheet = workBook.createSheet("Result");
				HSSFRow row = spreadSheet.createRow((short) 0);
				HSSFCell cell;
				cell = row.createCell(0);
				cell.setCellValue(new HSSFRichTextString("Hello"));
				cell = row.createCell(1);
				cell.setCellValue(new HSSFRichTextString("World"));
				
				
				workBook.write(outputStream);
				outputStream.writeTo(fout);
				outputStream.close();
				fout.close();
				newExcel = true;
			} catch (Exception e) {
				Log.e("FATAL: Excel file init error  !!!!");
				e.printStackTrace();
			}
		}
		try {
//			mWorkbook = new HSSFWorkbook(new FileInputStream(mExcelFile));
			mWorkbook = WorkbookFactory.create(new FileInputStream(mExcelFile));
			if (newExcel) {
				mWorkSheet = mWorkbook.getSheet("Result");
			} else {
				mWorkSheet = mWorkbook.getSheetAt(0);
			}

		} catch (Exception e) {
			Log.e("FATAL: Excel file init error:");
			e.printStackTrace();
		}
	}

	/**
	 * read a single item at specified coordinate
	 * 
	 * @param row
	 * @param column
	 * @return
	 */
	public ExcelItem readItem(int row, int column) {
		// if (LOG)
		// Log.write("readItem " + row + " " + column);

		ExcelItem item = new ExcelItem(row, column);
		Row rw = mWorkSheet.getRow(row);
		if (rw == null) {
			return null;
		}
		Cell cl = rw.getCell(column);
		if (cl == null) {
			return item;
		}

		switch (cl.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC:
			item.content = "" + cl.getNumericCellValue();
			if (((String) item.content).endsWith(".0")) {
				item.content = ((String) item.content).substring(0,
						((String) item.content).length() - 2);
			}
			break;
		case Cell.CELL_TYPE_STRING:
			item.content = cl.getStringCellValue();
			break;
		}
		// if (LOG)
		// Log.write("readItem " + item.toString());
		return item;
	}

	public ArrayList<ExcelItem> readRow(int row) {
		ArrayList<ExcelItem> items = new ArrayList<ExcelItem>();
		int columns = this.getNumberOfColumns();
		for (int c = 0; c < columns; c++) {
			items.add(readItem(row, c));
		}
		return items;
	}

	/**
	 * write a single item at specified coordinate
	 * 
	 * @param item
	 */
	public void writeItem(ExcelItem item) {
		Log.v("writeItem " + item);
		Row rw = mWorkSheet.getRow(item.row);
		if (rw == null) {
			rw = mWorkSheet.createRow(item.row);
		}
		Cell cl = rw.getCell(item.column);
		if (cl == null) {
			cl = rw.createCell(item.column);
		}
		if (item.content instanceof Integer) {
			cl.setCellValue((Integer) (item.content));
		} else {
			cl.setCellValue((String) (item.content));
		}
		try {
			mWorkbook.write(new FileOutputStream(new File(mExcelFile)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * write a single row
	 * 
	 * @param item
	 */
	public void writeRow(int row, ArrayList<Object> content) {
		Row rw = mWorkSheet.getRow(row);
		if (rw == null) {
			rw = mWorkSheet.createRow(row);
		}
		for (int i = 0; i < content.size(); i++) {
			Cell cl = rw.getCell(i);
			if (cl == null) {
				cl = rw.createCell(i);
			}
			Object o = content.get(i);
			if (o instanceof Double) {
				cl.setCellValue((Double) o);
			} else if (o instanceof Integer) {
				cl.setCellValue((Integer) o);
			} else {
				cl.setCellValue((String) o);
			}
		}
		try {
			mWorkbook.write(new FileOutputStream(new File(mExcelFile)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * find a single item matching content
	 * 
	 * @param content
	 * @return
	 */
	public ExcelItem findItem(String content) {
		Log.v("findItem " + content);
		if (content == null) {
			return null;
		}
		final int rows = getNumberOfRows();
		final int columns = getNumberOfColumns();

		ExcelItem item = new ExcelItem(0, 0);
		for (int r = 0; r <= rows; r++) {
			for (int c = 0; c <= columns; c++) {
				item = readItem(r, c);
				if (item != null && content.equals(item.content)) {
				    Log.v("findItem return " + item);
					return item;
				}
			}
		}
		return null;
	}

	/**
	 * find items with given content
	 * 
	 * @param content
	 * @return
	 */
	public ArrayList<ExcelItem> findItems(String content) {
	    Log.v("findItems " + content);
		if (content == null) {
			return null;
		}
		final int rows = getNumberOfRows();
		final int columns = getNumberOfColumns();
		ArrayList<ExcelItem> items = new ArrayList<ExcelItem>();

		for (int r = 0; r <= rows; r++) {
			for (int c = 0; c <= columns; c++) {
				ExcelItem item = readItem(r, c);
				if (item != null && content.equals(item.content)) {
					items.add(item);
					Log.v("findItems " + item);
				}
			}
		}
		return items;
	}

	/**
	 * read whole column
	 * 
	 * @param column
	 * @return
	 */
	public ArrayList<ExcelItem> readColumn(int column) {
		// if (LOG)
		// Log.write("readColumn " + column);
		if (column < 0) {
			return null;
		}
		final int rows = getNumberOfRows();
		ArrayList<ExcelItem> items = new ArrayList<ExcelItem>();

		for (int r = 0; r < rows; r++) {
			ExcelItem item = readItem(r, column);
			if (item == null) {
				continue;
			}
			items.add(item);
			Log.v("readColumn " + item);
		}
		return items;
	}

	/**
	 * get column number
	 * 
	 * @param columnName
	 * @return
	 */
	public int findColumn(String columnName) {
	    Log.v("findColumn " + columnName);
		if (columnName == null) {
			return -1;
		}
		final int rows = getNumberOfRows();
		if (rows <= 0) {
			return -1;
		}
		final int columns = getNumberOfColumns();

		ExcelItem item = null;
		for (int c = 0; c < columns; c++) {
			item = readItem(0, c);
			if (item != null && columnName.equals(item.content)) {
			    Log.v("findColumn " + item.column);
				return item.column;
			}
		}
		Log.v("findColumn column not found");
		return -1;
	}

	/**
	 * read part of column at given column name
	 * 
	 * @param columeName
	 * @param number
	 * @return
	 */
	public ArrayList<ExcelItem> readColumn(String columnName, int number) {
	    Log.v("readColumn " + columnName + " " + number);
		if (number < 0) {
			return null;
		}
		final int column = findColumn(columnName);
		if (column < 0) {
			return null;
		}

		ArrayList<ExcelItem> items = new ArrayList<ExcelItem>();
		for (int r = 0; r < number; r++) {
			ExcelItem item = readItem(r, column);
			if (item == null) {
				return items;
			}
			items.add(item);
			Log.v("readColumn " + item);
		}
		return items;
	}

	public int getNumberOfRows() {
//	    Log.v("getNumberOfRows " + mWorkSheet.getPhysicalNumberOfRows());
		return mWorkSheet.getPhysicalNumberOfRows();
	}

	public int getNumberOfColumns() {
		Row rw0 = mWorkSheet.getRow(0);
		Log.v("getNumberOfColumns " + rw0.getLastCellNum());
		return rw0.getLastCellNum();
	}
}
