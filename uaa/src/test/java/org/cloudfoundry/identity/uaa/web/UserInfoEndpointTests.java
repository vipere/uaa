/*
 * Copyright 2006-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.cloudfoundry.identity.uaa.web;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.cloudfoundry.identity.uaa.authentication.UaaAuthentication;
import org.cloudfoundry.identity.uaa.authentication.UaaTestFactory;
import org.cloudfoundry.identity.uaa.scim.ScimException;
import org.cloudfoundry.identity.uaa.scim.ScimUser;
import org.cloudfoundry.identity.uaa.user.InMemoryUaaUserDatabase;
import org.junit.Test;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * @author Dave Syer
 *
 */
public class UserInfoEndpointTests {

	private UserInfoEndpoint endpoint = new UserInfoEndpoint();
	private InMemoryUaaUserDatabase userDatabase = new InMemoryUaaUserDatabase();
	
	public UserInfoEndpointTests() {
		endpoint.setUserDatabase(userDatabase);
	}

	@Test
	public void testSunnyDay() {
		ScimUser user = userDatabase.createUser(UaaTestFactory.getScimUser("olds", "olds@vmware.com", "Dale", "Olds"), "password");
		UaaAuthentication authentication = UaaTestFactory.getAuthentication(user.getId(), "Dale", "olds@vmware.com");
		Map<String, String> map = endpoint.loginInfo(new OAuth2Authentication(null, authentication));
		assertEquals("olds", map.get("user_id"));
		assertEquals("Dale Olds", map.get("name"));
		assertEquals("olds@vmware.com", map.get("email"));
	}

	@Test(expected=ScimException.class)
	public void testMissingUser() {
		UaaAuthentication authentication = UaaTestFactory.getAuthentication("12345", "Dale", "olds@vmware.com");
		Map<String, String> map = endpoint.loginInfo(new OAuth2Authentication(null, authentication));
		assertEquals("olds", map.get("user_id"));
		assertEquals("Dale Olds", map.get("name"));
		assertEquals("olds@vmware.com", map.get("email"));
	}

}