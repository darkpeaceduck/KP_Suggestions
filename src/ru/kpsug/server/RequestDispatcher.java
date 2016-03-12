package ru.kpsug.server;

public class RequestDispatcher {
	private static String recall(MyProtoRequest request, RequestHandler caller) {

		return MyProtoFormatter.makeResponse(request, caller.processDb(request.getId(), request.getDepth()));
	}

	public static String dispatch(String s, RequestHandler caller) {
		MyProtoRequest parsed_request = MyProtoFormatter.parse(s);
		if (parsed_request == null) {
			return MyProtoFormatter.makeError();
		}
		return recall(parsed_request, caller);
	}
}
