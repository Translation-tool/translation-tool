package com.github.translation.dao;

import com.github.translation.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserDAO {
	@Autowired
	JdbcTemplate jdbcTemplate;

	/**
	 * Добавляет пользователя в базу
	 * @param user новый пользователь
	 */
	public void insert(User user) {
		String sql = "INSERT INTO USER (EMAIL, NAME) VALUES (?, ?)";
		jdbcTemplate.update(sql, user.getEmail(), user.getName());
	}
}
