package com.smlivejournal.google;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusClient.OnAccessRevokedListener;
//import com.google.android.gms.plus.GooglePlusUtil;
import com.google.android.gms.plus.PlusShare;



 import com.smlivejournal.client.MomentUtil;
import com.smlivejournal.client.R;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View.OnClickListener;
 

public class GooglePost implements  
PlusClient.ConnectionCallbacks, PlusClient.OnConnectionFailedListener,
OnAccessRevokedListener {
	Activity activity;
	private static final String TAG_ERROR_DIALOG_FRAGMENT = "errorDialog";
	private static final int REQUEST_CODE_RESOLVE_GOOGLE_PLUS_ERROR = 1;
	private PlusClient mPlusClient;
	private ConnectionResult mConnectionResult;
	private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final int REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES = 2;
    private static final int DIALOG_GET_GOOGLE_PLAY_SERVICES = 1;
    private static final int REQUEST_CODE_INTERACTIVE_POST = 2;
    private static final String LABEL_VIEW_ITEM = "VIEW_ITEM";

    private String shareText;
    

	public GooglePost(Activity activity,String shareText){
		//this.context=context;
		this.activity=activity;
		this.shareText=shareText;
		/*  mPlusClient = new PlusClient.Builder(activity,this,this)
          .setActions(MomentUtil.ACTIONS)
          .build();
*/
		}
	
	public void post(){
		/*int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (available != ConnectionResult.SUCCESS) {
            activity.showDialog(DIALOG_GET_GOOGLE_PLAY_SERVICES);
            return;
        }

        try {
           // mSignInStatus.setText(getString(R.string.signing_in_status));
            mConnectionResult.startResolutionForResult(activity, REQUEST_CODE_SIGN_IN);
        } catch (IntentSender.SendIntentException e) {
            // Fetch a new result to start.
            mPlusClient.connect();
        }
        */
		Intent shareIntent = new PlusShare.Builder(activity)
        .setType("text/plain")
        .setText(shareText)
        .setContentUrl(Uri.parse("https://developers.google.com/+/"))
        .getIntent();
		activity.startActivityForResult(shareIntent, 0);
	}
	
	public void disconnect(){
//		if (mPlusClient!=null)
//		mPlusClient.disconnect();
	}

	@Override
	public void onAccessRevoked(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
	      activity.startActivityForResult(getInteractivePostIntent(), REQUEST_CODE_INTERACTIVE_POST);
	}
	
	  private Intent getInteractivePostIntent() {
	        // Create an interactive post with the "VIEW_ITEM" label. This will
	        // create an enhanced share dialog when the post is shared on Google+.
	        // When the user clicks on the deep link, ParseDeepLinkActivity will
	        // immediately parse the deep link, and route to the appropriate resource.
	        String action = "/?view=true";
	        Uri callToActionUrl = Uri.parse(activity.getString(com.smlivejournal.client.R.string.plus_example_deep_link_url) + action);
	        String callToActionDeepLinkId = activity.getString(com.smlivejournal.client.R.string.plus_example_deep_link_id) + action;

	        // Create an interactive post builder.
	        PlusShare.Builder builder = new PlusShare.Builder(activity, mPlusClient);

	        // Set call-to-action metadata.
	        builder.addCallToAction(LABEL_VIEW_ITEM, callToActionUrl, callToActionDeepLinkId);

	        // Set the target url (for desktop use).
	        builder.setContentUrl(Uri.parse(activity.getString(com.smlivejournal.client.R.string.plus_example_deep_link_url)));

	        // Set the target deep-link ID (for mobile use).
	        builder.setContentDeepLinkId(activity.getString(com.smlivejournal.client.R.string.plus_example_deep_link_id),
	                null, null, null);
	        // Set the pre-filled message.
	        builder.setText(shareText);
	        return builder.getIntent();
	    }

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

}
