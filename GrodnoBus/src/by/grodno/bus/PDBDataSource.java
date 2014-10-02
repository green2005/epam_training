package by.grodno.bus;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class PDBDataSource extends SQLiteOpenHelper {
	ProgressDialog progress = null;

	class DBCopier extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				ZipInputStream zin = new ZipInputStream(context.getAssets()
						.open(zipdbName));
				ZipEntry ze = null;
				ze = zin.getNextEntry();
				String outFileName = getDBfileName();
				OutputStream myOutput = new FileOutputStream(outFileName);
				// transfer bytes from the inputfile to the outputfile
				byte[] buffer = new byte[1024];
				int length;
				while ((length = zin.read(buffer)) > 0) {
					myOutput.write(buffer, 0, length);
				}
				zin.closeEntry();
				// Close the streams
				myOutput.flush();
				myOutput.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (progress != null) {
				progress.dismiss();
			}
			if (PDBDataSource.this.copyDone != null) {
				PDBDataSource.this.copyDone.onCopydone();
			}
		}

	}

	private static String dbfile =
	// "/mnt/sdcard/books/busschedule.db";
	"/data/data/by.grodno.bus/databases/busschedule.db";
	public static String dbName = "busschedule.db";
	public static String zipdbName = "busschedule.zip";
	// extStore.getAbsolutePath()+ File.separator+ "external_sd" +
	// File.separator +"Android"+ File.separator+"data"+File.separator
	// +"MinskOrgPhones.db";
	private Context context;
	private SQLiteDatabase db;
	private Cursor cr;
	private OnCopyDone copyDone = null;

	public PDBDataSource(Context context) {
		super(context, dbfile, null, 1);
		this.context = context;
	}

	public void setOnCopyDoneListener(OnCopyDone done) {
		copyDone = done;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	public String getDBfileName() {
		File f = context.getDatabasePath(dbName);
		f.getAbsolutePath();
		// return "/mnt/sdcard/books/busschedule.db";//
		return f.getAbsolutePath();
	}

	public void open() {
		db = SQLiteDatabase.openDatabase(getDBfileName(), null,
				SQLiteDatabase.NO_LOCALIZED_COLLATORS
						| SQLiteDatabase.CREATE_IF_NECESSARY);
	}

	public synchronized void close() {

		db.close();
		if (cr != null) {
			cr.close();
		}
	}

	public void createDataBase() {
		if (!dbExists()) {
			progress = new ProgressDialog(context);
			progress.setMessage("Пожалуйста, подождите ...");
			progress.show();
			SQLiteDatabase db_Read = null;
			db_Read = this.getReadableDatabase();
			db_Read.close();
			copyDataBase();
		} else {
			if (copyDone != null) {
				copyDone.onCopydone();
			}

		}
	}

	private void copyDataBase() {
		DBCopier dbc = new DBCopier();
		dbc.execute();
	}

	private boolean dbExists() {
		SQLiteDatabase checkDB = null;
		try {
			String myPath = getDBfileName();
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY
							| SQLiteDatabase.NO_LOCALIZED_COLLATORS);
			String sql = " select name from buses group by name order by length(name),name";
			Cursor cr = checkDB.rawQuery(sql, null);
			if (cr.getCount() == 0) {
				cr.close();
				checkDB.close();
				try{
					File f=new File(getDBfileName());
					f.delete();
				}catch (Exception e) {
					e.printStackTrace();
				    return false;
				};
			    return false;
			}
			try {
				cr.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (SQLiteException e) {
			  return false;
		}
		// checkDB.rawQuery("select * from buses",null);
		if (checkDB != null) {
			checkDB.close();

		}
		;

		return checkDB != null ? true : false;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public Cursor getRoutes() {
		String sql = " select name from buses group by name order by length(name),name";
		return db.rawQuery(sql, null);
	}

	public int getRouteDirCount(String busName) {
		String sql = "select count(*)  from buses where name=\"" + busName
				+ "\"";
		Cursor cr = db.rawQuery(sql, null);
		cr.moveToFirst();
		int c = cr.getInt(0);
		cr.close();
		return c;
	}

	public int getStopId(String stopName) {
		String sql = "select id from stops where name=\"" + stopName + "\"";
		Cursor cr = db.rawQuery(sql, null);
		cr.moveToFirst();
		int i = cr.getInt(0);
		cr.close();
		return i;
	}

	public int getBusId(String route, String direction) {
		String sql = "select id from buses where name=\"" + route
				+ "\" and direction=\"" + direction + "\"";
		Cursor cr = db.rawQuery(sql, null);
		cr.moveToFirst();
		int i = cr.getInt(0);
		cr.close();
		return i;
	}

	public Cursor getDays(int idBus, int idStop) {
		String sql = "select day from schedule where idbus=" + idBus
				+ " and idstop=" + idStop + " group by day";
		Cursor cr = db.rawQuery(sql, null);
		return cr;
	}

	public Cursor getDays(int idStop) {
		String sql = "select day from schedule where   idstop=" + idStop
				+ " group by day";
		Cursor cr = db.rawQuery(sql, null);
		return cr;
	}

	public String getRouteChild(String routeName, int dirPos) {
		String sql = "select direction from buses where name=" + "\""
				+ routeName + "\"";
		Cursor cr = db.rawQuery(sql, null);
		cr.moveToPosition(dirPos);
		String s = cr.getString(0);
		cr.close();
		return s;
	}

	public int getBusRouteStopsCount(String bus, String route) {
		String sql = "select id from buses where name=\"" + bus
				+ "\" and direction=\"" + route + "\"";
		Cursor cr = db.rawQuery(sql, null);
		cr.moveToFirst();
		int id = cr.getInt(0);
		cr.close();
		sql = " select count(Name) from stops where id in( "
				+ " select idstop from schedule " + " where idbus=" + id
				+ " group by idstop)";
		Cursor cr1 = db.rawQuery(sql, null);
		cr1.moveToFirst();
		int qty = cr1.getInt(0);
		cr1.close();
		return qty;
	}

	public Cursor getBusStop(String bus, String route) {
		String sql = "select id from buses where name =\"" + bus
				+ "\" and direction=\"" + route + "\"";
		Cursor cr = db.rawQuery(sql, null);
		cr.moveToFirst();
		int id = cr.getInt(0);
		cr.close();
		sql = "select st.name from rlbusstops  rl "
				+ " join stops st on st.id=rl.idstop " + " where idbus=" + id
				+ " order by rl.nomorder";
		Cursor cr1 = db.rawQuery(sql, null);
		cr1.moveToFirst();
		return cr1;
	}

	public String getTimeBorder(int idBus, int idStop, String day,
			boolean isMinTime) {
		String sql = "";
		if (isMinTime) {
			sql = "select min(time) from schedule where idbus=" + idBus
					+ " and idStop=" + idStop + " and day=\"" + day
					+ "\" and time>\"04.00\"";
		} else {
			sql = "select max(time) from schedule where idbus=" + idBus
					+ " and idStop=" + idStop + " and day=\"" + day
					+ "\" and time<\"04.00\"";
		}
		;
		Cursor cr = db.rawQuery(sql, null);
		cr.moveToFirst();
		if ((!isMinTime) && (cr.getCount() == 0)) {
			sql = "select max(time) from schedule where idbus=" + idBus
					+ " and idStop=" + idStop + " and day=\"" + day + "\"";
			cr.close();
			cr = db.rawQuery(sql, null);
		}
		String s = cr.getString(0);
		cr.close();
		return s;
	}

	public Cursor getSchedule(int idBus, int idStop, String day, String hourMask) {
		String sql;
		sql = "select time from schedule where idbus=" + idBus + " and idStop="
				+ idStop + " and day=\"" + day + "\" order by time";// and time
																	// like
																	// \""+hourMask+"%"+"\""+" order by time";
		Cursor cr = db.rawQuery(sql, null);
		cr.moveToFirst();
		return cr;
	}

	public Cursor getStops() {
		String sql;
		sql = "select  trim(replace(name,\" (конечная)\",\"\"))"
				+ " from stops group by  trim(replace(name,\" (конечная)\",\"\"))";
		Cursor cr = db.rawQuery(sql, null);
		cr.moveToFirst();
		return cr;
	}

	public Cursor getRouteMinTimeByStopName(String time, String stopName,
			List<String> dayNames) {
		String days = "";

		for (String s : dayNames) {
			if (days.length() == 0)
				days = days + "\"" + s + "\"";
			else
				days = days + ",\"" + s + "\"";
		}
		days = "(" + days + ")";

		String sql = " select " + " b.name, "
				+ " b.direction, "
				+ " st.name, "
				+ " min(rl.time), "
				+ " b.id "
				+
				// " rl.day "+
				" from stops st " + " join schedule rl on rl.idstop=st.id "
				+ " join buses b on b.id=rl.idbus " + " where st.name like \""
				+ stopName + "%" + "\"" + "	and  (rl.day in " + days + ") "
				+ " and (rl.time>\"" + time + "\")" + " group by " + "b.name, "
				+ "b.direction," + "st.name";// +
		// "rl.day";
		Cursor cr = db.rawQuery(sql, null);
		cr.moveToFirst();
		return cr;
	}

	public Cursor getRouteMinTimeByStopName(String time, String stopName,
			String dayName, String dayName2) {
		String sql = " select "
				+ " b.name, "
				+ " b.direction, "
				+ " st.name, "
				+ " min(rl.time), "
				+ " b.id "
				+
				// " rl.day "+
				" from stops st " + " join schedule rl on rl.idstop=st.id "
				+ " join buses b on b.id=rl.idbus " + " where st.name like \""
				+ stopName + "%" + "\"" + "	and  ((rl.day=\"" + dayName + "\""
				+ ") or (rl.day=\"" + dayName2 + "\"" + "))"
				+ " and (rl.time>\"" + time + "\")" + " group by " + "b.name, "
				+ "b.direction," + "st.name, " + "rl.day";
		Cursor cr = db.rawQuery(sql, null);
		cr.moveToFirst();
		return cr;
	}

	public Cursor getRouteNextTime(String time, String stopName,
			String dayName, String dayName2, String buses) {
		String sql = " select b.name,b.direction,st.name,rl.time "
				+ " from stops st " + " join schedule rl on rl.idstop=st.id "
				+ " join buses b on b.id=rl.idbus " + " where st.Name like \""
				+ stopName + "%" + "\"" + "	and  ((rl.day=\"" + dayName + "\""
				+ ") or (rl.day=\"" + dayName2 + "\"" + "))"
				+ " and rl.time>=\"00.00" + "\"" + " and rl.time<\"" + "04.00"
				+ "\"" + " and b.id not in (" + buses + ")"
				+ " group by b.name,b.direction,st.name,rl.time ";
		Cursor cr = db.rawQuery(sql, null);
		cr.moveToFirst();
		return cr;
	}

	public Cursor getRouteNextTime(String time, String stopName, String buses,
			List<String> dayNames) {
		String days = "";

		for (String s : dayNames) {
			if (days.length() == 0)
				days = days + "\"" + s + "\"";
			else
				days = days + ",\"" + s + "\"";
		}
		days = "(" + days + ")";

		String sql = " select b.name,b.direction,st.name,rl.time "
				+ " from stops st " + " join schedule rl on rl.idstop=st.id "
				+ " join buses b on b.id=rl.idbus " + " where st.Name like \""
				+ stopName + "%" + "\"" + "	and  (rl.day in " + days + ") "
				+ " and rl.time>=\"00.00" + "\"" + " and rl.time<\"" + "04.00"
				+ "\"" + " and b.id not in (" + buses + ")"
				+ " group by b.name,b.direction,st.name,rl.time ";
		Cursor cr = db.rawQuery(sql, null);
		cr.moveToFirst();
		return cr;
	}

	public int getRouteCountByStopName(String stopName) {
		String sql = " select " + " b.name,b.direction " + " from stops st "
				+ " join rlbusstops rl on rl.idstop=st.id "
				+ " join buses b on b.id=rl.idbus " + " where st.name like \""
				+ stopName + "%" + "\"" + " group by " + " b.id,st.id ";
		Cursor cr = db.rawQuery(sql, null);
		int i = cr.getCount();
		cr.close();
		return i;
	}

}
