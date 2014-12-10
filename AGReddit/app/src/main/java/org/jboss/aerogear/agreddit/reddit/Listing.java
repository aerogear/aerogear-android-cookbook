package org.jboss.aerogear.agreddit.reddit;

public class Listing {

	private String kind;
	private ListingData data;
	private String after;
	private String before;
	
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public ListingData getData() {
		return data;
	}
	public void setData(ListingData data) {
		this.data = data;
	}
	public String getAfter() {
		return after;
	}
	public void setAfter(String after) {
		this.after = after;
	}
	public String getBefore() {
		return before;
	}
	public void setBefore(String before) {
		this.before = before;
	}
	
	
	
}
