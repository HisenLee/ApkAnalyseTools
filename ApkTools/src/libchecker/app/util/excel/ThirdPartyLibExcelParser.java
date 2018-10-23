package libchecker.app.util.excel;

import libchecker.app.util.Log;

import java.util.ArrayList;


public class ThirdPartyLibExcelParser {
	private String mExcelFile = null;
	private ExcelOperation mExcel = null;
	

    public static final String CATEGORY_APP_PROTECTION = "Application Encryption";
	
	public static final int COLUMN_NO_LIB_NAME = 2;
	public static final String COLUMN_NAME_LIB_NAME = "Lib Name";

	public static final int COLUMN_NO_VERSION = 3;
	public static final String COLUMN_NAME_VERSION = "Version";

	public static final int COLUMN_NO_LATEST_VERSION = 4;
	public static final String COLUMN__NAME_LATEST_VERSION = "Latest Version";

	public static final int COLUMN_NO_VENDOR_GEO = 5;
	public static final String COLUMN_NAME_VENDOR_GEO = "Vendor Geo";

	public static final int COLUMN_NO_VENDOR = 6;
	public static final String COLUMN_NAME_VENDOR = "Vendor";

	public static final int COLUMN_NO_VENDOR_CONTACT_INFO = 7;
	public static final String COLUMN_NAME_VENDOR_CONTACT_INFO = "Vendor Contact info";

	public static final int COLUMN_NO_CATEGORY = 8;
	public static final String COLUMN_NAME_CATEGORY = "Category";

	public static final int COLUMN_NO_LIB_DESCRIPTION = 9;
	public static final String COLUMN_NAME_LIB_DESCRIPTION = "Lib Description";

	public static final int COLUMN_NO_X86_SUPPORT = 10;
	public static final String COLUMN_NAME_X86_SUPPORT = "Platform Support";

	public static final int COLUMN_NO_LINK = 11;
	public static final String COLUMN_NAME_LINK = "Link";

	public ArrayList<ThirdPartyLibInfo> thirdPartyLibs = new ArrayList<ThirdPartyLibInfo>();

	public ThirdPartyLibExcelParser(String thirdPartyLibXls) {
		mExcelFile = thirdPartyLibXls;
		mExcel = new ExcelOperation(mExcelFile);
		final int rows = mExcel.getNumberOfRows()-1 ;
		Log.i("Third party lib number "+ rows);
		for (int r = 1; r <= rows; r++) {
			ThirdPartyLibInfo l = new ThirdPartyLibInfo();
			l.name = (String)(mExcel.readItem(r, COLUMN_NO_LIB_NAME).content);
			l.version = (String)(mExcel.readItem(r, COLUMN_NO_VERSION).content);
			l.latestVersion = (String)(mExcel.readItem(r, COLUMN_NO_LATEST_VERSION).content);
			l.vendorGeo = (String)(mExcel.readItem(r, COLUMN_NO_VENDOR_GEO).content);
			l.vendor = (String)(mExcel.readItem(r, COLUMN_NO_VENDOR).content);
			l.vendorContactInfo = (String)(mExcel.readItem(r,
					COLUMN_NO_VENDOR_CONTACT_INFO).content);
			l.category = (String)(mExcel.readItem(r, COLUMN_NO_CATEGORY).content);
			l.libDescription = (String)(mExcel.readItem(r, COLUMN_NO_LIB_DESCRIPTION).content);
			l.x86Support = (String)(mExcel.readItem(r, COLUMN_NO_X86_SUPPORT).content);
			l.link = (String)(mExcel.readItem(r, COLUMN_NO_LINK).content);
			thirdPartyLibs.add(l);
		}
	}

	public ArrayList<ThirdPartyLibInfo> getThirdPartyLibs() {
		return thirdPartyLibs;
	}

	public class ThirdPartyLibInfo {
		private String name;
		private String version;
		private String latestVersion;
		private String vendorGeo;
		private String vendor;
		private String vendorContactInfo;
		private String category;
		private String libDescription;
		private String x86Support;
		private String link;

		public String getName() {
			return name;
		}

		public String getDescription() {
			return libDescription;
		}
		
		public String getVendor(){
			return vendor;
		}

		public String getX86Support() {
			return x86Support;
		}
		
	      public String getCategory() {
	            return category;
	        }

		public String toString() {
			return name + " " + version + " " + latestVersion + " " + vendorGeo
					+ " " + vendor + " " + vendorContactInfo + " " + category
					+ " " + libDescription + " " + x86Support + " " + link;
		}
	}
}
