package org.scutsu.market.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.interfaces.Claim;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
//import org.apache.tomcat.jni.User;
import org.scutsu.market.models.User;
import org.scutsu.market.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;


@RestController
public class login {
	private final UserRepository userRepository;

    private static final String APPID = ""; //待填
    private static final String SECRET = "";    //待填

	@Autowired
	public login(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	//第一次登录，在数据库中创建一个用户
    //已经有过登录，把User中的数据返回给小程序
    @PostMapping("/login")
    public String Login(String code) {
		//url需根据小程序自定义
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code";

        //向url发送包含code的请求
        RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

		if (responseEntity.getStatusCode() == HttpStatus.OK) {
            //解析json
            String sessionData = responseEntity.getBody();
            JSONObject jsonObject = JSON.parseObject(sessionData);
            //提取openID和sessionKey
            String openID = jsonObject.getString("openid");
            String sessionKey = jsonObject.getString("session_key");
            if (userRepository.findByWeChatOpenId(openID) != null) {
            	User user = null;
				user.setWeChatOpenId(openID);
				userRepository.save(user);
			}
			String jwt = Jwts.builder()
				.setSubject(openID)
				.claim("roles", openID)
				.setIssuedAt(new Date())
				.signWith(SignatureAlgorithm.HS256, "secretkey")
				.compact();

			System.out.print("successful login!");
			return jwt;
        }
		System.out.print("failed login!");
        return null;
    }
}
