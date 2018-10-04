package org.scutsu.market.controllers.wx;

import java.util.Map;

import com.github.kevinsawicki.http.*;

import net.sf.json.JSONObject;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.jsonwebtoken.*;


@RestController
public class LoginController {
	@RequestMapping(value="/onlogin")
	public Map onlogin(@RequestParam(value="code")String code) {
		String codeJSONString="code="+code;
		String sr=HttpRequest.post("https://api.weixin.qq.com/sns/jscode2session").send(codeJSONString).body();
		JSONObject json=JSONObject.fromObject(sr);
		String session_key=json.get("session_key").toString();
		String openid=json.get("openid").toString();
		
		
	}
	
	public String createJWT() {
		JwtBuilder builder=JwtBuilder
	}
}
