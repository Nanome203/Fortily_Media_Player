package model;

import java.util.List;

public class PlayListItem {
	private String playListName;
	private String imgSrc;
	private int numOfItems;
	private List<String> listUrl;
	
	public String getPlayListName() {
		return playListName;
	}
	public void setPlayListName(String playListName) {
		this.playListName = playListName;
	}
	public String getImgSrc() {
		return imgSrc;
	}
	public void setImgSrc(String imgSrc) {
		this.imgSrc = imgSrc;
	}
	public int getNumOfItems() {
		return numOfItems;
	}
	public void setNumOfItems(int numOfItems) {
		this.numOfItems = numOfItems;
	}
	@Override
	public String toString() {
		return "PlayListItem [playListName=" + playListName + ", imgSrc=" + imgSrc + ", numOfItems=" + numOfItems + "]";
	}
	
	
	
	
	
}
