package by.grodno.bus;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

public class RoutesLvHelper implements OnChildClickListener {
	private Context context;
	private ExpandableListView lv;
	private PDBDataSource db;
	private Adapter adapter;
	private Cursor crRoutes=null;
	private LayoutInflater inflater;
	private OnChildClickListener chilListener;
	
	RoutesLvHelper(Context context,ExpandableListView lv,PDBDataSource db){
		this.context=context;
		this.lv=lv;
		this.db=db;
		adapter=new Adapter();
		crRoutes=db.getRoutes();
		inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		lv.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		lv.setOnChildClickListener(this);
	}
	
	public void close(){
		//
		if (crRoutes!=null)
			crRoutes.close();
		
	}
	
	
class Adapter extends BaseExpandableListAdapter{
	

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		crRoutes.moveToPosition(groupPosition);
		String s=db.getRouteChild(crRoutes.getString(0), childPosition);
		return s;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View cnView=convertView;
		if (cnView==null){
			cnView=inflater.inflate(R.layout.routechild, null);
		}
		TextView childText=(TextView)cnView.findViewById(R.id.textChild);
		String s=(String)getChild(groupPosition, childPosition);
		childText.setText(s);
		return cnView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		crRoutes.moveToPosition(groupPosition);
		return db.getRouteDirCount(crRoutes.getString(0));
	}

	@Override
	public Object getGroup(int groupPosition) {
		crRoutes.moveToPosition(groupPosition);
		return crRoutes.getString(0);
	}

	@Override
	public int getGroupCount() {
		return crRoutes.getCount();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View cnView=convertView;
		if (cnView==null){
			cnView=inflater.inflate(R.layout.routetitle, null);
		}
	/*	int colorOdd = res.getColor(android.R.color.holo_blue_light);
		int colorEven = res.getColor(android.R.color.transparent);
		if (groupPosition % 2 == 1) {
			cnView.setBackgroundColor(colorEven);
		} else {
			cnView.setBackgroundColor(colorOdd);
		}
		;*/
		cnView.refreshDrawableState();

		TextView view=(TextView)cnView.findViewById(R.id.textTitle);
		crRoutes.moveToPosition(groupPosition);
		view.setText(crRoutes.getString(0));
		return cnView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}
}


@Override
public boolean onChildClick(ExpandableListView parent, View v,
		int groupPosition, int childPosition, long id) {
	Intent stops=new Intent(context,RouteStopsActivity.class);
	Bundle b=new Bundle();
	crRoutes.moveToPosition(groupPosition);
	String route=crRoutes.getString(0);
	String direction=db.getRouteChild(route, childPosition);
	b.putString("route", route);
	b. putString("direction", direction);
	
	stops.putExtras(b);
	context.startActivity(stops);
	return true;
}	

}
