package com.smlivejournal.userblog;

import java.io.Serializable;

public class Tag implements Serializable {
	String tags = "";
	boolean hasAdultContent = false;
	EComments comments = EComments.eDefault;
	EAccess access = EAccess.ePublic;

	public Tag() {
		// TODO Auto-generated constructor stub
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public boolean adultContent() {
		return hasAdultContent;
	}

	public void setAdultContent(boolean hasAdultContent) {
		this.hasAdultContent = hasAdultContent;
	}

	public void setEComments(EComments comments) {
		this.comments = comments;
	}

	public EComments getComments() {
		return comments;
	}

	public void setEAccess(EAccess access) {
		this.access = access;
	}

	public EAccess getEAccess() {
		return access;
	}

	public void setInfoByStr(String tagsList,String sadultContent,String commentsOptions,String security){
		tags=tagsList;
		hasAdultContent=false;
		if (sadultContent!=null)
			if (sadultContent.equals("1"))
				hasAdultContent=true;
		
		comments=EComments.eDefault;
		if (commentsOptions!=null)
			if (commentsOptions.equals("opt_nocomments")){
				comments=EComments.eShutOff;
			} else
				if (commentsOptions.equals("opt_lockcomments")){
					comments=EComments.eBlock;
				} else
					if (commentsOptions.equals("opt_noemail")){
						comments=EComments.eNotNotify;
					};
						
	  access=EAccess.ePublic;
	  if (security!=null){
		  if (security.equals("usemask"))
			  access=EAccess.eFriends; else
				  if (security.equals("private"))
					  access=EAccess.ePrivate;  
	  }
	  /*
		 usemask  - друзья
		 private - личная 
		 публичная - null
		 */
			
		/*
		 map.put("tagList",taglist);
			map.put("adultcontent", adultcontent);
			map.put("commentsOptions", commentsOptions);
			map.put("security",security);
		 */
		
	}

	public enum EComments {
		eBlock, eShutOff, eNotNotify, eDefault
	}

	public enum EAccess {
		ePublic, ePrivate, eFriends
	}

}
