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
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

import java.util.ArrayList;
import java.util.List;

import by.grodno.indexableListView.IndexableListView;
import by.grodno.indexableListView.StringMatcher;

public class StopsActivity extends SherlockActivity{
	private PDBDataSource db;
	private Cursor crStops;
	private LayoutInflater inflater;
	private Resources res;
	private ArrayList<String> catalog;
	
	public class StopAdapter extends BaseAdapter
	{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return crStops.getCount();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			crStops.moveToPosition(position);
			return crStops.getString(0);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
				View cnView=convertView;
				if (cnView==null){
					cnView=inflater.inflate(R.layout.routestopsitem, null);
				}
				TextView stopsTv=(TextView)cnView.findViewById(R.id.routestopTV);
				crStops.moveToPosition(position);
				stopsTv.setText(crStops.getString(0));
				
				int colorOdd = res.getColor(R.color.light_blue);
				int colorEven = res.getColor(android.R.color.transparent);
				
				if (position%2==0){
				//	cnView.setBackgroundColor(colorOdd);
				}else
				{
				//	cnView.setBackgroundColor(colorEven);
				}
				return cnView;
		}
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		setTheme(GrodnoBusActivity.THEME);
		super.onCreate(savedInstanceState);
		setTitle("ŒÒÚ‡ÌÓ‚ÍË");
		inflater=getLayoutInflater();
		setContentView(R.layout.stops);
		db=new PDBDataSource(getApplicationContext());
		db.open();
		crStops= db.getStops();
		//StopAdapter adapter=new StopAdapter();
		catalog=new ArrayList<String>();
		crStops.moveToFirst();
		while (!crStops.isAfterLast()){
			catalog.add(crStops.getString(0));
			crStops.moveToNext();
		}
		ContentAdapter ca=new ContentAdapter(this, android.R.layout.simple_list_item_1, catalog);
        IndexableListView stopslv=(IndexableListView)findViewById(R.id.ixlv);
		stopslv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//
				crStops.moveToPosition(arg2);
				String stopName=crStops.getString(0);
				Intent stopRoutes=new Intent(getApplicationContext(),StopRoutesActivity.class);
				Bundle b=new Bundle();
				b.putString("stopName", stopName);
				stopRoutes.putExtras(b);
				startActivity(stopRoutes);
			}
		});
		stopslv.setAdapter(ca);
		stopslv.setFastScrollEnabled(true);
		res=getResources();
	}
	
	 private class ContentAdapter extends ArrayAdapter<String> implements SectionIndexer {
	    	
	    	private String mSections = "¿¡¬√ƒ≈®∆«» ÀÃÕŒœ–—“”‘’÷◊ÿŸ‹⁄›ﬁﬂ";
	    	
			public ContentAdapter(Context context, int textViewResourceId,
					List<String> objects) {
				super(context, textViewResourceId, objects);
			}
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				//v.setBackgroundResource(R.drawable.backgrounditem);
				return v;
			}
				

			@Override
			public int getPositionForSection(int section) {
				// If there is no item for current section, previous section will be selected
				for (int i = section; i >= 0; i--) {
					for (int j = 0; j < getCount(); j++) {
						if (i == 0) {
							// For numeric section
							for (int k = 0; k <= 9; k++) {
								if (StringMatcher.match(String.valueOf(getItem(j).charAt(0)), String.valueOf(k)))
									return j;
							}
						} else {
							if (StringMatcher.match(String.valueOf(getItem(j).charAt(0)), String.valueOf(mSections.charAt(i))))
								return j;
						}
					}
				}
				return 0;
			}

			@Override
			public int getSectionForPosition(int position) {
				return 0;
			}

			@Override
			public Object[] getSections() {
				String[] sections = new String[mSections.length()];
				for (int i = 0; i < mSections.length(); i++)
					sections[i] = String.valueOf(mSections.charAt(i));
				return sections;
			}
	    }
	
	protected void onDestroy(){
		super.onDestroy();
		try{
		db.close();
		crStops.close();
		}catch(Exception e){e.printStackTrace();};
	}
	

}
