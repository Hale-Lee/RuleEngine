//Copyright Hale [hale2000@163.com]
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.

package tech.kiwa.engine.utility;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import tech.kiwa.engine.framework.DBAccesser;

/**
 * 通过SpringContext获取数据库连接.
 * @author Hale.Li
 * @since  2018-01-28
 * @version 0.1
 */
@Service
public class SpringDBAccesser implements ApplicationContextAware , DBAccesser {

	private static ApplicationContext applicationContext = null;

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public Connection getConnection() {

		DataSource ds = (DataSource) applicationContext.getBean("dataSource");

		try {
			return ds.getConnection();
		} catch (SQLException e) {

			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

		SpringDBAccesser.applicationContext = applicationContext;
	}

}
