package de.smbsolutions.tagungVisitor;

public class Room {
	
public String objectId;
public String name;

public Room (String objectId, String name)  {
	this.name = name;
	this.objectId = objectId;
}
public String getObjectId() {
	return objectId;
}
public void setObjectId(String objectId) {
	this.objectId = objectId;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
}
