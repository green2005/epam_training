package com.smlivejournal.top;

import java.util.HashMap;
import java.util.List;

import com.smlivejournal.client.MainActivity;
import com.smlivejournal.settings.Settings;

import android.R;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MemoriesAdapter extends BaseAdapter {
	Context context;
	private LayoutInflater inflater;
	private List<HashMap<String, String>> list;
	private MainActivity mainActivity;
	private ListView mainListiew;
	private Settings settings;

	
	public MemoriesAdapter(Context context, List<HashMap<String, String>> list,
			LayoutInflater inflater) {
		super();
		this.context=context;
		this.inflater=inflater;
		this.list=list;
	}
	
	public void setSettings(Settings settings){
		this.settings=settings;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public void setMainActivity(MainActivity activity) {
		this.mainActivity = activity;
	}

	
	public void setListView(ListView mainListView) {
		this.mainListiew = mainListView;
		mainListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						HashMap<String, String> map = list.get(arg2);
						String post_url = map.get("post_url");
						//String journal = map.get("journal");
						Intent ljIntent = new Intent(context, LJPost.class);
						Bundle b = new Bundle();
						b.putString("post_url", post_url);
						b.putSerializable("settings",settings);
						//b.putString("journal", journal);
						ljIntent.putExtras(b);
						mainActivity.startActivityForResult(ljIntent, 0);
						// context.startActivity(ljIntent);
					}
				});

	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView tvText;
		if (convertView==null){
			convertView=inflater.inflate(com.smlivejournal.client.R.layout.memoriesitem, null);
		};
		tvText=(TextView)convertView.findViewById(com.smlivejournal.client.R.id.tvCommentsText);
		if (tvText!=null){
			HashMap<String,String> map=list.get(position);
			tvText.setText(map.get("body"));
		}
		return convertView;
	}

}
