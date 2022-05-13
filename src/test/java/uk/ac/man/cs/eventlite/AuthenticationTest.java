package uk.ac.man.cs.eventlite;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import uk.ac.man.cs.eventlite.config.Security;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EventLite.class)
@Import(Security.class)
public class AuthenticationTest {
	
	private PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
	public static final String ADMIN_ROLE = "ADMINISTRATOR";
	public static final String NORMAL_USER_ROLE = "NORMAL USER";
	
	@Test
	public void testAdminStatus() throws Exception {
		UserDetails admin = User.withUsername("Admin").password(encoder.encode("1234")).roles(ADMIN_ROLE).build();
		assert(admin.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + ADMIN_ROLE)));
	}
	
	@Test
	public void testNonAdminStatus() throws Exception {
		UserDetails user = User.withUsername("User").password(encoder.encode("1234")).roles(NORMAL_USER_ROLE).build();
		assert(user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + NORMAL_USER_ROLE)));
	}

	
}
