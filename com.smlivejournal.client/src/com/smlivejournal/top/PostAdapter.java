package com.smlivejournal.top;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smlivejournal.client.R;
import com.smlivejournal.lazylist.ImageLoader;
import com.smlivejournal.settings.Settings;

public class PostAdapter extends android.widget.BaseAdapter {
	private Context context;
	private Activity activity;
	private ArrayList<HashMap<String, String>> post;
	private HashMap<ImageView, String> imgUrls;
	private LayoutInflater inflater;
	private ImageLoader imageloader;
	private float screenWidth = 0;
	private float screenHeight = 0;
	private Settings settings;
	private String postUrl;
	private String sTag="";
	private ArrayList<String> tagsList;
	private String userName="";

	PostAdapter(Activity activity, Context context,
			ArrayList<HashMap<String, String>> post, Settings settings,String postUrl) {
		super();
		this.activity = activity;
		this.context = context;
		this.context = activity.getApplicationContext();
		this.post = post;
		inflater = LayoutInflater.from(context);
		imageloader = new ImageLoader(context);
		imgUrls = new HashMap<ImageView, String>();
		this.settings = settings;
		this.postUrl=postUrl;
		this.tagsList=new ArrayList<String>();
	}

	public void clearCache() {
		imageloader.clearCache();
	}

	@Override
	public int getCount() {
		return post.size();
	}

	@Override
	public Object getItem(int arg0) {
		return post.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View cnView = convertView;
		RelativeLayout laPostItem;
		RelativeLayout laTitleItem;
		//LinearLayout laImView;
		HashMap<String, String> map = post.get(position);
		ViewHolder holder;

		if (cnView == null) {
			cnView = inflater.inflate(
					com.smlivejournal.client.R.layout.ljposttext, null);
			holder = new ViewHolder();
			cnView.setTag(holder);

			laPostItem = (RelativeLayout) cnView.findViewById(R.id.laPostItem);
			laTitleItem = (RelativeLayout) cnView
					.findViewById(R.id.laTitleItem);
			holder.laImView = (LinearLayout) laPostItem.findViewById(R.id.laImView);

			holder.title = (TextView) laTitleItem.findViewById(R.id.tvTitle);
			holder.user = (TextView) laTitleItem.findViewById(R.id.tvUser);
			holder.date = (TextView) laTitleItem.findViewById(R.id.tvDate);
			holder.tvText = (TextView) laPostItem.findViewById(R.id.tvPostText);
			holder.tvTag=(TextView)laTitleItem.findViewById(R.id.tvTagText);
			holder.upicImView=(ImageView)laTitleItem.findViewById(R.id.upicimageview);
			
			//holder.imvSmall = (ImageView) laPostItem.findViewById(R.id.imviewSmall);
			holder.imView = (ImageView) laPostItem.findViewById(R.id.imView);
			// holder.tvText.setMovementMethod(new
			// LinkMovementMethod().getInstance());

		} else {
			laPostItem = (RelativeLayout) cnView.findViewById(R.id.laPostItem);
			laTitleItem = (RelativeLayout) cnView
					.findViewById(R.id.laTitleItem);
			//laImView = (LinearLayout) laPostItem.findViewById(R.id.laPostItem);

			holder = (ViewHolder) cnView.getTag();
		}

		if (position == 0) {
			laPostItem.setVisibility(View.GONE);
			laTitleItem.setVisibility(View.VISIBLE);
			if (map.containsKey("taglist"))
			sTag=(String)map.get("taglist");
			holder.user.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//String userName = ((TextView) (v)).getText().toString();
					((LJPost) activity).loadUserLine(userName);
				}
			});
			holder.title.setText(map.get("title"));
			userName=map.get("userName");
			holder.user.setText(userName);
			holder.date.setText(map.get("pubdate"));
			holder.laImView.setVisibility(View.GONE);
			if ((sTag!=null)&&(!sTag.equalsIgnoreCase(""))){
 					setTag(sTag,holder);
			}
			String upic=(String)map.get("upic");
			if ((upic!=null)&&(!upic.equalsIgnoreCase(""))){
				holder.upicImView.setVisibility(View.VISIBLE);
				imageloader.DisplayImage(upic, holder.upicImView);
			}else
			{
				holder.upicImView.setVisibility(View.GONE);
			}
			
		} else {
			holder.tvTag.setVisibility(View.GONE);
			
			laPostItem.setVisibility(View.VISIBLE);
			laTitleItem.setVisibility(View.GONE);
			//holder.imView.setVisibility(View.VISIBLE);
			
			 
			String s = map.get("text");
			if (!s.equalsIgnoreCase("")) {
				// s = s.trim();
				// holder.tvText.setMovementMethod(LinkMovementMethod
				// .getInstance());
				// holder.tvText.setText(s);
				
				
				holder.tvText.setLinksClickable(true);
				holder.tvText.setMovementMethod(LinkMovementMethod
						.getInstance()); 
				Spanned spanned= Html.fromHtml(s);
				
			 	SpannableString stext = new SpannableString(spanned);

			 	SpannableStringBuilder sp=new SpannableStringBuilder();
			 	sp.append(spanned);
			
			 	//bbb
			 	/*Object[] spans = stext.getSpans(0, stext.length() - 1,
						Object.class);
			 	int i=0;
				for (Object span : spans) {
					int start = stext.getSpanStart(span);
					int end = stext.getSpanEnd(span);
					int flags = stext.getSpanFlags(span);
					if (span instanceof URLSpan) {
						URLSpan urlSpan = (URLSpan) span;
						String url=urlSpan.getURL();
						if (url!=null){
						 sp.setSpan(new NonUnderlinedClickableSpan(i++){
							 @Override
								public void onClick(View v) {
									 Toast.makeText(context,this.getIdx()+"", Toast.LENGTH_LONG).show();
									 super.onClick(v);
								}
							 
						 }, start, end, flags);
						 
						 
							//if ((url.contains(".livejournal.com/"))&&(url.endsWith(".html"))&&(!url.equalsIgnoreCase(postUrl))) {
						//	span = new TextClickableSpan(url);
						//}
						//stext.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
					}
				}
				
				 
				
				 //xx
				*/
				holder.tvText.setText(sp);//stext);
				 
				// Linkify.addLinks(holder.tvText, Linkify.ALL);

				//
				// Html.fromHtml(s));
				// HtmlToPlainText f=new HtmlToPlainText();
				// Jsoup.parse(s).body()
				// Html.fromHtml(s)
				// holder.tvText.setText(s);
				holder.tvText.setVisibility(View.VISIBLE);
			} else {
				holder.tvText.setVisibility(View.GONE);
			}
			
			
			if ((map.containsKey("img")) && (settings.getLoadImages())) {
				holder.laImView.setVisibility(View.VISIBLE);
				if (screenWidth == 0) {
					setScreenSize();
					imageloader.setScreenSize(screenWidth, screenHeight);
				}
				boolean changeImage = true;

				if (imgUrls.containsKey(holder.imView)) {
					if (imgUrls.get(holder.imView).equalsIgnoreCase(map.get("img")))
						changeImage = false;
					if (changeImage)
						imgUrls.remove(holder.imView);
				}
				if (changeImage) {
					imgUrls.put(holder.imView, map.get("img"));
					imageloader.DisplayImage(map.get("img"), holder.imView);
				}

				holder.imView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String imgUrl = imgUrls.get(v);
						
						
						Bundle b = new Bundle();
						b.putString("imgUrl", imgUrl);
						b.putFloat("screenWidth", screenWidth);
						b.putFloat("screenHeight", screenHeight);
						Intent imgViewActivity = new Intent(
								context,
								com.smlivejournal.client.ViewImageActivity.class);
						imgViewActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						imgViewActivity.putExtras(b);
						context.startActivity(imgViewActivity);

					}
				});

				// imView.setImageResource(R.drawable.ic_launcher);
			} else {
				holder.laImView.setVisibility(View.GONE);
				//holder.imvSmall.setVisibility(View.GONE);
			}

		}
		holder.setTextViewSize(settings);
		return cnView;
	}
	
	private void fillTagsList(String sTag){
		int i=sTag.indexOf(",");
		while (i>0){
			String s=sTag.substring(0,i)+"";
			tagsList.add(s);
			sTag=sTag.substring(i+1,sTag.length());
			sTag=sTag.trim();
			i=sTag.indexOf(",");
		}
		tagsList.add(sTag);
	}
	
	private void setTag(String sTag,ViewHolder holder){
		if (tagsList.size()==0){
			fillTagsList(sTag);
		}
		String r="";
		String sTagLabel = context.getResources().getString(com.smlivejournal.client.R.string.staglabel)+" ";
		int i=0;
		SpannableStringBuilder sp=new SpannableStringBuilder();
		for (String tag:tagsList){
			if (r.length()>0)
			{r=r+", "+tag;
			sp.append(", "+tag);
			} else
			{	r=sTagLabel + tag;
				sp.append(r);
			}
			sp.setSpan(new  NonUnderlinedClickableSpan(i) {
				@Override
				public void onClick(View v) {
					 //Toast.makeText(context,this.getIdx()+"", Toast.LENGTH_LONG).show();
					 String tag=tagsList.get(this.getIdx());
					 String s=userName+":"+tag;
					 ((LJPost) activity).loadUserLine(s);
					 
					 super.onClick(v);
				}
			}
			, r.length()-tag.length(), r.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			i++;
		}
		holder.tvTag.setVisibility(View.VISIBLE);
	//nn
		
		holder.tvTag.setText(sp);
		holder.tvTag.setMovementMethod(LinkMovementMethod.getInstance());
		 
		
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}

	protected void onCallback(String data) {
		// ....
		Toast.makeText(context, data, Toast.LENGTH_LONG).show();
	}

	private void setScreenSize() {
		Display display = activity.getWindowManager().getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		float density = context.getResources().getDisplayMetrics().density; // dm.density;
		screenHeight = dm.heightPixels / density;
		screenWidth = dm.widthPixels / density;
	}

	private class ViewHolder {
		TextView title;
		TextView user;
		TextView tvText;
		TextView date;
		ImageView imvSmall;
		ImageView imView;
		ImageView upicImView;
		TextView tvTag;
		LinearLayout laImView;

		void setTextViewSize(Settings settings) {
			if (settings == null) {
				return;
			}
			settings.setTextViewSize(title, 2);
			settings.setTextViewSize(user);
			settings.setTextViewSize(tvText);
			settings.setTextViewSize(date);
			settings.setTextViewSize(tvTag);
		}
	}

	 public class TextClickableSpan extends ClickableSpan {
	        private String text;

	        public TextClickableSpan(String text) {
	            this.text = text;
	        }

	        @Override
	        public void onClick(View view) {
	            Toast.makeText(PostAdapter.this.context,text,Toast.LENGTH_SHORT).show();
	        }
	        }
	 
	 private   class NonUnderlinedClickableSpan extends ClickableSpan {
		   private int idx=0;
		   
		    
		  public NonUnderlinedClickableSpan(int i) {
			 idx=i;
		  }
		  
		  public int getIdx(){
			  return idx;
		  }
		 
		  
		  @Override
		  public void updateDrawState(TextPaint ds) {
		   ds.setColor(context.getResources().getColor(com.smlivejournal.client.R.color.lightblue));
		   ds.setUnderlineText(false);
		   ds.setTypeface(Typeface.DEFAULT_BOLD);
		  }

		  @Override
		  public void onClick(View v) {

		  }
		 }

}
