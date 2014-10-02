package com.smlivejournal.top;

import android.content.Context;
import android.widget.AbsListView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class PostScrollListener implements OnScrollListener {
	private int scrollState;
	private boolean isLoading;
	private int visibleItemCount;
	private Context context;
	private int prevTopItem=0;
	
	public PostScrollListener(Context context) {

		this.context=context;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.visibleItemCount=visibleItemCount;
		int i=firstVisibleItem+visibleItemCount;
		if ((totalItemCount>0)&& (i==totalItemCount)&&(!isLoading)&&(prevTopItem<firstVisibleItem)){
			 isLoading = true;
			 loadMoreData(visibleItemCount,firstVisibleItem,totalItemCount);
		}
		prevTopItem=firstVisibleItem;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.scrollState = scrollState;
		
	}

	
	
	private void loadMoreData(int visibleItemCount,int firstVisible,int totalItemCount){
		///Toast.makeText(context, "scrollState="+scrollState+";viisbleqty="+visibleItemCount+";firstvisible="+firstVisible
		///		,Toast.LENGTH_SHORT).show();
		isLoading=false;
	}

}
