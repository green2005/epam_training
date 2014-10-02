package by.grodno.bus;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class StopRoutesActivity extends SherlockActivity{
	private String stopName;
	private String days[];
	private int qty;
	private Cursor maxTime;
	private Cursor nextDayTime;
	private PDBDataSource db;
	private String cTime;
	private String cDay1;
	private String cDay2;
	private LayoutInflater inflater;
	private Resources res;
	private String direction;
	private String route;
	private FavDBSource favdb;
	private MenuItem mFav;
	private Context context;
	String activeDay="";
	Adapter routesAdapter;
	ArrayAdapter<CharSequence> listAdapter;
	
	
	public class Adapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return qty;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			if (position>maxTime.getCount()-1){
				nextDayTime.moveToPosition(maxTime.getCount()-1-position);
				return nextDayTime.getString(0);
			} else
			{
				maxTime.moveToPosition(position);
				return maxTime.getString(0);
			}
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		
		private String getTimeDiff(String time){
			String sh=time.substring(0,2);
			String sm=time.substring(3,5);
			String ch=cTime.substring(0,2);
			String cm=cTime.substring(3,5);
			int h=Integer.parseInt(ch);
			int m=Integer.parseInt(cm);
			int nh=Integer.parseInt(sh);
			int nm=Integer.parseInt(sm);
			int res;
			if (h>nh){
				res=(nh-h+24)*60+nm-m;
			} else
			{
				res=(nh-h)*60+nm-m;
			};
		
			if (Math.abs(res)>60){
				return (int)(Math.abs((res)/60))+" ч "+Math.abs((res)%60)+" мин";
				
			}else{
				return Math.abs(res)+" мин";
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View cnView=convertView;
			if (cnView==null){
				cnView=inflater.inflate(R.layout.stoproutesitem, null);
			}
			int colorOdd = res.getColor(R.color.light_blue);
			int colorEven = res.getColor(android.R.color.transparent);
			if (position%2==0){
			//	cnView.setBackgroundColor(colorOdd);
			}else
			{
			//  cnView.setBackgroundColor(colorEven);
			}
			TextView routeNameView=(TextView)cnView.findViewById(R.id.routeNameView);
			TextView timeView1=(TextView)cnView.findViewById(R.id.time1View);
			TextView timeView2=(TextView)cnView.findViewById(R.id.time2View);
			 if (position<maxTime.getCount()){
				maxTime.moveToPosition(position);
				String route=maxTime.getString(0)+" ("+maxTime.getString(1)+")";
				routeNameView.setText(route);
				timeView1.setText("Следующий рейс: "+maxTime.getString(3));
				timeView2.setText("Через: "+getTimeDiff(maxTime.getString(3)));
			}else
			{
				nextDayTime.moveToPosition(position-maxTime.getCount());
				String route=nextDayTime.getString(0)+" ("+nextDayTime.getString(1)+")";
				routeNameView.setText(route);
				timeView1.setText("Следующий рейс: "+nextDayTime.getString(3));
				timeView2.setText("Через: "+getTimeDiff(nextDayTime.getString(3)));
			};
			return cnView;
		}
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		setTheme(GrodnoBusActivity.THEME);
		super.onCreate(savedInstanceState);
		Bundle b=getIntent().getExtras();
		stopName=b.getString("stopName");
		context=this;
		db=new PDBDataSource(getApplicationContext());
		db.open();
		
		favdb=new FavDBSource(this);
		favdb.open();
		//getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		setTitle(stopName);
		fillDays();
		
		setContentView(R.layout.routestops);
		inflater=getLayoutInflater();
		
		
		int dayNo=Calendar.getInstance().get(Calendar.getInstance().DAY_OF_WEEK)-Calendar.getInstance().getFirstDayOfWeek()+1;
		switch (dayNo) {
		
		
		case 1:  
			cDay1 = "Понедельник";
			cDay2 = "Рабочий";
			break;
		case 2:
			cDay1="Вторник";
			cDay2="Рабочий";
			break;
		case 3:
			cDay1="Среда";
			cDay2="Рабочий";
			break;
		case 4:
			cDay1="Четверг";
			cDay2="Рабочий";
			break;
		case 5:
			cDay1="Пятница";
			cDay2="Рабочий";
		case 6:  
			cDay1 = "Суббота";
			cDay2 = "Выходной";
			break;
		 
		case 0:  
			cDay1 = "Воскресенье";
			cDay2 = "Выходной";
			break;
		 
		default: {
			cDay1 = "Рабочий";
			cDay2="";
		}
		}
		
		for (String s:days){
			if (s.equals(cDay1)||(s.equals(cDay2)))
			{
				activeDay=s+"";
				break;
			}
		}
		
		Context context = getSupportActionBar().getThemedContext();
		 listAdapter = new ArrayAdapter<CharSequence>(
				context, R.layout.sherlock_spinner_item);
	   
		for (String day : days) {
			listAdapter.add(day);
		}
		if ((listAdapter.getPosition("Выходной")!=-1)&&
			(listAdapter.getPosition("Суббота")!=-1)&&
			(listAdapter.getPosition("Воскресенье")!=-1)){
			listAdapter.remove("Выходной");
		};
		if ((listAdapter.getPosition("Понедельник")!=-1)&&
				(listAdapter.getPosition("Вторник")!=-1)&&
				(listAdapter.getPosition("Среда")!=-1)&&
				(listAdapter.getPosition("Четверг")!=-1)&&
				(listAdapter.getPosition("Пятница")!=-1)&&
				(listAdapter.getPosition("Рабочий")!=-1)){
				listAdapter.remove("Рабочий");
			};
			
		
		
		listAdapter
		.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
		getSupportActionBar().setListNavigationCallbacks(listAdapter,
		new OnNavigationListener() {

			@Override
			public boolean onNavigationItemSelected(int itemPosition,
					long itemId) {
				activeDay = (String) listAdapter.getItem(itemPosition);
				prepareSchedule();
				routesAdapter.notifyDataSetChanged();
				return true;
			}
		});
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		res = getResources();
		getSupportActionBar().setSelectedNavigationItem(listAdapter.getPosition(activeDay));

	 	//prepareSchedule();
		ListView lv=(ListView)findViewById(R.id.routeStopslv);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String stopName;
				if (arg2<maxTime.getCount()){
					maxTime.moveToPosition(arg2);
					route=maxTime.getString(0);
					direction=maxTime.getString(1);
					stopName=maxTime.getString(2);
				}else{
					nextDayTime.moveToPosition(arg2-maxTime.getCount());
					route=nextDayTime.getString(0);
					direction=nextDayTime.getString(1);
					stopName=nextDayTime.getString(2);
				}
				Bundle b=new Bundle();
				b.putString("stop", stopName);
				b.putString("direction", direction);
				b.putString("route", route);
				Intent intent=new Intent(getApplicationContext(),ScheduleActivity.class);
				intent.putExtras(b);
				startActivity(intent);
			}
		});
		
		 routesAdapter=new Adapter();
		lv.setAdapter(routesAdapter);
	}
	
	private void fillDays(){
		int idStop = db.getStopId(stopName);
		Cursor crDays = db.getDays(idStop);
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
	
	private void fillDaysByActiveDay(){
		cDay1=activeDay;
		cDay2="";
	}
	
	private void prepareSchedule(){
	//	fillDaysByActiveDay();
		String ch=Integer.toString(Calendar.getInstance().get(Calendar.getInstance().HOUR_OF_DAY));
		String cm=Integer.toString(Calendar.getInstance().get(Calendar.getInstance().MINUTE));
		if (ch.length()<2)
			ch="0"+ch;
		if (cm.length()<2)
			cm="0"+cm;
		cTime=ch+"."+cm;
		res=getResources();
		ArrayList<String> aDays=new ArrayList<String>();
		if (!activeDay.equals(""))
		aDays.add(activeDay); else
			aDays.add(cDay1);
		
		//aDays.add(cDay2);
		if (aDays.indexOf("Рабочий")==-1){
			if ((aDays.indexOf("Понедельник")!=-1)||
				(aDays.indexOf("Вторник")!=-1)||
				(aDays.indexOf("Среда")!=-1)||
				(aDays.indexOf("Четверг")!=-1)||
				(aDays.indexOf("Пятница")!=-1)){
				aDays.add("Рабочий");
			}
		}	
		
		if ((aDays.indexOf("Выходной")!=-1) &&(aDays.indexOf("Суббота")==-1)){
			aDays.add("Суббота");
		}
		if ((aDays.indexOf("Выходной")!=-1) &&(aDays.indexOf("Воскресенье")==-1)){
			aDays.add("Воскресенье");
		}
		
		if ((aDays.indexOf("Воскресенье")!=-1) &&(aDays.indexOf("Выходной")==-1)){
			aDays.add("Выходной");
		}
		
		if ((aDays.indexOf("Суббота")!=-1) &&(aDays.indexOf("Выходной")==-1)){
			aDays.add("Выходной");
		}
		
		maxTime=db.getRouteMinTimeByStopName(cTime, stopName,aDays);
		maxTime.moveToFirst();
		String buses="";
		while (!maxTime.isAfterLast()){
			if (buses.equals(""))
			buses=maxTime.getString(4); else
				buses=buses+","+maxTime.getString(4);
			maxTime.moveToNext();
		}
		
		nextDayTime=db.getRouteNextTime(cTime, stopName,  buses, aDays);
		qty=maxTime.getCount()+nextDayTime.getCount();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
	 if (!favExists()){
			mFav=menu.add("");
			mFav.setIcon(R.drawable.favorities);
			mFav.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			mFav.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					// TODO Auto-generated method stub
					favdb.add(stopName);
					Toast.makeText(context, "Добавлено в избранное", Toast.LENGTH_LONG).show();
					mFav.setVisible(false);
					GrodnoBusActivity.favChanged=1;
					return true;
				}
			});
			return true;
		}else
			return false;
			/*
		SubMenu sub = menu.addSubMenu("submenu");
		MenuItem item = sub.getItem();
		item.setTitle("another subMenu");
		item.setIcon(R.drawable.abs__ic_menu_moreoverflow_normal_holo_dark);
		item=sub.add("Маршруты");
		item.setIcon(R.drawable.routes);
		if (!favExists()){
			item=sub.add("Добавить в избранное");
			
		
		}
		MenuItem item1 = sub.getItem();
		item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
		*/
	}
	
	private boolean favExists(){
		return favdb.existsInFav(stopName);
	}
	
	
	protected void onDestroy(){
		super.onDestroy();
		try{
		db.close();
		maxTime.close();
		nextDayTime.close();
		if (favdb!=null)
			favdb.close();
		}catch(Exception e){e.printStackTrace();};
	}

}
