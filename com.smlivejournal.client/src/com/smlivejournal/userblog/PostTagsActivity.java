package com.smlivejournal.userblog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.smlivejournal.top.CommentsAdapter;
import com.smlivejournal.userblog.Tag.EAccess;
import com.smlivejournal.userblog.Tag.EComments;

public class PostTagsActivity extends Activity {
	private Tag tagInfo;
	private String[] commentsData = { "Как во всем журнале", "Отключить",
			"Заблокировать", "Не уведомлять" };
	private String[] accessData = { "Каждому (публичная)", "Друзьям",
			"Только мне (личная)" };

	private EditText tagsEdit;
	private CheckBox adultCheck;
	private Spinner commentsSpinner;
	private Spinner accessSpinner;
	private Button okBtn;

	public PostTagsActivity() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.smlivejournal.client.R.layout.tagsedit);

		tagsEdit = (EditText) findViewById(com.smlivejournal.client.R.id.tagedit);
		okBtn = (Button) findViewById(com.smlivejournal.client.R.id.btnSave);
		adultCheck = (CheckBox) findViewById(com.smlivejournal.client.R.id.adultcheck);
		commentsSpinner = (Spinner) findViewById(com.smlivejournal.client.R.id.commentspinner);
		accessSpinner = (Spinner) findViewById(com.smlivejournal.client.R.id.accessspinner);
		//commentsSpinner.

		Bundle b = getIntent().getExtras();
		if (b != null) {
			tagInfo = (Tag) b.getSerializable("tag");
		} else {
			tagInfo = new Tag();
		}
		;
		setUIFromTag();
		okBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				PostTagsActivity.this.fillTagfromUI();
				Intent i = new Intent();
				Bundle b = new Bundle();
				b.putSerializable("tag", tagInfo);
				i.putExtras(b);
				PostTagsActivity.this.setResult(RESULT_OK, i);
				finish();
			}
		});

	}

	private void fillTagfromUI() {
		tagInfo.setTags(tagsEdit.getText().toString());
		tagInfo.setAdultContent(adultCheck.isChecked());
	}

	private void setUIFromTag() {
		tagsEdit.setText(tagInfo.tags);
		adultCheck.setChecked(tagInfo.hasAdultContent);
		ArrayAdapter<String> accessAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, accessData);
		accessSpinner.setAdapter(accessAdapter);
		switch (tagInfo.access) {
		case eFriends: {
			accessSpinner.setSelection(1);
			break;
		}
		case ePrivate: {
			accessSpinner.setSelection(2);
			break;
		}

		case ePublic: {
			accessSpinner.setSelection(0);
			break;
		}
		}
		accessSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				switch (arg2) {
				case 0: {
					tagInfo.setEAccess(EAccess.ePublic);
					break;
				}
				case 1: {
					tagInfo.setEAccess(EAccess.eFriends);
					break;
				}
				case 2: {
					tagInfo.setEAccess(EAccess.ePrivate);
					break;
				}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}

		});

		ArrayAdapter<String> commentsSpinnerAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, commentsData);
		commentsSpinner.setAdapter(commentsSpinnerAdapter);
		switch (tagInfo.comments) {
		case eDefault: {
			commentsSpinner.setSelection(0);
			break;
		}
		case eBlock: {
			commentsSpinner.setSelection(2);
			break;
		}
		case eNotNotify: {
			commentsSpinner.setSelection(3);
			break;
		}
		case eShutOff: {
			commentsSpinner.setSelection(1);
			break;
		}

		}
		commentsSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				switch (arg2){
				case 0:{
					tagInfo.setEComments(EComments.eDefault);
					break;
				}
				case 1:{
					tagInfo.setEComments(EComments.eShutOff);
					break;
				}
				case 2:{
					tagInfo.setEComments(EComments.eBlock);
				}
				case 3:{
					tagInfo.setEComments(EComments.eShutOff);
				}
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

}
