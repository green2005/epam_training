package com.smlivejournal.messages;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;

import android.os.Handler;
import android.util.Log;

import com.smlivejournal.settings.Settings;

enum MReason {
	mGetMessages, mDelMessage, mSendMessage, mSetRead
}

enum MMsgType {
	sAll, sMessages, sPostsAndRecords, sFriendsUpdateMsg, sSend
}

public class MessageThread extends Thread {
	private Settings settings;
	private MReason reason;
	private Handler mainHandler;
	private List<LJMessage> msgList;
	private MMsgType msgType = MMsgType.sAll;

	public MessageThread(Settings settings, MReason reason, Handler mainHandler)
			throws Exception {
		super();
		this.settings = settings;
		this.reason = reason;
		this.mainHandler = mainHandler;
		if (settings == null) {
			throw new Exception("Settings must not be empty");
		}
		if (settings.getUserName().equalsIgnoreCase("")) {
			throw new Exception("Unknown user name");
		}
	}

	public void setMessageList(List<LJMessage> msgList) {
		this.msgList = msgList;
	}

	public void setMsgType(MMsgType msgType) {
		this.msgType = msgType;
	}

	@Override
	public void run() {
		super.run();

		switch (reason) {
		case mDelMessage: {

			break;
		}
		case mGetMessages: {
			getMessages();
			break;
		}
		case mSendMessage: {

			break;
		}
		case mSetRead: {

			break;
		}
		}

	}

	private void getMessages() {
		if (msgList == null) {
			return;
		}
		try {
			Hashtable method_calls = new Hashtable();
			String lj_url = "http://www.livejournal.com/interface/xmlrpc";
			XmlRpcClient xmlrpc;
			xmlrpc = new XmlRpcClient(lj_url);

			// String pwd = settings.getPwd();
			// String password = AuthThread.getMD5(pwd);
			Vector<Hashtable<String, Comparable>> params = new Vector();

			method_calls.put("username", settings.getUserName());
			method_calls.put("password", settings.getPwd());
			method_calls.put("ver", "1");
			method_calls.put("clientversion", "WebServiceBook/0.0.1");
			method_calls.put("extended", "true");
			if (msgType != MMsgType.sAll) {
				Vector v=new Vector();
				switch (msgType) {
				case sFriendsUpdateMsg: {
					/*
					 	1 Friended
						2. Birthday
						3. CommunityInvite
						4. CommunityJoinApprove
						5. CommunityJoinReject
						6. CommunityJoinRequest
						7. Defriended
						8. InvitedFriendJoins
					 */
					for (int i=1;i<9;i++){
						v.add(i);
					}
					v.add(11);
					v.add(12);
					v.add(15);
					v.add(17);
					
					//11. NewUserpic
					//12. NewVGift (new virtual gift)
					//15. PollVote
					//17. UserExpunged
					break;
				}
				case sMessages: {

					break;
				}
				case sPostsAndRecords: {
					/*
					 20. UserNewComment
					 21. UserNewEntry
					 */
					v.add(20);
					v.add(21);
					break;
				}

				case sSend: {
					//19. UserMessageSent
					v.add(19);
					break;
				}

				}
				method_calls.put("gettype", v);
			}

			params.add(method_calls);
			Object o = xmlrpc.execute("LJ.XMLRPC.getinbox", params);
			// Log.d("", o.getClass().getName());

			for (int i = 0; i < ((Vector) ((Hashtable) o).get("items")).size(); i++) {
				Hashtable item = ((Hashtable) ((Vector) ((Hashtable) o)
						.get("items")).get(i));
				LJMessage msg=new LJMessage();
				
				String stime="";
				java.util.Date time = new java.util.Date(
						1000 * Long
								.parseLong(item.get("when").toString()));
				stime=time.toString();
				msg.setTime(stime);
				
				String qid=(String)item.get("qid");
				msg.setId(qid);
				
				int type=(Integer) (((Hashtable) ((Vector) ((Hashtable) o)
						.get("items")).get(1)).get("type"));
				msg.setMsgType(type);
				
				
				
				//msgList.add(msg);
				
			}

			java.util.Date time = new java.util.Date(
					1000 * Long
							.parseLong(((Hashtable) ((Vector) ((Hashtable) o)
									.get("items")).get(1)).get("when")
									.toString()));

			// type
			// qid
			// state
			// subject
			// entry
			// poster
			// comment=http://green-2005.livejournal.com/4363.html?thread=6923#t6923

			// {subject=, qid=3, state=N, extended={subject_raw=, dtalkid=6923,
			// body=[B@40609820}, type=9, journal=green_2005,
			// entry_subject=[B@406089b8,
			// poster_userpic_url=http://l-userpic.livejournal.com/94416129/23592362,
			// entry=http://green-2005.livejournal.com/4363.html,
			// when=1408115294, poster=lamaalex, action=new, posterid=23592362,
			// ditemid=4363,
			// comment=http://green-2005.livejournal.com/4363.html?thread=6923#t6923}
			// {body=[B@40605408, msgid=72008155, from_id=71649866,
			// subject=[B@406042c8, msg_type=in, when=1407354686,
			// timesent=1407354685, qid=2, state=n, parent=72008124,
			// from=fakeaccount2005, type=18}

			/*
			 * ((Hashtable)
			 * ((Vector)((Hashtable)o).get("items")).get(0)).get("entry")
			 * 
			 * ((Hashtable)((Hashtable)
			 * ((Vector)((Hashtable)o).get("items")).get
			 * (1)).get("extended")).get("body")
			 * 
			 * ((Hashtable)
			 * ((Vector)((Hashtable)o).get("items")).get(1)).get("qid")
			 * ((Hashtable)
			 * ((Vector)((Hashtable)o).get("items")).get(1)).get("msg_type"
			 * ).equals("in") //"in" ((Hashtable)
			 * ((Vector)((Hashtable)o).get("items")).get(0)).get("msgid")
			 * ((Hashtable)
			 * ((Vector)((Hashtable)o).get("items")).get(0)).get("from_id")
			 * //from_id
			 * 
			 * new String((byte[])((Hashtable)
			 * ((Vector)((Hashtable)o).get("items")).get(0)).get("subject")) new
			 * String((byte[])((Hashtable)
			 * ((Vector)((Hashtable)o).get("items")).get(0)).get("body"))
			 */

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
