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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 加载属性文件，同时写到内存中。
 * @author Hale.Li
 * @since  2018-01-28
 * @version 0.1
 */
public class PropertyUtil {

	private static Logger log = LoggerFactory.getLogger(PropertyUtil.class);

	/**
	 * 获取配置属性，从内存中获取配置属性（key-value键值对），如果不存在，则尝试解析一下${}格式，再读一遍。
	 * @param key
	 * @return
	 */
	public static String getProperty(String key){

		String value = directGetProperty(key);

		if(StringUtils.isNotEmpty(value)){
			value = value.trim();

			//${}格式化的数据，那么取括号里面的内容。
			if(value.startsWith("${") && value.endsWith("}")){

				value = value.substring(2, value.length()-1);

				value = directGetProperty(value);
				if(StringUtils.isNotEmpty(value)){
					value = value.trim();
				//	break;
				}
			}
		}


		return value;
	}

	/**
	 * 加载配置文件，从配置文件中加载到内存中。
	 * @param fileName
	 * @return
	 */
	public static Properties loadPropertFile(String fileName){
		Properties prop = null;

		if(fileName.contains(File.separator)){
			try {
				File file = new File(fileName);

				prop = new Properties();
				InputStream fisResource = new FileInputStream(file);
				prop.load(fisResource);
				fisResource.close();
			} catch (FileNotFoundException e) {
				log.debug(e.getMessage());
			} catch (IOException e) {
				log.debug(e.getMessage());
			}
		}else{
			try {
				File dir = new File(PropertyUtil.class.getClassLoader().getResource("").getPath());
				File file = new File(dir.getAbsolutePath() + File.separator + fileName);

				prop = new Properties();
				InputStream fisResource = new FileInputStream(file);
				prop.load(fisResource);
				fisResource.close();
			} catch (FileNotFoundException e) {
				log.debug(e.getMessage());
			} catch (IOException e) {
				log.debug(e.getMessage());
			}
		}

		if(prop != null){

			Set<Object> keySet = (Set<Object>) prop.keySet();
			for(Object key : keySet){

				//如果key不是字符串格式的，那么跳过。

				if(key instanceof String){
					String value = prop.getProperty((String)key);
					//value 存在的情况下。
					if(null != value){

						value = value.trim();
						//如果是变量形式的值。
						if(value.startsWith("${") && value.endsWith("}")){

							//那么再读取一遍。
							value = value.substring(2, value.length()-1);
							value = directGetProperty(value);
							if(StringUtils.isNotEmpty(value)){
								value = value.trim();
								prop.setProperty((String)key, value);
							}
						}
					}
				} //end if

			}	//end for.
		}
		return prop;
	}

	private static Properties ruleEngineBuffer  =  new Properties();
	private static String directGetProperty(String key){

		final String ruleEngineFile = "ruleEngine.properties";

		if(!ruleEngineBuffer.isEmpty()){
			return ruleEngineBuffer.getProperty(key);
		}

		String value = null ;

		try {

			InputStream fisResource = PropertyUtil.class.getClassLoader().getResourceAsStream(ruleEngineFile);
			if(fisResource != null){
				Properties prop = new Properties();

				prop.load(fisResource);
				fisResource.close();

				ruleEngineBuffer.putAll(prop);
				value = prop.getProperty(key);
				if(null != value){
					return value;
				}
			}

			//read the resource file from the other property files.
			File dir = new File(PropertyUtil.class.getClassLoader().getResource("").getPath());
			File[] propertyFiles = dir.listFiles();

			for(int iLoop=0; iLoop < propertyFiles.length; iLoop++){
				String propFile = propertyFiles[iLoop].getPath();
				propFile = propFile.toLowerCase();

				if(propFile.endsWith(".properties")){
					FileInputStream fis = new FileInputStream(propFile);
					Properties prop = new Properties();
					prop.load(fis);
					fisResource.close();

					ruleEngineBuffer.putAll(prop);

					value = prop.getProperty(key);
					if(null != value){
						return value;
					}
				}
			}

		} catch (FileNotFoundException e) {
			log.debug(e.getMessage());
		} catch (IOException e) {
			log.debug(e.getMessage());
		} catch (Exception e){
			log.debug(e.getMessage());
		}

		// read the value from system memory.
		if(null == value){
			value = System.getProperty(key);
		}
		return value;
	}


	public static void main(String[] args){
		PropertyUtil.getProperty("jdbc.pool");
	}
}
