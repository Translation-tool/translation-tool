package com.github.translation.controller;

import com.github.translation.dao.UserDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.*;

/**
 * Unit-тесты проверяют отдельные методы и не используют PostgreSQL
 */
public class TestControllerUnitTest {

	TestController controller = new TestController();
	UserDAO userDaoMock;
	MockHttpServletResponse resp = new MockHttpServletResponse();

	@Before
	public void init() {
		userDaoMock = Mockito.mock(UserDAO.class);
		controller.setUserDAO(userDaoMock);
	}

	@Test
	public void testTest_returns_test() throws Exception {
		assertEquals("test", controller.test("key", resp));
	}
}
