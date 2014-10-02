package com.smlivejournal.friends;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.smlivejournal.client.MainActivity;
import com.smlivejournal.client.R;
import com.smlivejournal.settings.AuthThread;
import com.smlivejournal.settings.Settings;
import com.smlivejournal.top.LJPost;
import com.smlivejournal.top.MainTop;
import com.smlivejournal.top.TopAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FriendLine extends Fragment implements OnScrollListener, OnNavigationListener {

	
	private Context context;
	private LayoutInflater inflater;
	// private View mainActivityView;
	private View progressView;
	private Settings settings;
	private ArrayList<HashMap<String, String>> friendLineList;
	private ArrayList<HashMap<String, String>> tmpList;
	private View selfView;
	private ListView mainListView;
	private TopAdapter adapter;
	private boolean prevExists;
	private String prevUrl;
	private int visibleItemCount;
	private int prevTopItem;
	private boolean isLoading;
	private View loadMoreView;
	private ArrayList<HashMap<String, String>> filters;
	private String prevFilter = "";
	private boolean groupsLoaded = false;
	private boolean isRestoring=false;
	MainActivity mainActivity;

	public FriendLine(MainActivity mainActivity, LayoutInflater inflater,
			View progressView, Settings settings) {
		this.context = mainActivity;
		this.mainActivity = mainActivity;
		this.inflater = inflater;
		this.progressView = progressView;
		this.settings = settings;
		friendLineList = new ArrayList<HashMap<String, String>>();
		tmpList = new ArrayList<HashMap<String, String>>();
		initFilters();
		// restoreSettings();
	}
	
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
			if (isLoading) {
				//isLoading=false;
				return true;
			};
			if (isRestoring){
				isRestoring=false;
				return true;
			}

			String sv = "";
			HashMap<String, String> map = filters.get(itemPosition);
			for (String name : map.keySet()) {
				sv = map.get(name);
			}
			// if (!sv.equalsIgnoreCase(""))
			setFriendLine(sv);
			saveSettings();
			return true;
		}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		boolean restore=selfView!=null;
		
		
		selfView = inflater.inflate(
					com.smlivejournal.client.R.layout.topmainview, null);

			ListView lv = (ListView) selfView.findViewById(R.id.lvtops);
			lv.setOnScrollListener(this);
		if (restore){
			mainListView = (ListView) selfView.findViewById(R.id.lvtops);
			// if (adapter == null) {
			//adapter = new TopAdapter(context, friendLineList, inflater);
			adapter.setSettings(settings);
			mainListView.setAdapter(adapter);
			Activity main = ((Activity) context);
			adapter.setMainActivity((MainActivity) main);
			adapter.setListView(mainListView);	
			
			
			final ArrayAdapter<CharSequence> topList = new ArrayAdapter<CharSequence>(
					mainActivity.getSupportActionBar().getThemedContext(),
					R.layout.sherlock_spinner_item);

			for (HashMap<String, String> map : filters) {
				for (String name : map.keySet()) {
					topList.add(name);
				}
				// topList.add(name);
			}

			topList.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

			mainActivity.getSupportActionBar().setNavigationMode(
					ActionBar.NAVIGATION_MODE_LIST);
			mainActivity.getSupportActionBar().setListNavigationCallbacks(topList,
					this);
			isRestoring=true;
			restoreSettings();
			//isLoading=false;
		}
		mainActivity.getSupportActionBar().setTitle(
				com.smlivejournal.client.R.string.sFriends);
		return selfView;
	}


	@Override
	public void onDetach() {
		int i=mainActivity.getSupportFragmentManager().getBackStackEntryCount();
		if (i==0){
			mainActivity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			String title=getResources().getString(com.smlivejournal.client.R.string.app_name);
			mainActivity.getSupportActionBar().setTitle(title);
		}
		super.onDetach();
	}

	public void setLoadMoreView(View loadMoreView) {
		this.loadMoreView = loadMoreView;
	}

	public void hideMenu() {
		mainActivity.getSupportActionBar().setNavigationMode(
				ActionBar.NAVIGATION_MODE_STANDARD);
	}

	public void showMenu() {
		if (!groupsLoaded)
			return;
		final ArrayAdapter<CharSequence> topList = new ArrayAdapter<CharSequence>(
				mainActivity.getSupportActionBar().getThemedContext(),
				R.layout.sherlock_spinner_item);

		for (HashMap<String, String> map : filters) {
			for (String name : map.keySet()) {
				topList.add(name);
			}
			// topList.add(name);
		}

		topList.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

		mainActivity.getSupportActionBar().setNavigationMode(
				ActionBar.NAVIGATION_MODE_LIST);
		mainActivity.getSupportActionBar().setSelectedNavigationItem(0);
		mainActivity.getSupportActionBar().setListNavigationCallbacks(topList,
				this);
		// mainActivity.getSupportActionBar().setSelectedNavigationItem(0);

	}

	public View getListView() {
		return selfView;
	}

	public void setFriendLine(String filter) {

		setProgressVisible(true);
		selfView.setVisibility(View.VISIBLE);
		mainListView = (ListView) selfView.findViewById(R.id.lvtops);
		// if (adapter == null) {
		adapter = new TopAdapter(context, friendLineList, inflater);
		adapter.setSettings(settings);
		mainListView.setAdapter(adapter);
		Activity main = ((Activity) context);
		adapter.setMainActivity((MainActivity) main);
		adapter.setListView(mainListView);
		// }

		friendLineList.clear();
		adapter.notifyDataSetChanged();
		prevFilter = filter;
		readFriendLine("", filter);
	}

	public void saveSettings() {
		SharedPreferences prefs = context.getSharedPreferences("friendLine",
				Context.MODE_PRIVATE);
		Editor e = prefs.edit();
		e.putString("friendLineFilter", prevFilter);
		e.apply();
	}

	public void restoreSettings() {
		if (!groupsLoaded) {
			return;
		}
		;

		SharedPreferences prefs = context.getSharedPreferences("friendLine",
				Context.MODE_PRIVATE);
		String s = prefs.getString("friendLineFilter", "");

		prevFilter = "";
		if (!s.equalsIgnoreCase("")) {
			for (HashMap<String, String> map : filters) {
				for (String key : map.keySet()) {
					String v = map.get(key);
					if (v.equalsIgnoreCase(s)) {
						int i = filters.indexOf(map);
						mainActivity.getSupportActionBar()
								.setSelectedNavigationItem(i);
						prevFilter = v;
						return;
					}
				}
			}
		}
		mainActivity.getSupportActionBar().setSelectedNavigationItem(0);
	}

	public void readFriendLine(String url, String filter) {
		if ((settings.getPwd().equalsIgnoreCase(""))
				|| (settings.getUserName().equalsIgnoreCase(""))) {
			return;
		}
		tmpList.clear();
		Handler mainHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.getData() != null) {
					Bundle b = msg.getData();
					if (b.containsKey("prevExists")) {
						prevExists = b.getBoolean("prevExists");
					} else {
						prevExists = false;
					}
					if (b.containsKey("prevUrl")) {
						prevUrl = b.getString("prevUrl");
					}
				}

				friendLineList.addAll(tmpList);
				tmpList.clear();
				adapter.notifyDataSetChanged();
				setProgressVisible(false);
				setLoadMoreVisible(false);
				isLoading = false;
			};
		};
		AuthThread thread = new AuthThread(mainHandler,
				AuthThread.iGetFriendLine, settings.getUserName(),
				settings.getPwd());
		thread.setUrl(url);
		thread.setFilter(filter);
		thread.setSettings(settings);
		thread.setContext(context);
		thread.setList(tmpList);
		thread.start();

	}

	private void setProgressVisible(boolean visible) {
		if (progressView != null) {
			if (visible) {
				progressView.setVisibility(View.VISIBLE);
			} else {
				progressView.setVisibility(View.GONE);
			}
		}
	}

	private void setLoadMoreVisible(boolean visible) {
		if (loadMoreView != null) {
			if (visible) {
				loadMoreView.setVisibility(View.VISIBLE);
			} else {
				loadMoreView.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.visibleItemCount = visibleItemCount;
		int i = firstVisibleItem + visibleItemCount;
		if ((totalItemCount > 0) && (i == totalItemCount) && (!isLoading)
				&& (prevTopItem < firstVisibleItem)
				&& (!prevUrl.equalsIgnoreCase(""))) {
			isLoading = true;
			setLoadMoreVisible(true);
			loadMoreData();
		}
		prevTopItem = firstVisibleItem;
	}

	private void loadMoreData() {
		if (!prevUrl.equalsIgnoreCase("")) {
			readFriendLine(prevUrl, prevFilter);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	private void initFilters() {
		filters = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map;

		Handler mainHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Bundle b = msg.getData();
				if (b != null) {
					if (!b.containsKey("friendgroups")) {
						return;
					}
					Hashtable result = (Hashtable) b.get("friendgroups");
					for (int i = 0; i < ((Vector) (result).get("friendgroups"))
							.size(); i++) {
						Hashtable<String, Object> group = ((Hashtable<String, Object>) ((Vector) ((result))
								.get("friendgroups")).get(i));
						String id = group.get("id").toString();
						String name = "";
						if (group.get("name").getClass().getSimpleName()
								.equalsIgnoreCase("String")) {
							name = new String((String) group.get("name"));
						} else
							name = new String((byte[]) group.get("name"));
						HashMap<String, String> map;
						map = new HashMap<String, String>();
						map.put(name, id);
						filters.add(map);
					}
				}
				groupsLoaded = true;
				showMenu();
				restoreSettings();
			}
		};

		AuthThread thread = new AuthThread(mainHandler,
				AuthThread.iGetFriendsGroups);
		thread.setSettings(settings);
		thread.setContext(context);

		map = new HashMap<String, String>();
		map.put(context.getResources().getString(
				com.smlivejournal.client.R.string.dDefault),
				context.getResources().getString(
						com.smlivejournal.client.R.string.vDefault));
		filters.add(map);

		map = new HashMap<String, String>();
		map.put(context.getResources().getString(
				com.smlivejournal.client.R.string.dOnlyJournal),
				context.getResources().getString(
						com.smlivejournal.client.R.string.vOnlyJournal));
		filters.add(map);

		map = new HashMap<String, String>();
		map.put(context.getResources().getString(
				com.smlivejournal.client.R.string.dOnlyGroups),
				context.getResources().getString(
						com.smlivejournal.client.R.string.vOnlyGroups));
		filters.add(map);

		map = new HashMap<String, String>();
		map.put(context.getResources().getString(
				com.smlivejournal.client.R.string.dOnlyTrans),
				context.getResources().getString(
						com.smlivejournal.client.R.string.vOnlyTrans));
		filters.add(map);

		thread.start();

	}

	private Map<? extends String, ? extends String> put(String string,
			String string2) {
		// TODO Auto-generated method stub
		return null;
	}

}
