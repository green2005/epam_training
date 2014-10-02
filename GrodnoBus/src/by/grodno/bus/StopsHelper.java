package by.grodno.bus;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;

import java.util.ArrayList;
import java.util.List;

import by.grodno.indexableListView.IndexableListView;
import by.grodno.indexableListView.StringMatcher;

public class StopsHelper implements OnItemClickListener{
	private PDBDataSource db;
	private Context context;
	IndexableListView ixlv;
	Cursor cr=null;
	ArrayList<String> catalog;
	
	StopsHelper(PDBDataSource db,Context context,IndexableListView ixlv){
		this.db=db;
		this.ixlv=ixlv;
		this.context=context;
		cr= db.getStops();
		//StopAdapter adapter=new StopAdapter();
		catalog=new ArrayList<String>();
		cr.moveToFirst();
		while (!cr.isAfterLast()){
			catalog.add(cr.getString(0));
			cr.moveToNext();
		}
		ContentAdapter ca=new ContentAdapter(context, android.R.layout.simple_list_item_1, catalog);
		ixlv.setAdapter(ca);
		ixlv.setFastScrollEnabled(true);
		ixlv.setOnItemClickListener(this);
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
	
	void close(){
		if (cr!=null){
			cr.close();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		cr.moveToPosition(arg2);
		String stopName=cr.getString(0);
		Intent stopRoutes=new Intent(context,StopRoutesActivity.class);
		Bundle b=new Bundle();
		b.putString("stopName", stopName);
		stopRoutes.putExtras(b);
		context.startActivity(stopRoutes);
	}
}
