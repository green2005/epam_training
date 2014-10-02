package by.grodno.bus;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class RouteStopsActivity extends SherlockActivity{
	private String bus="";
	private String route="";
	private Cursor crBusStops=null;
	private PDBDataSource ds;
	private LayoutInflater inflater;
	private int routeStopsCount=0;
	private Resources res;
	
	
	private class RouteStopsAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return routeStopsCount;  
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			crBusStops.moveToPosition(arg0);
			return crBusStops.getString(0);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View cnView=convertView;
			if (cnView==null){
				cnView=inflater.inflate(R.layout.routestopsitem, null);
			};
			/*int colorOdd = res.getColor(android.R.color.holo_blue_light);
			int colorEven = res.getColor(android.R.color.transparent);
			if (position % 2 == 1) {
				cnView.setBackgroundColor(colorEven);
			} else {
				cnView.setBackgroundColor(colorOdd);
			}
			;
			*/
			cnView.refreshDrawableState();

			TextView routeStopTv=(TextView)cnView.findViewById(R.id.routestopTV);
			crBusStops.moveToPosition(position);
			routeStopTv.setText(crBusStops.getString(0));
			return cnView;
		}
		
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		setTheme(GrodnoBusActivity.THEME);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.routestops);
		Bundle b=getIntent().getExtras();
		if (b!=null){
			bus=b.getString("route");
			route=b.getString("direction");
		}
		setTitle(bus+" ("+route+")");
		ds=new PDBDataSource(getApplicationContext());
		ds.open();
		ListView routeStops=(ListView)findViewById(R.id.routeStopslv);
		RouteStopsAdapter adapter=new RouteStopsAdapter();
		routeStops.setAdapter(adapter);
		inflater=this.getLayoutInflater();
		crBusStops=ds.getBusStop(bus, route);
		routeStopsCount=ds.getBusRouteStopsCount(bus, route);
		routeStops.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				crBusStops.moveToPosition(arg2);
				String stopName=crBusStops.getString(0);
				Bundle b=new Bundle();
				b.putString("route", bus);
				b.putString("direction", route);
				b.putString("stop", stopName);
				Intent schedule=new Intent(getApplicationContext(),ScheduleActivity.class);
				schedule.putExtras(b);
				startActivity(schedule);
			}	
		});
		res=getResources();
	}
	
	protected void onDestroy(){
		super.onDestroy();
		try{
		ds.close();
		crBusStops.close();
		}catch(Exception e){e.printStackTrace();};
	}
	
	
	
	
}
