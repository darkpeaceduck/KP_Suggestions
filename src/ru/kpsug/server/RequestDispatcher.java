package ru.kpsug.server;

public class RequestDispatcher {
	private static String recall(SugProtoRequest request, RequestHandler caller) {

		return SugProtoParser.makeResponse(request, caller.processDb(request.getId(), request.getDepth()));
	}

	public static String dispatch(String s, RequestHandler caller) {
		SugProtoRequest parsed_request = SugProtoParser.parse(s);
		if (parsed_request == null) {
			return SugProtoParser.makeError();
		}
		return recall(parsed_request, caller);
	}
}
