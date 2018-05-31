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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.util.StringUtils;

import tech.kiwa.engine.framework.DBAccesser;

/**
 * @author Hale.Li
 * @since  2018-01-28
 * @version 0.1
 */
public class DirectDBAccesser implements DBAccesser{

	private static Logger log = LoggerFactory.getLogger(DirectDBAccesser.class);

	private static boolean UseDruid = true;

	private static ArrayList<Connection> connList = new ArrayList<Connection> ();			//只用了一个连接， 没有用到连接池。


    public static boolean isUseDruid() {
		return UseDruid;
	}

	public static void setUseDruid(boolean useDruid) {
		UseDruid = useDruid;
	}

	/** The druid source. */
    private static volatile DataSource dataSource = null;



    /**
     * 根据类型获取数据源.  非线程安全的函数
     *
     * @return druid或者dbcp数据源
     * @throws Exception the exception
     */
    public static final DataSource getDataSource() throws Exception {

    	Properties prop = 	PropertyUtil.loadPropertFile("druid.properties");
    	if(prop == null){
    		throw new Exception("druid.properties file load error.");
    	}

    	String password = prop.getProperty("password");
    	String publicKey = prop.getProperty("publickey");

        if(!StringUtils.isEmpty(publicKey)){

			password = ConfigTools.decrypt(publicKey, password);
	    	prop.setProperty("password", password);
        }

        dataSource = DruidDataSourceFactory.createDataSource(prop);

        return dataSource;
    }

	private Connection openConnection(){

		Connection conn = null;

		String driver = PropertyUtil.getProperty("jdbc.driver");		// "oracle.jdbc.driver.OracleDriver";
        String url = PropertyUtil.getProperty("jdbc.url"); 				//"jdbc:Oracle:thin:@localhost:1521:orcl";
        String userName = PropertyUtil.getProperty("jdbc.username");
        String password = PropertyUtil.getProperty("jdbc.password");
        String publicKey = PropertyUtil.getProperty("jdbc.publickey");

        if(!StringUtils.isEmpty(publicKey)){
        	try {
				password = ConfigTools.decrypt(publicKey, password);

		        Class.forName(driver);

		        DriverManager.setLoginTimeout(30000);
		        conn = DriverManager.getConnection(url, userName, password);

			}catch (ClassNotFoundException e) {
	            log.debug(e.getMessage());
	        }  catch (Exception e) {
	        	 log.debug(e.getMessage());
			}
        }

        if(null != conn){
        	connList.add(conn);
        }

		return conn;
	}

	public Connection getConnection(){

		if(UseDruid){
			try {

				if(dataSource == null){
					synchronized (DataSource.class){
						if(dataSource == null){
							dataSource = getDataSource();
						}
					}
				}

				return dataSource.getConnection();
			} catch (SQLException e) {
				log.error(e.getMessage());
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}

		//如果是直接连接,或者是druid读取失败的情况下。
		for(Connection conn : connList){
			try {
				if(!conn.isClosed()){
					return conn;
				}
			} catch (SQLException e) {
				log.debug(e.getMessage());
			}
		}

		//if the proper connection is not found , create a new one.
		return openConnection();
	}

	//@SuppressWarnings("unused")
	public void closeConnection(Connection conn){

		if(UseDruid){

			try {
				conn.close();

			} catch (SQLException e) {
				 log.debug(e.getMessage());
			}
		}

		try {

			if(conn != null && !conn.isClosed()){
				conn.close();


				if(connList.contains(conn)){

					synchronized (connList){
						connList.remove(conn);
					}
				}
			}
		} catch (SQLException e) {
			 log.debug(e.getMessage());
		}
	}



}
