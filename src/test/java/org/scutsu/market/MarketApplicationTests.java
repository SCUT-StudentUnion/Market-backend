package org.scutsu.market;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.scutsu.market.models.User;
import org.scutsu.market.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MarketApplicationTests {

	@Autowired
	UserRepository userRepository;

	@Test
	public void contextLoads() {

		User user = new User();
		user.setWeChatOpenId("123");
		userRepository.save(user);
	}

}
