package org.scutsu.market.controllers.wx;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.scutsu.market.models.User;
import org.scutsu.market.repositories.UserRepository;
import org.scutsu.market.security.JwtTokenProvider;

@Controller
public class LoginController {
	
	UserRepository userRepository;
	
	@Value("${app.appid}")
	String appid;
	
	@Value("${app.secret}")
	String secret;
	
	@Value("app.grant_type")
	String grant_type;
	
	JwtTokenProvider jwtTokenProvider;
	
	@RequestMapping(value="/login")
	public Map<String,Object> OnLogin(@RequestBody Map<String,Object> reqMsg ) {
		
		String code=reqMsg.get("code").toString();
		String session_key="";
		String openid="";
		
		String params = "appid=" + appid + "&secret=" + secret + "&js_code=" + code + "&grant_type=" + grant_type;
		String url="https://api.weixin.qq.com/sns/jscode2session?"+params;
		String result;
		try {
			
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);
		
			if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK) {
				result=EntityUtils.toString(response.getEntity(),"utf-8");
				ObjectMapper mapper = new ObjectMapper();
				JsonNode jsonNode = mapper.readTree(result);
				session_key=jsonNode.get("session_key").textValue();
				openid = jsonNode.get("openid").textValue();
			
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		if(openid!=""&&userRepository.findByWeChatOpenId(openid)==null){
			User user =new User();
			user.setWeChatOpenId(openid);
			userRepository.save(user);
		}
		
		Long userId=userRepository.findByWeChatOpenId(openid).getId();
		String jwt=jwtTokenProvider.generateToken(userId);
		
		Map<String,Object> resMsg =new HashMap<String,Object>();
		resMsg.put("jwt",jwt);
		return resMsg;
	}
}
