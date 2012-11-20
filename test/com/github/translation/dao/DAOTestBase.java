package com.github.translation.dao;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import junit.framework.AssertionFailedError;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;


/**
 * Базовый класс для тестов DAO. Подключается к тестовой PostgrdeSQL базе по jdbc:postgresql://localhost/test,
 * создаёт там схему БД. Файл translation.properties должен существовать чтобы этот тест отрабатывал правильно.
 *
 * @author Dmitry Negoda <Dmitry.Negoda@gmail.com>
 */
@ContextConfiguration("classpath:/com/github/translation/applicationContext.xml")
public abstract class DAOTestBase extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	protected DataSource dataSource;

	static {
		System.setProperty("hibernate.hbm2ddl.auto", "create-drop");
		System.setProperty("jdbc.url", "jdbc:postgresql://localhost/test");
		System.setProperty("geoip.cache.in.ram", "0");
	}

	/**
	 * Создаёт схему БД
	 */
	@Before
	public void installDatabaseSchema() throws Exception {
		// TODO
	}

}
