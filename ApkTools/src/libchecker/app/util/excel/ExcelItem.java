package libchecker.app.util.excel;

public class ExcelItem {
	
	public static final String CONTENT_NULL = "";
	
	public int column;
	public int row;
	public Object content;
	
	public ExcelItem(int r, int c){
		column = c;
		row = r;
		content = CONTENT_NULL;
	}
	
	public ExcelItem(int r, int c, Object cont){
		column = c;
		row = r;
		content = cont;
	}
	
	@Override
	public String toString(){
		return row + " "+ column + " "+ content;
	}
}
