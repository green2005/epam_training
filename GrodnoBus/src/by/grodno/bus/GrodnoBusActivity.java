package by.grodno.bus;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

import by.grodno.indexableListView.IndexableListView;

public class GrodnoBusActivity extends SherlockActivity implements TabListener, OnCopyDone{
    public static int THEME = R.style.Theme_Sherlock_Light;
    private int prevTheme=THEME;
	private String dbDate;
	private TextView dbDateTv; 
	private ExpandableListView routesLv;
	private PDBDataSource db;
	private RoutesLvHelper routesHelper;
	private StopsHelper stopsHelper;
	private IndexableListView stopsLv;
	private TextView favListEmptyTv;
	private ListView favListView;
	private FavHelper favHelper;
	private MenuItem delFav;
	private Context context;
	public static int favChanged=0;
	RelativeLayout bla;
	 
	
	private void fillDBDate(){
		Resources res=getResources();
		dbDate=res.getString(R.string.scheduleDate);
		SharedPreferences pref= getSharedPreferences("by.grodno.bus",Activity.MODE_PRIVATE );
		dbDate=pref.getString("scheduleDate", dbDate);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		
		delFav=menu.add("Удалить из избранного");
		delFav.setIcon(R.drawable.deldisabled);
		if (favHelper!=null){
			favHelper.setDelFavMenuItem(delFav,bla);
		}
		delFav.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS|MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		delFav.setVisible(false);
		
		MenuItem options=menu.add("Настройки");
		options.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			 
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent i=new Intent(context,OptionsActivity.class);
				startActivity(i);
				return true;
			}
		});
		
		options.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS|MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		options.setIcon(R.drawable.settings);
		
		return true;
	}
	
	@Override
	public void onRestoreInstanceState(Bundle b){
		super.onRestoreInstanceState(b);
		
	}
	
	

	
	
	/* @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        SubMenu sub = menu.addSubMenu("Theme");
	        sub.add(0, R.style.Theme_Sherlock, 0, "Default");
	        sub.add(0, R.style.Theme_Sherlock_Light, 0, "Light");
	        sub.add(0, R.style.Theme_Sherlock_Light_DarkActionBar, 0, "Light (Dark Action Bar)");
	        sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	        return true;
	    } 
	
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
	            return false;
	        }
	        THEME = item.getItemId();
	        Toast.makeText(this, "Theme changed to \"" + item.getTitle() + "\"", Toast.LENGTH_SHORT).show();
	        return true;
	    }
	 */ 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	SharedPreferences pref=getSharedPreferences("by.grodno.bus", Activity.MODE_PRIVATE);
    	String s=pref.getString("style", "1");
    	if (s.equals("0")||(s.equals("R.style.Theme_Sherlock"))){
			GrodnoBusActivity.THEME=R.style.Theme_Sherlock;
		} else
			if (s.equals("1")||(s.equals("R.style.Theme_Sherlock_Light"))){
				GrodnoBusActivity.THEME=R.style.Theme_Sherlock_Light;
			}else if (s.equals("2")||s.equals("R.style.Theme_Sherlock_Light_DarkActionBar")){
				GrodnoBusActivity.THEME=R.style.Theme_Sherlock_Light_DarkActionBar;
			}
    	setTheme(THEME);
    	prevTheme=THEME;
        super.onCreate(savedInstanceState);
        
        
        db=new PDBDataSource(this);
        db.setOnCopyDoneListener(this);
        db.createDataBase();
        
       }
    
     
    
    @Override
    protected void onStart(){
    	super.onStart();
    	if (prevTheme!=THEME){
    		Intent main = new Intent(context,
					GrodnoBusActivity.class);
			startActivity(main);
			GrodnoBusActivity.this.finish();
    	}
    	
    	if (favChanged==1){
			if (favHelper!=null)
				favHelper.refresh();
			favChanged=0;
		}
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	if (db!=null)
    		db.close();
    	if (routesHelper!=null)
    		routesHelper.close();
    	if (stopsHelper!=null)
    		stopsHelper.close();
    	if (favHelper!=null){
    		favHelper.close();
    	}
    }
    
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (tab.getPosition()==0){
			routesLv.setVisibility(View.VISIBLE);
			stopsLv.setVisibility(View.GONE);
			
			favListEmptyTv.setVisibility(View.GONE);
			favListView.setVisibility(View.GONE);
			if (delFav!=null){
				delFav.setVisible(false);
			}
			bla.setVisibility(View.GONE);
		}else
		if (tab.getPosition()==1)
		{
			routesLv.setVisibility(View.GONE);
			stopsLv.setVisibility(View.VISIBLE);
		
			favListEmptyTv.setVisibility(View.GONE);
			favListView.setVisibility(View.GONE);
			if (delFav!=null){
				delFav.setVisible(false);
			}
			bla.setVisibility(View.GONE);
		}else
		if (tab.getPosition()==2){
			routesLv.setVisibility(View.GONE);
			stopsLv.setVisibility(View.GONE);
			favHelper.refreshDelLayout();
		//	if (delFav!=null){
			//	delFav.setVisible(true);
			//} 
		
			if (favHelper.isEmpty()){
				favListEmptyTv.setVisibility(View.VISIBLE);
				favListView.setVisibility(View.GONE);
			} else
			{
				favListEmptyTv.setVisibility(View.GONE);
				favListView.setVisibility(View.VISIBLE);
			}	
		};	
		 	
	}
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCopydone() {
		setContentView(R.layout.main);
        setTitle("Расписание автобусов");
        context=this;
        bla=(RelativeLayout)findViewById(R.id.bla);
        favListView=(ListView)findViewById(R.id.favLv);
        favListEmptyTv=(TextView)findViewById(R.id.favEmptyTv);
        favHelper=new FavHelper(favListView, db, this);
		
		db.open();
        db.close();
        
        db=new PDBDataSource(getApplicationContext());
		db.open();


       routesLv=(ExpandableListView) findViewById(R.id.routeslv);
		routesHelper=new RoutesLvHelper(this, routesLv, db);
        
		stopsLv=(IndexableListView)findViewById(R.id.ixlv);
		stopsHelper=new StopsHelper(db, this, stopsLv);
		
		 getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	        Tab tab=getSupportActionBar().newTab();
	        //tab.setText(getResources().getString(R.string.routes));
	        tab.setIcon(R.drawable.routes);
	        tab.setTabListener(this);
	       // tab.setTag(R.string.routes);
	        getSupportActionBar().addTab(tab);
	        
	         
	        tab=getSupportActionBar().newTab();
	        //tab.setText(getResources().getString(R.string.stops));
	        tab.setIcon(R.drawable.stops);
	        tab.setTabListener(this);
	        tab.setTag(R.string.stops);
	        getSupportActionBar().addTab(tab);
	        
	        tab =getSupportActionBar().newTab();
	       // tab.setText(getResources().getString(R.string.favorities));
	        tab.setIcon(R.drawable.favorities);
	        tab.setTabListener(this);
	        tab.setTag(R.string.favorities);
	        getSupportActionBar().addTab(tab);
	        
	        Drawable dr=getResources().getDrawable(R.drawable.bg);
	        getSupportActionBar().setBackgroundDrawable(dr);  
    }
    
    
    
}