package com.smlivejournal.fb;

import java.util.Arrays;
import java.util.List;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.smlivejournal.top.LJPost;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class FbPost {
	private Context context;
	private Activity activity;
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    
	public FbPost(Context context,Activity activity){
		this.context=context;
		this.activity=activity;
		
	}
	
	
	public void postMessage(final String msg){
			Session.openActiveSession(activity, true, new Session.StatusCallback() {
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				if (session.isOpened()){
					Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
						
						@Override
						public void onCompleted(GraphUser user, Response response) {
							if (user!=null){
								performPublish(msg);
							}
						}
					});
					
				}
			}
		});
			
	}
	
	public void onActivityResult(int requestCode,int resultCode,Intent data){
		Session.getActiveSession().onActivityResult(activity, requestCode, resultCode, data);
	}
	
	
	 private boolean hasPublishPermission() {
	        Session session = Session.getActiveSession();
	        return session != null && session.getPermissions().contains("publish_actions");
	   }
	 
	 private void performPublish(String message) {
	        Session session = Session.getActiveSession();
	        if (session != null) {
	            if (hasPublishPermission()) {
	                // We can do the action right away.
	            	 //final String message = getString(R.string.status_update, user.getFirstName(), (new Date().toString()));
	                 Request request = Request
	                         .newStatusUpdateRequest(Session.getActiveSession(), message, new Request.Callback() {
	                             @Override
	                             public void onCompleted(Response response) {
	                                Toast.makeText(context, FbPost.this.context.getResources().getString(com.smlivejournal.client.R.string.itemaded), 
	                                		Toast.LENGTH_LONG).show();
	                            	 //showPublishResult(message, response.getGraphObject(), response.getError());
	                             }
	                         });
	                 request.executeAsync();
	            } else {
	                // We need to get new permissions, then complete the action when we get called back.
	                session.requestNewPublishPermissions(new Session.NewPermissionsRequest(activity, PERMISSIONS));
	            }
	        }
	    }
	
	
	

}
