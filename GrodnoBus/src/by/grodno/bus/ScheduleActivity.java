package by.grodno.bus;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class ScheduleActivity extends SherlockActivity {
	private String route = "";
	private String direction = "";
	private String stop = "";
	private int idBus = 0;
	private int idStop = 0;
	private PDBDataSource db;
	private String[] days;
	private int minTime;
	private int maxTime;
	private int hoursQty = 0;
	private LayoutInflater inflater;
	private Cursor scheduleCursor = null;
	private int[] scheduleMap;
	private Resources res;
	private String activeDay;
	private SubMenu sub;
	private ScheduleAdapter adapter;
	private ListView scheduleLv;
	private MenuItem mAddToFav;
	private FavDBSource favdb;
	private Context context;

	private class ScheduleAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return hoursQty;
		}

		@Override
		public Object getItem(int arg0) {
			if (arg0 + 5 <= 24)
				return Integer.toString(arg0 + 5);
			return Integer.toString(arg0 + 5 - 24);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View cnView = arg1;
			String h = "";
			if (cnView == null) {
				cnView = inflater.inflate(R.layout.scheduleitem, null);
			}
			// R.color.
			int colorOdd = res.getColor(R.color.light_blue);
			// (R.color.default_circle_indicator_page_color);
			int colorEven = res.getColor(android.R.color.transparent);
			if (arg0 % 2 == 1) {
			//	cnView.setBackgroundColor(colorEven);
			} else {
			//	cnView.setBackgroundColor(colorOdd);
			}
			;
			cnView.refreshDrawableState();

			TextView hourView = (TextView) cnView.findViewById(R.id.hourTitle);
			int hNo = arg0 + minTime;
			if (hNo < 10)
				h = "0" + hNo;
			else if (hNo > 23) {
				hNo -= 24;
				h = "0" + (hNo);
			} else
				h = hNo + "";
			hourView.setText(h);

			TextView tv = (TextView) cnView.findViewById(R.id.tv1);
			tv.setVisibility(View.INVISIBLE);
			tv = (TextView) cnView.findViewById(R.id.tv2);
			tv.setVisibility(View.INVISIBLE);
			tv = (TextView) cnView.findViewById(R.id.tv3);
			tv.setVisibility(View.INVISIBLE);
			tv = (TextView) cnView.findViewById(R.id.tv4);
			tv.setVisibility(View.INVISIBLE);
			tv = (TextView) cnView.findViewById(R.id.tv5);
			tv.setVisibility(View.INVISIBLE);
			tv = (TextView) cnView.findViewById(R.id.tv6);
			tv.setVisibility(View.INVISIBLE);
			tv = (TextView) cnView.findViewById(R.id.tv7);
			tv.setVisibility(View.INVISIBLE);
			tv = (TextView) cnView.findViewById(R.id.tv8);
			tv.setVisibility(View.INVISIBLE);

			// Cursor cr = db.getSchedule(idBus, idStop, day, h + ".");
			// cr.moveToFirst();
			int pos = 0;
			int id = 0;

			int crpos = scheduleMap[hNo];
			if (crpos == -1)
				return cnView;
			// scheduleCursor.getCount()
			scheduleCursor.moveToPosition(crpos);

			pos = 0;
			while (scheduleCursor.getString(0).contains(h + ".")) {
				switch (pos) {
				case 0: {
					id = R.id.tv1;
					break;
				}
				case 1: {
					id = R.id.tv2;
					break;
				}
				case 2: {
					id = R.id.tv3;
					break;
				}
				case 3: {
					id = R.id.tv4;
					break;
				}
				case 4: {
					id = R.id.tv5;
					break;
				}
				case 5: {
					id = R.id.tv6;
					break;
				}
				case 6: {
					id = R.id.tv7;
					break;
				}
				case 7: {
					id = R.id.tv8;
					break;
				}
				}
				pos++;
				tv = (TextView) cnView.findViewById(id);
				tv.setVisibility(View.VISIBLE);
				String s = scheduleCursor.getString(0).substring(3, 5);
				tv.setText(s);
				scheduleCursor.moveToNext();
				if (scheduleCursor.isAfterLast())
					break;
			}

			return cnView;
		}

	}

	private void prepareSchedule() {
		if (scheduleCursor != null)
			scheduleCursor.close();
		int ch=Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		int cm=Calendar.getInstance().get(Calendar.MINUTE);
		String nextBus="";
		
		
		scheduleCursor = db.getSchedule(idBus, idStop, activeDay, "");
		for (int i = 0; i < scheduleMap.length; i++) {
			scheduleMap[i] = -1;
		}
		String s = "";
		String prevHour = "";
		int j = 0;
		for (int i = 0; i < scheduleCursor.getCount(); i++) {
			scheduleCursor.moveToPosition(i);
			s = scheduleCursor.getString(0);
			if ((!s.substring(0, 2).equals(prevHour))||(nextBus.equals(""))) {
				if (s.substring(0, 1).equals("0"))
					s = s.substring(1, 2);
				else
					s = s.substring(0, 2);
				j = Integer.parseInt(s);

				if (!scheduleCursor.getString(0).substring(0, 2).equals(prevHour)){
					scheduleMap[j] = i;
					prevHour = scheduleCursor.getString(0).substring(0, 2);
					}

				
				if (nextBus.equals("")&&(j>=ch)){
					s = scheduleCursor.getString(0).substring(3,5);
					if (s.substring(0, 1).equals("0"))
						s = s.substring(1, 2);
					else
						s = s.substring(0, 2);
					int m=Integer.parseInt(s);
					if (j>ch){
						nextBus=scheduleCursor.getString(0);
					} else
						if ((j==ch)&&(m>cm)){
							nextBus=scheduleCursor.getString(0);
						}
			}
				
			}
		}

		for (int i = 4; i < 19; i++) {
			if (scheduleMap[i] != -1) {
				minTime = i;
				break;
			}
		}
		j = minTime + 1;
		while (j < 4) {
			if (scheduleMap[j] != -1) {
				maxTime = j;
			}
			j++;
		}
		if (maxTime < 5) {
			maxTime += 24;
		}
		hoursQty = maxTime - minTime + 1;
		if (nextBus.equals("")){
			scheduleCursor.moveToPosition(0);
			s = scheduleCursor.getString(0);
			if ((!s.substring(0, 2).equals(prevHour))||(nextBus.equals(""))) {
				if (s.substring(0, 1).equals("0"))
					s = s.substring(1, 2);
				else
					s = s.substring(0, 2);
				j = Integer.parseInt(s);
			if ((j<4)&&(ch>4)){
				nextBus=scheduleCursor.getString(0);
			}	
			}
		}
		
		if (!nextBus.equals("")){
			TextView nextB=(TextView)findViewById(R.id.timeView);
			nextB.setText(nextBus);
			s=nextBus;
			if (s.substring(0, 1).equals("0"))
				s = s.substring(1, 2);
			else
				s = s.substring(0, 2);
			int h=Integer.parseInt(s);
			s=nextBus.substring(3,5);
			if (s.substring(0, 1).equals("0"))
				s = s.substring(1, 2);
			int m=Integer.parseInt(s);
			if ((h<4)&&(ch>4)){
				h+=24;
			}
			int ht=Math.abs(h-ch);
			int mt=Math.abs(m-cm);
			int time=Math.abs(h*60+m-ch*60-cm);
			TextView  timeDistance=(TextView)findViewById(R.id.timeDistance);
			if (time>60){
				ht=(time-time%60)/60; 
				mt=time%60;
				timeDistance.setText(ht+" ч "+mt +" мин ");
				
			} else
			{
				mt=time%60;
				timeDistance.setText(mt +" мин ");
			}
		}else
		{
			TextView  timeDistance=(TextView)findViewById(R.id.timeDistance);
			timeDistance.setText("-");
			TextView nextB=(TextView)findViewById(R.id.timeView);
			nextB.setText("-");
		}
		
		int hNo = Calendar.getInstance().getTime().getHours();
		int pos=hNo-minTime;
		if (pos<0)pos+=24;
		try{
			scheduleLv.setSelection(pos);
		}catch(Exception e){e.printStackTrace();};
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(GrodnoBusActivity.THEME);
		super.onCreate(savedInstanceState);
		context=this;
		setContentView(R.layout.schedule);
		favdb=new FavDBSource(this);
		favdb.open();
		
		db = new PDBDataSource(this);
		db.open();
		inflater = getLayoutInflater();
		Bundle b = getIntent().getExtras();
		stop = b.getString("stop");
		direction = b.getString("direction");
		route = b.getString("route");
		setTitle(route + ", " + stop);

		idBus = db.getBusId(route, direction);
		idStop = db.getStopId(stop);
		fillDays();
		String firstName = "";
		String secondName;
		int dayNo=Calendar.getInstance().get(Calendar.getInstance().DAY_OF_WEEK)-Calendar.getInstance().getFirstDayOfWeek()+1;
		//,Calendar.LONG ,
			//	Locale.ENGLISH)
		//Calendar.getInstance().getFirstDayOfWeek()
		//Calendar.getInstance().getDisplayName(Calendar.getInstance().DAY_OF_WEEK, Calendar.getInstance().LONG, Locale.ENGLISH)
		switch (dayNo) {
		case 1:  
			firstName = "Понедельник";
			secondName = "Рабочий";
			break;
		case 2:
			firstName="Вторник";
			secondName = "Рабочий";
			break;
		case 3:
			firstName="Среда";
			secondName = "Рабочий";
			break;
		case 4:
			firstName="Четверг";
			secondName="Рабочий";
			break;
		case 5:
			firstName="Пятница";
			secondName="Рабочий";
			break;
		case 6:  
			firstName = "Суббота";
			secondName = "Выходной";
			break;
		case 0:  
			firstName = "Воскресенье";
			secondName = "Выходной";
			break;
		 
		default: {
			secondName = "Рабочий";
		}
		}
		for (String s:days){
			if (s.equalsIgnoreCase(firstName)||(s.equalsIgnoreCase(secondName)))
			{
				activeDay=s+"";
				break;
			}
		}

		scheduleLv = (ListView) findViewById(R.id.schedulelv);
		adapter = new ScheduleAdapter();
		scheduleLv.setAdapter(adapter);
		scheduleMap = new int[24];

		Context context = getSupportActionBar().getThemedContext();
		ArrayAdapter<CharSequence> listAdapter = new ArrayAdapter<CharSequence>(
				context, R.layout.sherlock_spinner_item);
	   
		for (String day : days) {
			listAdapter.add(day);
		}
		
  		listAdapter
				.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
		getSupportActionBar().setListNavigationCallbacks(listAdapter,
				new OnNavigationListener() {

					@Override
					public boolean onNavigationItemSelected(int itemPosition,
							long itemId) {
						activeDay = days[itemPosition]+"";
						prepareSchedule();
						adapter.notifyDataSetChanged();
						return true;
					}
				});
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		res = getResources();
		getSupportActionBar().setSelectedNavigationItem(listAdapter.getPosition(activeDay));
		
		
 

	}
	
	private void fillDays(){
		Cursor crDays = db.getDays(idBus, idStop);
		days = new String[crDays.getCount()];
		HashMap<Integer,String> map=new HashMap<Integer,String>();
		
		for (int i = 0; i < crDays.getCount(); i++) {
			crDays.moveToPosition(i);
			String s=crDays.getString(0);
			if (s.equalsIgnoreCase("Понедельник"))
			{map.put(1,"Понедельник");} else
			if (s.equalsIgnoreCase("Вторник")){	
			map.put(2,"Вторник");} else
			if (s.equalsIgnoreCase("Среда")) {	
			map.put(3,"Среда");} else
			if (s.equalsIgnoreCase("Четверг")){
			map.put(4,"Четверг");} else
			if (s.equalsIgnoreCase("Пятница")) 	
			{map.put(5,"Пятница");} else
			if (s.equalsIgnoreCase("Суббота"))	
			{map.put(6,"Суббота");}
			else if (s.equalsIgnoreCase("Воскресенье"))
			{
			map.put(7,"Воскресенье");}
			else if (s.equalsIgnoreCase("Выходной")){
			map.put(8,"Выходной");} else
				if (s.equalsIgnoreCase("Рабочий")){
			map.put(9,"Рабочий");}
		}
		int j=0;
		for (int i=1;i<10;i++){
			if (map.containsKey((Integer)i)){
				days[j]=map.get(i);
				j++;
			}
		}
		crDays.close();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
	 
		SubMenu sub = menu.addSubMenu("submenu");
		MenuItem item = sub.getItem();
		item.setTitle("another subMenu");
		if (R.style.Theme_Sherlock_Light==GrodnoBusActivity.THEME)
		item.setIcon(R.drawable.abs__ic_menu_moreoverflow_normal_holo_light);else
		item.setIcon(R.drawable.abs__ic_menu_moreoverflow_normal_holo_dark);
		item=sub.add("Все автобусы на остановке");
		item.setIcon(R.drawable.stops);
		item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Bundle b=new Bundle();
				b.putString("stopName",stop);
				Intent sr=new Intent(context, StopRoutesActivity.class);
				sr.putExtras(b);
				startActivity(sr);
				return true;
			}
		});
		
		
		if (!favExists()){
			mAddToFav=sub.add("Добавить в избранное");
			mAddToFav.setIcon(R.drawable.favorities);
			mAddToFav.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					favdb.add(route,direction,stop);
					Toast.makeText(context, "Добавлено в избранное", Toast.LENGTH_LONG).show();
					mAddToFav.setVisible(false);
					GrodnoBusActivity.favChanged=1;
					return true;
				}
			});
		
		}
		
		MenuItem item1 = sub.getItem();
		item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
		/*
		
		
		if (!favExists()){
		mAddToFav=menu.add("Избранное");
		Drawable f=getResources().getDrawable(R.drawable.favorities);
		mAddToFav.setIcon(f);
		mAddToFav.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		mAddToFav.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				favdb.add(route,direction,stop);
				Toast.makeText(context, "Добавлено в избранное", Toast.LENGTH_LONG).show();
				mAddToFav.setVisible(false);
				GrodnoBusActivity.favChanged=1;
				return true;
			}
		});
		return true;
		}else
			return false;
			*/
	}
	
	private boolean favExists(){
		return favdb.existsInFav( route,direction, stop);
	}
	
	protected void onDestroy(){
		super.onDestroy();
		try{
		  db.close();
		  scheduleCursor.close();
		  if (favdb!=null)
			  favdb.close();
		}catch(Exception e){e.printStackTrace();};
	}
}
