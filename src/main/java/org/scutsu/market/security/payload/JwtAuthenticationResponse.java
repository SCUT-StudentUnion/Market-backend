package org.scutsu.market.security.payload;

public class JwtAuthenticationResponse {
	private String accessToken;
	
	private String tokenType="Bearer";
	
	public JwtAuthenticationResponse(String accessToken) {
		this.accessToken=accessToken;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public String getTokenType() {
		return tokenType;
	}
	
	public void setTokenType(String tokenType) {
		this.tokenType=tokenType;
	}
	
	
}
