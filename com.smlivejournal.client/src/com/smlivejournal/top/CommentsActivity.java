package com.smlivejournal.top;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.smlivejournal.client.R;
import com.smlivejournal.settings.Settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class CommentsActivity extends Activity {
	ArrayList<HashMap<String, String>> comments;
	ArrayList<HashMap<String, String>> tmpComments;
	ListView lvComments;
	CommentsReader cmReader;
	CommentsAdapter commentsAdapter;
	Handler commentsHandler;
	RelativeLayout laProgress;
	Settings settings;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.smlivejournal.client.R.layout.commentactivity);
		Bundle b = getIntent().getExtras();
		String href = b.getString("expandHref");
		RelativeLayout ljComment = (RelativeLayout) findViewById(R.id.laComments);
		settings=(Settings)b.getSerializable("settings");
		
		laProgress = (RelativeLayout) getLayoutInflater().inflate(
				R.layout.progresslayout, null);
		ljComment.addView(laProgress);
		setProgressVisible(true);

		comments = new ArrayList<HashMap<String, String>>();
		tmpComments = new ArrayList<HashMap<String, String>>();
		lvComments = (ListView) findViewById(R.id.lvComments);

		cmReader = new CommentsReader(href, tmpComments, this, ljComment, laProgress);
		commentsAdapter = new CommentsAdapter(this, this, comments);
		commentsAdapter.setSettings(settings);
		lvComments.setOnScrollListener(cmReader); // (new
													// PostScrollListener(LJPost.this));
		lvComments.setAdapter(commentsAdapter);

		commentsHandler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.getData().getInt("done") == 1) {
					comments.addAll(tmpComments);
					commentsAdapter.notifyDataSetChanged();
					// setCommentPorgressVisible(false);
					cmReader.setCommentsLoadingdone();
					setProgressVisible(false);
					tmpComments.clear();
				}
			};
		};
		cmReader.setCommentsHandler(commentsHandler);
		cmReader.setSettings(settings);
		cmReader.readComments(tmpComments, commentsHandler, this, href);
	}
	
	public void loadUserLine(String userName){
	//	Toast.makeText(this, userName, Toast.LENGTH_SHORT).show();
		Intent i=new Intent();
		i.putExtra("userName", userName);
		setResult(RESULT_OK, i);
		finish();
	}

	private void setProgressVisible(boolean visible) {
		if (visible) {
			laProgress.setVisibility(View.VISIBLE);
		} else {
			laProgress.setVisibility(View.GONE);
		}
	}
}
