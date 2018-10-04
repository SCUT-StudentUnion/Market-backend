package org.scutsu.market.controllers.wx;

import java.util.Map;

import com.github.kevinsawicki.http.*;

import net.sf.json.JSONObject;

import org.scutsu.market.security.JwtAuthenticationResponse;
import org.scutsu.market.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.jsonwebtoken.*;


import org.scutsu.market.repositories.UserRepository;
import org.scutsu.market.models.User;

@RestController
public class LoginController {
	
	@Autowired
		UserRepository userRepository;
	
	 @Autowired
	    JwtTokenProvider tokenProvider;
	 
	 @Autowired
	    AuthenticationManager authenticationManager;
	
	@RequestMapping(value="/onlogin")
	public ResponseEntity onlogin(@RequestParam(value="code")String code) {
		String codeJSONString="code="+code;
		String sr=HttpRequest.post("https://api.weixin.qq.com/sns/jscode2session").send(codeJSONString).body();
		JSONObject json=JSONObject.fromObject(sr);
		String session_key=json.get("session_key").toString();
		String openid=json.get("openid").toString();
		User user=new User();
		user.setWeChatOpenId(openid);
		
		if(userRepository.findByWeChatOpenId(openid)==null) {
			userRepository.save(user);
		}
		User findUser=userRepository.findByWeChatOpenId(openid).get(0);
		String jwt = tokenProvider.generateToken(findUser.getId());
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
		
	}
	
}
