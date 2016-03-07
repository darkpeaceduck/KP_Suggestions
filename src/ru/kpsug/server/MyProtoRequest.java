package ru.kpsug.server;

public class MyProtoRequest {
	private int type;
	private int depth;
	private String id;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public MyProtoRequest(int type, int depth, String id) {
		super();
		this.type = type;
		this.depth = depth;
		this.id = id;
	}

}
