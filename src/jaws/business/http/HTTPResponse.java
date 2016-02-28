package jaws.business.http;

public class HTTPResponse extends HTTPObject<HTTPResponse> {
	
	private int statusCode;
	
	/**
	 * For more information see {@link jaws.business.http.HTTPObject#getFirstLine}.
	 */
	@Override
	protected String getFirstLine() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int statusCode() {
		
		return statusCode;
	}
	
	public HTTPResponse statusCode(int code) {
		
		statusCode = code;
		return this;
	}
}
