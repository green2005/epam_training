package com.smlivejournal.top;

import java.util.HashMap;
import java.util.List;

import com.smlivejournal.client.MainActivity;
import com.smlivejournal.client.R;
import com.smlivejournal.settings.Settings;
import com.smlivejournal.userblog.PostEditor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TopAdapter extends BaseAdapter {
	Context context;
	private LayoutInflater inflater;
	private List<HashMap<String, String>> list;
	private MainActivity mainActivity;
	private ListView mainListiew;
	private Settings settings;

	public TopAdapter(Context context, List<HashMap<String, String>> list,
			LayoutInflater inflater) {
		super();
		this.context = context;
		this.list = list;
		this.inflater = inflater;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
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
						String journal = map.get("journal");
						Intent ljIntent = new Intent(context, LJPost.class);
						Bundle b = new Bundle();
						b.putString("post_url", post_url);
						b.putString("journal", journal);
						b.putSerializable("settings", settings);
						ljIntent.putExtras(b);
						mainActivity.startActivityForResult(ljIntent,
								MainActivity.iSetFriendLine);
						// context.startActivity(ljIntent);
					}
				});

	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		View cnView;
		// TextView title;
		// TextView body;
		// TextView journal;
		// TextView timeView;
		// TextView replys;
		// TextView replysLabel;
		// final TextView tvEdit;
		HashMap<String, String> map;
		final ViewHolder holder;

		if (arg1 != null) {
			cnView = arg1;
			holder = (ViewHolder) cnView.getTag();
		} else {
			cnView = inflater.inflate(
					com.smlivejournal.client.R.layout.topsitem, null);
			holder = new ViewHolder();
			cnView.setTag(holder);
			holder.journal = (TextView) cnView
					.findViewById(com.smlivejournal.client.R.id.authorView);

			holder.timeView = (TextView) cnView
					.findViewById(com.smlivejournal.client.R.id.timeView);
			holder.title = (TextView) cnView
					.findViewById(com.smlivejournal.client.R.id.titleView);
			holder.body = (TextView) cnView
					.findViewById(com.smlivejournal.client.R.id.body);
			holder.replys = (TextView) cnView
					.findViewById(com.smlivejournal.client.R.id.commentQtyView);
			holder.replysLabel = (TextView) cnView
					.findViewById(com.smlivejournal.client.R.id.commentQtyLabel);

			holder.tvEdit = (TextView) cnView
					.findViewById(com.smlivejournal.client.R.id.editText);
		}

		map = list.get(arg0);
		holder.body.setText(map.get("body"));
		holder.timeView.setText(map.get("time"));
		String replyCount = map.get("reply_count");
		if (replyCount.equalsIgnoreCase("")) {
			holder.replys.setVisibility(View.GONE);
			holder.replysLabel.setVisibility(View.GONE);
		} else {
			holder.replys.setVisibility(View.VISIBLE);
			holder.replysLabel.setVisibility(View.VISIBLE);
			holder.replys.setText(map.get("reply_count") + "");
		}
		holder.title.setText(map.get("subject"));
		holder.journal.setText(map.get("journal"));
		if (map.containsKey("canedit")) {
			if (map.get("canedit").equalsIgnoreCase("1")) {
				holder.tvEdit.setVisibility(View.VISIBLE);
				holder.tvEdit.setTag(arg0);
			} else
				holder.tvEdit.setVisibility(View.GONE);
		} else
			holder.tvEdit.setVisibility(View.GONE);
		holder.tvEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int id = (Integer) holder.tvEdit.getTag();
				HashMap<String, String> map = list.get(id);
				String itemId = map.get("itemId");
				String url = map.get("post_url");
				Bundle b = new Bundle();
				b.putInt("reason", PostEditor.iEditPost);
				b.putString("itemId", itemId);
				b.putString("url", url);
				b.putSerializable("settings", settings);
				Intent i = new Intent(context, PostEditor.class);
				i.putExtras(b);
				mainActivity.startActivityForResult(i, MainActivity.iMakePost);
			};
		});

		holder.journal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String userName = ((TextView) v).getText().toString();
				userName = userName.replace("_", "-");
				if (mainActivity != null) {
					mainActivity.setUserLine(userName,"");
				}
				// String url="http://"+userName+".livejournal.com/data/rss";
				// Toast.makeText(context, url, Toast.LENGTH_SHORT).show();
			}
		});
		holder.setTextSize(settings);
		return cnView;
	}

	private class ViewHolder {
		TextView title;
		TextView body;
		TextView journal;
		TextView timeView;
		TextView replys;
		TextView replysLabel;
		TextView tvEdit;

		void setTextSize(Settings settings) {
			if (settings == null) {
				return;
			}
			settings.setTextViewSize(title,2);
			settings.setTextViewSize(body);
			settings.setTextViewSize(journal);
			settings.setTextViewSize(timeView);
			settings.setTextViewSize(replys);
			settings.setTextViewSize(replysLabel);
			settings.setTextViewSize(tvEdit);
		}
	}

}
