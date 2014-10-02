package com.smlivejournal.messages;

public class LJMessage {
	private String body;
	private String title;
	private String time;
	private String author;
	private boolean isRead = false;
	private String id;
	private MsgType msgType;

	enum MsgType {
		mFriended, mBirthday, mCommunityInvite, mCommunityJoinApprove, mCommunityJoinReject, mCommunityJoinRequest, mDefriended, mInvitedFriendJoins, mJournalNewComment, mJournalNewEntry, mNewUserpic, mNewVGift, mOfficialPost, mPermSale, mPollVote, mSupOfficialPost, mUserExpunged, mUserMessageRecvd, mUserMessageSent, mUserNewComment, mUserNewEntry

	}

	public LJMessage() {

	}

	public MsgType getMsgType() {
		return msgType;
	}

	public void setMsgType(MsgType msgType) {
		this.msgType = msgType;
	}
	
	public void setMsgType(int msgType){
		this.msgType=inttoMsgType(msgType);
	}
	
	public static MsgType inttoMsgType(int i) {
		MsgType msg = null;
		switch (i) {
		case (0): {
			// msg=((MsgType)(i));
			msg = MsgType.mFriended;
			break;
		}
		case (1): {
			msg = MsgType.mBirthday;
			break;
		}
		case (2): {
			msg = MsgType.mCommunityInvite;
			break;
		}
		case (3): {
			msg = MsgType.mCommunityJoinApprove;
			break;
		}
		case (4): {
			msg = MsgType.mCommunityJoinReject;
			break;
		}
		case (5): {
			msg = MsgType.mCommunityJoinRequest;
			break;
		}
		case (6): {
			msg = MsgType.mDefriended;
			break;
		}
		case (7): {
			msg = MsgType.mInvitedFriendJoins;
			break;
		}
		case (8): {
			msg = MsgType.mJournalNewComment;
			break;

		}
		case (9): {
			msg = MsgType.mJournalNewEntry;
			break;

		}
		case (10): {
			msg = MsgType.mNewUserpic;
			break;

		}
		case (11): {
			msg = MsgType.mNewVGift;
			break;

		}
		case (12): {
			msg = MsgType.mOfficialPost;
			break;

		}
		case (13): {
			msg = MsgType.mPermSale;
			break;

		}
		case (14): {
			msg = MsgType.mPollVote;
			break;
		}
		case (15): {
			msg = MsgType.mSupOfficialPost;
			break;
		}
		case (16): {
			msg = MsgType.mUserExpunged;
			break;

		}
		case (17): {
			msg = MsgType.mUserMessageRecvd;
			break;

		}
		case (18): {
			msg = MsgType.mUserMessageSent;
			break;

		}
		case (19): {
			msg = MsgType.mUserNewComment;
			break;

		}
		case (20): {
			msg = MsgType.mUserNewEntry;
			break;
		}

		}

		return msg;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = new String(author);
	}

	public String getTime() {
		return this.time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setRead(boolean read) {
		this.isRead = read;
	}

	public boolean getIsRead() {
		return isRead;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}

	public void setBody(String body) {
		this.body = new String(body);
	}

	public String getBody() {
		return this.body;
	}

}
