package de.smbsolutions.tagungVisitor;

public class Presentation {
	
	public String objectId;
	public String Referent;
	public String date;
	public String room_id;
	public String time_from;
	public String time_to;
	public String topic;
	
	
	
	public Presentation(String objectId, String Referent, String date, String room_id, String time_from, String time_to, String topic) {
		this.objectId = objectId;
		this.Referent = Referent;
		this.date = date;
		this.room_id = room_id;
		this.time_from = time_from;
		this.time_to = time_to;
		this.topic = topic;
	}
	
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public String getReferent() {
		return Referent;
	}
	public void setReferent(String referent) {
		Referent = referent;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime_from() {
		return time_from;
	}
	public void setTime_from(String time_from) {
		this.time_from = time_from;
	}
	public String getTime_to() {
		return time_to;
	}
	public void setTime_to(String time_to) {
		this.time_to = time_to;
	}

	public String getRoom_id() {
		return room_id;
	}

	public void setRoom_id(String room_id) {
		this.room_id = room_id;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}
	

}
