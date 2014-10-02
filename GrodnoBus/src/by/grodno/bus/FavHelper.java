package by.grodno.bus;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

import java.util.ArrayList;

public class FavHelper {
	class FavAdapter extends BaseAdapter {
		Cursor cr;
		LayoutInflater inflater;
		Context context;
		private ArrayList<Integer> checkedItems;

		public FavAdapter(final Cursor fcr, Context fcontext) {
			this.cr = fcr;
			this.context = fcontext;
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			checkedItems = new ArrayList<Integer>();

		}

		@Override
		public int getCount() {
			return cr.getCount();
		}

		public void setDelFavMenuItem(final MenuItem delFavItem, final RelativeLayout bla) {
			Button delBtn=(Button)bla.findViewById(R.id.delBtn);
			delBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					for (int i : checkedItems) {
						cr.moveToPosition(i);
						String busName = cr.getString(cr
								.getColumnIndex("busName"));
						String stopName = cr.getString(cr
								.getColumnIndex("stopName"));
						String routeName = cr.getString(cr
								.getColumnIndex("routeName"));
						if ((busName != null) && (!busName.equals(""))) {
							favDb.remove(busName, routeName, stopName);
						} else
							favDb.remove(stopName);
					}
					cr.close();
					cr = favDb.getFavCr();
					FavHelper.this.cr=cr;
					notifyDataSetChanged();
					checkedItems.clear();
					delFavItem.setEnabled(false);
					delFavItem.setIcon(R.drawable.deldisabled);
					bla.setVisibility(View.GONE);
				}
			});
			
			
			
			delFavItem
					.setOnMenuItemClickListener(new OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							for (int i : checkedItems) {
								cr.moveToPosition(i);
								String busName = cr.getString(cr
										.getColumnIndex("busName"));
								String stopName = cr.getString(cr
										.getColumnIndex("stopName"));
								String routeName = cr.getString(cr
										.getColumnIndex("routeName"));
								if ((busName != null) && (!busName.equals(""))) {
									favDb.remove(busName, routeName, stopName);
								} else
									favDb.remove(stopName);
							}
							cr.close();
							cr = favDb.getFavCr();
							notifyDataSetChanged();
							checkedItems.clear();
							delFavItem.setEnabled(false);
							delFavItem.setIcon(R.drawable.deldisabled);
							return true;
						}
					});

		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View cnView;
			cnView = convertView;

			if (cnView == null) {
				cnView = (View) inflater.inflate(R.layout.favitem, null);
			}
			TextView busName = (TextView) cnView.findViewById(R.id.busName);
			TextView stopName = (TextView) cnView.findViewById(R.id.stopName);
			TextView routeName = (TextView) cnView.findViewById(R.id.routeName);
			TextView singleStopName = (TextView) cnView
					.findViewById(R.id.singleStopName);
			cr.moveToPosition(position);
			String st = cr.getString(cr.getColumnIndex("busName"));
			if ((st != null) && (!st.equals(""))) {
				busName.setVisibility(View.VISIBLE);
				stopName.setVisibility(View.VISIBLE);
				routeName.setVisibility(View.VISIBLE);
				singleStopName.setVisibility(View.GONE);

				busName.setText(cr.getString(cr.getColumnIndex("busName")));
				routeName.setText(cr.getString(cr.getColumnIndex("routeName")));
				stopName.setText(cr.getString(cr.getColumnIndex("stopName")));
			} else {
				busName.setVisibility(View.GONE);
				stopName.setVisibility(View.GONE);
				routeName.setVisibility(View.GONE);
				singleStopName.setVisibility(View.VISIBLE);
				singleStopName.setText(cr.getString(cr
						.getColumnIndex("stopName")));
			}
			CheckBox ch = (CheckBox) cnView.findViewById(R.id.checkItem);
			ch.setTag(-1);
			ch.setChecked(false);
			ch.setTag(position);
			ch.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Integer i = (Integer) ((CheckBox) (v)).getTag();
					if (i == -1)
						return;

					if (((CheckBox) v).isChecked()) {
						int j = checkedItems.indexOf(i);
						if (j == -1) {
							checkedItems.add(i);
						}
					} else {
						int j = checkedItems.indexOf(i);
						if (j >= 0) {
							checkedItems.remove(j);
						}
					}
					;
					if (checkedItems.size() > 0) {
						delFavMenuItem.setEnabled(true);
						delFavMenuItem.setIcon(R.drawable.delenabled);
						bla.setVisibility(View.VISIBLE);
					} else {
						delFavMenuItem.setEnabled(false);
						delFavMenuItem.setIcon(R.drawable.deldisabled);
						bla.setVisibility(View.GONE);
					}
					;
				}
			});
			int j = checkedItems.indexOf(position);
			ch.setChecked(j > -1);
			return cnView;
		}
	}

	private Cursor cr;
	private PDBDataSource db;
	private Context context;
	private ListView lv;
	private FavDBSource favDb;
	private FavAdapter adapter;
	private MenuItem delFavMenuItem;
	private RelativeLayout bla;

	FavHelper(ListView lv, PDBDataSource db, final Context context) {
		this.context = context;
		this.db = db;
		this.lv = lv;
		favDb = new FavDBSource(context);
		favDb.open();
		cr = favDb.getFavCr();
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		adapter = new FavAdapter(cr, context);
		lv.setAdapter(adapter);
		adapter.notifyDataSetChanged();

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//
				if (cr != null) {
					cr.moveToPosition(arg2);
					String stopName = cr.getString(cr
							.getColumnIndex("stopName"));
					String busName = cr.getString(cr
							.getColumnIndexOrThrow("busName"));
					String routeName = cr.getString(cr
							.getColumnIndex("routeName"));
					if ((busName != null) && (busName != "")) {
						Bundle b = new Bundle();
						b.putString("route", busName);
						b.putString("direction", routeName);
						b.putString("stop", stopName);
						Intent schedule = new Intent(context,
								ScheduleActivity.class);
						schedule.putExtras(b);
						context.startActivity(schedule);
						/*
						 * Bundle b=new Bundle(); b.putString("route", bus);
						 * b.putString("direction", route); b.putString("stop",
						 * stopName); Intent schedule=new
						 * Intent(getApplicationContext
						 * (),ScheduleActivity.class); schedule.putExtras(b);
						 * startActivity(schedule);
						 */
					} else {
						Intent stopRoutes = new Intent(context,
								StopRoutesActivity.class);
						Bundle b = new Bundle();
						b.putString("stopName", stopName);
						stopRoutes.putExtras(b);
						context.startActivity(stopRoutes);
						/*
						 * String stopName=crStops.getString(0); Intent
						 * stopRoutes=new
						 * Intent(getApplicationContext(),StopRoutesActivity
						 * .class); Bundle b=new Bundle();
						 * b.putString("stopName", stopName);
						 * stopRoutes.putExtras(b); startActivity(stopRoutes);
						 */

					}
				}
			}

		});
	}

	public void setDelFavMenuItem(MenuItem delFavMenuItem,RelativeLayout bla) {
		this.delFavMenuItem = delFavMenuItem;
		this.bla=bla;
		delFavMenuItem.setEnabled(false);
		delFavMenuItem.setIcon(R.drawable.deldisabled);
		if (adapter != null)
			adapter.setDelFavMenuItem(delFavMenuItem,bla);
	}
	
	public void refreshDelLayout(){
		if (bla!=null){
			bla.setVisibility(View.GONE);
		}
		if (adapter!=null){ 
			if ((adapter.checkedItems!=null)&&(adapter.checkedItems.size()>0)){
				bla.setVisibility(View.VISIBLE);
			}
		}
	}

	public void refresh() {
		if (cr != null) {
			cr.close();
		}
		cr = favDb.getFavCr();
		if (adapter != null) {
			if (adapter.checkedItems != null)
				adapter.checkedItems.clear();
			adapter.cr=cr;
		}
		adapter.notifyDataSetChanged();
	}

	public void close() {
		if (cr != null) {
			cr.close();
		}
		if (favDb != null) {
			favDb.close();
		}
	}

	public void add(String busName, String routeName, String stopName) {
		favDb.add(busName, routeName, stopName);
	}

	public void add(String stopName) {
		favDb.add(stopName);
	}

	public boolean existsInFav(String stopName) {
		return favDb.existsInFav(stopName);
	}

	public boolean existsInFav(String busName, String routeName, String stopName) {
		return favDb.existsInFav(busName, routeName, stopName);
	}

	public void remove(String busName, String routeName, String stopName) {
		favDb.remove(busName, routeName, stopName);
	}

	public void remove(String stopName) {
		favDb.remove(stopName);
	}

	public boolean isEmpty() {
		return cr.getCount() == 0;
	}
}
