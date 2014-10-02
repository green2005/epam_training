package com.smlivejournal.messages;

import java.util.ArrayList;
import java.util.List;

import com.smlivejournal.settings.Settings;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class MessagesAdapter extends BaseAdapter {
	private  List<LJMessage> messagesList;
	private LayoutInflater inflater;
	private Settings settings;

	MessagesAdapter( List<LJMessage> messagesList, LayoutInflater inflater,Settings settings) {
		super();
		this.messagesList = messagesList;
		this.inflater = inflater;
		this.settings=settings;
	}

	@Override
	public int getCount() {
		return messagesList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return messagesList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		View cnView = arg1;
		ViewHolder holder;
		if (cnView == null) {
			cnView = inflater.inflate(
					com.smlivejournal.client.R.layout.message, null);
			holder=new ViewHolder();
			cnView.setTag(holder);
			holder.tvAuthor=(TextView)cnView.findViewById(com.smlivejournal.client.R.id.authorView);
			holder.tvBody=(TextView)cnView.findViewById(com.smlivejournal.client.R.id.body);
			holder.tvTime=(TextView)cnView.findViewById(com.smlivejournal.client.R.id.timeView);
			holder.tvTitle=(TextView)cnView.findViewById(com.smlivejournal.client.R.id.titleView);
			holder.check=(CheckBox)cnView.findViewById(com.smlivejournal.client.R.id.checkBox);
			holder.setTextSize();
		}else
		{
			holder=(ViewHolder)cnView.getTag();
		}	
		LJMessage msg=messagesList.get(arg0);
		
		if (msg.getAuthor().equalsIgnoreCase("")){
			holder.tvAuthor.setVisibility(View.GONE);
		} else
		{	
			holder.tvAuthor.setVisibility(View.VISIBLE);
			holder.tvAuthor.setText(msg.getAuthor());
		}
		
		SpannableString sstring=new SpannableString(msg.getTitle());
		if (msg.getIsRead()){
			sstring.setSpan(new StyleSpan(Typeface.BOLD),1,2000,0);
		} else
		{
			sstring.setSpan(new StyleSpan(Typeface.NORMAL), 1,2000, 0);
		}
		holder.tvTitle.setText(sstring);
		

		sstring=new SpannableString(msg.getTitle());
		if (msg.getIsRead()){
			sstring.setSpan(new StyleSpan(Typeface.BOLD),1,2000,0);
		} else
		{
			sstring.setSpan(new StyleSpan(Typeface.NORMAL), 1,2000, 0);
		}
		holder.tvTitle.setText(sstring);
			
		

		return cnView;
	}

	 
	
	private class ViewHolder{
		/*
		    android:id="@+id/body"
        android:id="@+id/titleView"
        android:id="@+id/checkBox"
        android:id="@+id/authorView"
        android:id="@+id/timeView"
        
		 */
		TextView tvBody;
		TextView tvAuthor;
		TextView tvTitle;
		TextView tvTime;
		CheckBox check; 
		void setTextSize(){
			if (settings!=null){
				settings.setTextViewSize(tvBody);
				settings.setTextViewSize(tvAuthor);
				settings.setTextViewSize(tvTitle);
				settings.setTextViewSize(tvTime);
			}
		}
		
	}


}
