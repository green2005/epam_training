package by.grodno.bus;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

public class FavDBSource extends SQLiteOpenHelper{
	Context context;
	private static String dbName="FavDb";
	private static int dbVersion=1;
	private String tableName="Fav";
	private SQLiteDatabase dataBase;
	
	public String getDBfileName(){
		File f=context.getDatabasePath(dbName);
		f.getAbsolutePath();
		return f.getAbsolutePath();
	}
	
	public FavDBSource(Context context) {
		super(context, dbName,null, dbVersion);
		this.context=context;
		// TODO Auto-generated constructor stub
	}

	public FavDBSource(Context context, String name, CursorFactory factory,
			int version) {
		super(context, dbName, factory, dbVersion);
		this.context=context;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql="CREATE TABLE Fav (id integer PRIMARY KEY AUTOINCREMENT UNIQUE,"+
			"busName varchar(10),routeName varchar(255),stopName varchar(255))";
		db.execSQL(sql);
		sql="CREATE TABLE android_metadata(locale TEXT,dbversion int )";
		try{
		//db.execSQL("CREATE TABLE android_metadata(locale TEXT,dbversion int )");
		}catch(Exception e){e.printStackTrace();};
		
		//sql=" insert into android_metadata(locale,dbversion) values(ru_RU,"+dbVersion+")";
		//db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql;
		if (dbVersion<newVersion){
			sql="drop table if exists "+tableName;
			db.execSQL(sql);
			
			sql="drop table if exists android_metadata";
			db.execSQL(sql);
			onCreate(db);
		}
		
	}
	
	public void open(){
		dataBase=this.getWritableDatabase();
		dataBase.close();
		dataBase=SQLiteDatabase.openDatabase(getDBfileName(), null ,SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.CREATE_IF_NECESSARY);// SQLiteDatabase.OPEN_READONLY); //openHelper.getWritableDatabase();
	}

	public boolean isFavEmpty(){
		return getFavCount()==0;
	}
	
	public int getFavCount(){
		String sql="select count(*) from Fav";
		Cursor cr=dataBase.rawQuery(sql, null);
		cr.moveToFirst();
		int i=cr.getInt(0);
		cr.close();
		return i;
	}
	
	public Cursor getFavCr(){
		String sql="select * from Fav";
		Cursor cr=dataBase.rawQuery(sql,null);
		return cr;
	}
	
	public void add(String busName,String routeName,String stopName){
		String sql="insert into Fav(busName,routeName,stopName)values(\""+
		busName+"\","+"\""+routeName+"\""+","+"\""+stopName+"\""+")";
		dataBase.execSQL(sql);
	}
	
	public void add(String stopName){
		String sql="insert into Fav(stopName)values(\""+stopName+"\""+")";
		dataBase.execSQL(sql);
	}
	
	public boolean existsInFav(String stopName){
		String sql="select * from Fav where stopName=\""+stopName+"\""+
		" and busName is null and routeName is null ";
		Cursor cr=dataBase.rawQuery(sql,null);
	    int i=cr.getCount();
		cr.close();
		return i>0;
	}
	
	public boolean existsInFav(String busName,String routeName,String stopName){
		String sql="select * from Fav where stopName=\""+stopName+"\""+
		" and busName=\""+busName+"\" and stopName =\""+stopName+"\"";
		Cursor cr=dataBase.rawQuery(sql,null);
		int i=cr.getCount();
		cr.close();
		return i>0;
	}
	
	public void remove(String busName,String routeName,String stopName){
		String sql="delete from Fav where stopName=\""+stopName+"\" and "+
		" routeName=\""+routeName+"\""+" and busName="+"\""+busName+"\"";
		dataBase.execSQL(sql);
		
	}
	/*
	  Fav (id integer PRIMARY KEY AUTOINCREMENT UNIQUE,"+
			"busName varchar(10),routeName varchar(255),stopName varchar(255))";
	 */
	
	public void remove(String stopName){
		 String sql=" delete from Fav where stopName=\""+stopName+"\""+" and routeName is Null and busName is Null ";
		 dataBase.execSQL(sql);
	}
	
	public synchronized void close(){
		if (dataBase!=null){
			dataBase.close();
		}
	}
	
}
