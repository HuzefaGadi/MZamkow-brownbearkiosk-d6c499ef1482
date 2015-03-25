package com.huzefagadi.brownbear.beans;

import java.util.List;

public class PhotoResponse {

	private String status;
	private Photos data;
	
	
	
	
	public Photos getData() {
		return data;
	}
	public void setData(Photos data) {
		this.data = data;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	 
	public class Photos {
		
		private List<ImagesUrl> photos;

		public List<ImagesUrl> getPhotos() {
			return photos;
		}

		public void setPhotos(List<ImagesUrl> photos) {
			this.photos = photos;
		}

		 
	}
	public class ImagesUrl {
		
		private String url;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
		
	}
}
