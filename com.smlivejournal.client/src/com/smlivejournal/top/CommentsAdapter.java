package com.smlivejournal.top;

import java.util.ArrayList;
import java.util.HashMap;

import com.smlivejournal.client.R;
import com.smlivejournal.lazylist.ImageLoader;
import com.smlivejournal.settings.Settings;
import com.smlivejournal.userblog.PostEditor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class CommentsAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<HashMap<String, String>> commentsList;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;
	private Activity activity;
	private float screenWidth = (float) 0.0;
	private float screenHeight = (float) 0.0;
	HashMap<ImageView, String> imgUrls;
	protected int itemIndex = 0;
	private Settings settings;
	private boolean showReplyView=false;
	 
	private final static int defaultMargin = 10;

	public CommentsAdapter(Activity activity, Context context,
			ArrayList<HashMap<String, String>> commentsList) {
		super();
		this.context = context;
		this.commentsList = commentsList;
		this.activity = activity;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(context);
		imgUrls = new HashMap<ImageView, String>();
		 
	}
	
	public void setSettings(Settings settings){
		this.settings=settings;
		if (settings!=null)
			if (!settings.getPwd().equalsIgnoreCase("")){
				showReplyView=true;
			}
	}

	@Override
	public int getCount() {
		return commentsList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return commentsList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		View cnView = arg1;
		ViewHolder holder;
		if (screenWidth == 0) {
			setScreenSize();
			imageLoader.setScreenSize(screenWidth, screenHeight);
		}

		if (cnView == null) {
			cnView = inflater.inflate(R.layout.commentitem, null);
			holder = new ViewHolder(cnView, imageLoader);
			cnView.setTag(holder);
			// holder.tvComment=(TextView)cnView.findViewById(R.id.tvCommentText);
			// holder.tvComment2=(TextView)cnView.findViewById(R.id.tvCommentText2);
			// holder.imView=(ImageView)cnView.findViewById(R.id.imv);
			holder.tvUserName = (TextView) cnView.findViewById(R.id.tvUserName);
			holder.tvReply=(TextView)cnView.findViewById(R.id.tvReply);
			holder.rlaBorder = (RelativeLayout) cnView
					.findViewById(R.id.rlaBorder);
			holder.tvDate = (TextView) cnView.findViewById(R.id.tvUserDate);
			holder.tvExpand = (TextView) cnView.findViewById(R.id.tvExpand);
			holder.lacomments = (LinearLayout) cnView
					.findViewById(R.id.laComments);
			holder.tvUserName.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//Toast.makeText(context, "click", Toast.LENGTH_SHORT).show();
				}
			});
			
			if (showReplyView){
				holder.tvReply.setVisibility(View.VISIBLE);
				holder.tvReply.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int i=(Integer)v.getTag();
						HashMap<String,String> map=commentsList.get(i);
						Bundle b=new Bundle();
						b.putInt("reason", PostEditor.iReply);
						b.putSerializable("settings", settings);
						b.putSerializable("commentData", map);
						Intent intent=new Intent(context,PostEditor.class);
						intent.putExtras(b);
						//context.startActivity(intent);
						activity.startActivityForResult(intent, PostEditor.iAddComment);
					}
				});
			}
			

			holder.tvExpand.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (activity instanceof LJPost) {
						int i = (Integer) v.getTag();
						HashMap<String, String> map = commentsList.get(i);
						String href = map.get("expandHref");
						Intent ci = new Intent(context, CommentsActivity.class);
						Bundle b = new Bundle();
						b.putString("expandHref", href);
						b.putSerializable("settings",settings);
						ci.putExtras(b);
						activity.startActivityForResult(ci, 0);
					
					}
					// context.startActivity(ci);
					// Toast.makeText(context, "clickExpand",
					// Toast.LENGTH_SHORT).show();
				}
			});

		} else {
			holder = (ViewHolder) cnView.getTag();
		}
		holder.tvExpand.setTag((Integer) arg0);
		holder.tvReply.setTag((Integer)arg0);

		HashMap<String, String> map = commentsList.get(arg0);
		holder.setContent(map);
		holder.setTextSize(settings);
		return cnView;
	}

	private class ViewHolder {
		int itemIndex;
		TextView tvComment;
		TextView tvComment2;
		TextView tvReply;
		ImageView imView;
		LinearLayout lacomments;
		TextView tvUserName;
		TextView tvDate;
		RelativeLayout rlaBorder;
		TextView tvExpand;
		ImageLoader loader;
		View parentView;
		ArrayList<TextView> commentsList = new ArrayList();
		ArrayList<ImageView> imgList = new ArrayList();
		protected String href;

		ViewHolder(View parentView, ImageLoader loader) {
			this.loader = loader;
			this.parentView = parentView;
		}
		
		void setTextSize(Settings settings){
			if (settings==null){
				return;
			}
			settings.setTextViewSize(tvReply);
			settings.setTextViewSize(tvDate);
			settings.setTextViewSize(tvExpand);
			settings.setTextViewSize(tvUserName);
		}

		void setContent(HashMap<String, String> map) {
			String userName = map.get("userName");
			String commentText = map.get("commentText");
			String commentDate = map.get("date");
			String level = map.get("level");
			String href = "";
			if (map.containsKey("expandHref")) {
				href = map.get("expandHref");
			}

			int ilevel = Integer.decode(level);
			if (ilevel > 1) {
				android.widget.LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) rlaBorder
						.getLayoutParams();
				
				lp.setMargins(defaultMargin + 15, defaultMargin, defaultMargin,
						0);
				rlaBorder.setLayoutParams(lp);
				tvExpand.setVisibility(View.GONE);
			} else {
				/*
				 * android.widget.RelativeLayout.LayoutParams lp=new
				 * android.widget.RelativeLayout.LayoutParams(
				 * LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				 */
				android.widget.LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) rlaBorder
						.getLayoutParams();

				lp.setMargins(defaultMargin, defaultMargin, defaultMargin, 0);
				rlaBorder.setLayoutParams(lp);
				if (!href.equalsIgnoreCase("")) {
					tvExpand.setVisibility(View.VISIBLE);
				} else {
					tvExpand.setVisibility(View.GONE);

				}
			}

			tvUserName.setText(userName);
			tvUserName.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					String userName = ((TextView) v).getText().toString();
					if (activity instanceof LJPost) {
						((LJPost) activity).loadUserLine(userName);
					} else if (activity instanceof CommentsActivity) {
						((CommentsActivity) activity).loadUserLine(userName);
					}
				}
			});

			tvDate.setText(commentDate);
			for (int i = 0; i < 10; i++) {
				int j = 0;
				if (map.containsKey("commentText" + i)) {
					TextView tv;
					if (commentsList.size() <= i) {
						tv = new TextView(context);
						/*
						 * android:autoLink="web" android:clickable="true"
						 * android:focusable="false"
						 */

						lacomments.addView(tv);
						TableRow.LayoutParams params = new TableRow.LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT, 1.0f);
					//	tv.setAutoLinkMask(Linkify.ALL);
						tv.setLinksClickable(true);
						tv.setClickable(true);
						
						tv.setMovementMethod(LinkMovementMethod
								.getInstance()); 
					 	

						// android.widget.TableRow.LayoutParams params=new
						// TableRow.LayoutParams(0,0,1.0f);
						params.setMargins(10, 10, 0, 0);
						tv.setLayoutParams(params);
						commentsList.add(tv);
					} else {
						tv = commentsList.get(i);
					}
					settings.setTextViewSize(tv);
					SpannableString stext = new SpannableString(Html.fromHtml(map.get("commentText" + i)));
				 	tv.setText(stext);

				} else {

					j = 1;
				}
				
				boolean loadImages=true;
				if (settings!=null){
					if (!settings.getLoadImages())
						loadImages=false;
				}
				
				if ((map.containsKey("img" + i)&&(loadImages))) {
					ImageView imv;
					if (imgList.size() <= i) {
						imv = new ImageView(context);
						lacomments.addView(imv);
						imgList.add(imv);
					} else {
						imv = imgList.get(i);
					}
					imv.setVisibility(View.VISIBLE);
					imageLoader.DisplayImage(map.get("img" + i), imv);
					imgUrls.put(imv, map.get("img" + i));
					imv.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Bundle b = new Bundle();
							String imgUrl = imgUrls.get(v);
							b.putString("imgUrl", imgUrl);
							b.putFloat("screenWidth", screenWidth);
							b.putFloat("screenHeight", screenHeight);
							Intent imgViewActivity = new Intent(
									context,
									com.smlivejournal.client.ViewImageActivity.class);
							imgViewActivity
									.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							imgViewActivity.putExtras(b);
							context.startActivity(imgViewActivity);
						}
					});

				} else {

					j = 1;
				}

				if (j != 0) {
					for (j = i; j < 10; j++) {
						if ((commentsList.size() > j)
								&& (!map.containsKey("commentText" + j))) {
							commentsList.get(j).setVisibility(View.GONE);
						}
						if ((imgList.size() > j)
								&& (!map.containsKey("img" + j))) {
							imgList.get(j).setVisibility(View.GONE);
						}
					}

					break;
				}
			}
		}
	}

	private void setScreenSize() {
		Display display = activity.getWindowManager().getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		float density = context.getResources().getDisplayMetrics().density; // dm.density;
		screenHeight = dm.heightPixels / density;
		screenWidth = dm.widthPixels / density;
		screenWidth /= 1.1;
		screenHeight /= 1.1;
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}

}
