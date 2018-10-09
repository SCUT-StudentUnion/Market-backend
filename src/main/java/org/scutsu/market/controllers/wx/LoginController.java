package org.scutsu.market.controllers.wx;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.kevinsawicki.http.HttpRequest;

import net.sf.json.JSONObject;

import org.scutsu.market.models.User;
import org.scutsu.market.repositories.UserRepository;
import org.scutsu.market.security.JwtTokenProvider;

@Controller
public class LoginController {
	
	UserRepository userRepository;
	
	JwtTokenProvider jwtTokenProvider;
	
	@RequestMapping(value="/login")
	public Map<String,Object> OnLogin(@RequestBody Map<String,Object> reqMsg ) {
		
		String code=reqMsg.get("code").toString();
		String wxspAppid=reqMsg.get("appid").toString();
		String wxspSecret=reqMsg.get("secret").toString();
		String grant_type=reqMsg.get("grant_type").toString();
		
		String params = "appid=" + wxspAppid + "&secret=" + wxspSecret + "&js_code=" + code + "&grant_type=" + grant_type;
		String str=HttpRequest.get("https://api.weixin.qq.com/sns/jscode2session?"+params).body();
		JSONObject json = JSONObject.fromObject(str);
		String session_key=json.get("session_key").toString();
		String openid=json.get("openid").toString();
		
		if(userRepository.findByWeChatOpenId(openid)==null){
			User user =new User();
			user.setWeChatOpenId(openid);
			userRepository.save(user);
		}
		Long userId=userRepository.findByWeChatOpenId(openid).get(0).getId();
		String jwt=jwtTokenProvider.generateToken(userId);
		
		Map<String,Object> resMsg =new HashMap<String,Object>();
		resMsg.put("jwt",jwt);
		return resMsg;
	}
}
