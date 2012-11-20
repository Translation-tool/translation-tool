package com.github.translation.controller;

import com.github.translation.dao.UserDAO;
import com.github.translation.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

@Controller
public class TestController {

	@Autowired
	UserDAO userDAO;

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	@Transactional(rollbackFor = Throwable.class)
	public String test(@RequestParam(value = "key", required = false) String key,
						 /*@RequestBody User user,*/ HttpServletResponse resp) throws Exception {
		// TODO: just a sample
		return "test"; // corresponds to test.jsp
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
}
