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

package tech.kiwa.engine.component.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tech.kiwa.engine.component.AbstractCommand;
import tech.kiwa.engine.component.AbstractRuleItem;
import tech.kiwa.engine.entity.ItemExecutedResult;
import tech.kiwa.engine.entity.RESULT;
import tech.kiwa.engine.entity.RuleItem;
import tech.kiwa.engine.exception.RuleEngineException;
import tech.kiwa.engine.framework.DBAccesser;
import tech.kiwa.engine.utility.PropertyUtil;


/**
 * @author Hale.Li
 * @since  2018-01-28
 * @version 0.1
 */
public class DefaultRuleExecutor extends AbstractRuleItem {

	private Logger log = LoggerFactory.getLogger(DefaultRuleExecutor.class);

	private static volatile DBAccesser accesser = null;		//数据库访问类。

	@SuppressWarnings("unchecked")
	private DBAccesser loadDBAccesser() throws ClassNotFoundException, InstantiationException, IllegalAccessException{

		if(accesser == null){

			String className = PropertyUtil.getProperty("db.accesser");
			Class<DBAccesser> DBClass = (Class<DBAccesser>) Class.forName(className);
			synchronized(DBAccesser.class){
				if(null == accesser){
					accesser = DBClass.newInstance();
				}
			}
		}

		return accesser;
	}


	@Override
	public ItemExecutedResult doCheck(RuleItem item) throws RuleEngineException {

		String sqlStr  = item.getExeSql();

		if(StringUtils.isEmpty(sqlStr)){
			log.debug("This function must be called when the sql statement is not empty.");
			throw new RuleEngineException("This function must be called when the sql statement is not empty.");
		}

		// add tailed space.
		if(sqlStr.endsWith("?")){
			sqlStr = sqlStr + " ";
		}

		String paramName = item.getParamName();
		List<Object> paramList = null ;
		if(!StringUtils.isEmpty(paramName)){

			String[] params = paramName.split(",");
			String[] sqlSection = sqlStr.split("\\?");

			if(sqlSection.length != params.length + 1){
				log.debug("the parameters count must be equal to the count of the question mark.item_no = {},  item.desc = {}", item.getItemNo(), item.getContent());
				throw new RuleEngineException("the parameters count must be equal to the count of the question mark.");
			}

			if(StringUtils.isEmpty(item.getParamType())){
				log.debug("the cannot be empty if param is assigned. item_no = {},  item.desc = {}", item.getItemNo(), item.getContent());
				throw new RuleEngineException("the cannot be empty if param is assigned.");
			}

			String[] paramTypes = item.getParamType().split(",");
			if(params.length != paramTypes.length){
				log.debug("the parameters count must be equal to the count of the question mark. item_no = {},  item.desc = {}", item.getItemNo(), item.getContent());
				throw new RuleEngineException("the parameters count must be equal to the count of the question mark.");
			}

			paramList =new ArrayList<Object>();
			for(int iLoop =0 ; iLoop < paramTypes.length; iLoop++){
				String type = paramTypes[iLoop];
				paramList.add(getParamValue(params[iLoop],type));
			}

		}


		Map<String, Object> resultSet = null;
		resultSet = executeSQL(sqlStr, paramList);
		boolean bRet = false;

		bRet = super.analyze(resultSet, item);

		ItemExecutedResult checkResult = new ItemExecutedResult();

		//默认做法时可以继续执行下一条规则。
		checkResult.setContinue(ItemExecutedResult.CONTINUE);
		checkResult.setReturnValue(bRet);

		if(bRet){
			checkResult.setResult(RESULT.PASSED);
			checkResult.setRemark(RESULT.PASSED.getName());
			//checkResult.setContinue( Integer.parseInt(item.getContinueFlag()));
		}else{
			checkResult.setResult(RESULT.REJECTED);
			checkResult.setRemark(RESULT.REJECTED.getName());
			//checkResult.setContinue( Integer.parseInt(item.getContinueFlag()));			
			// add false result return.
		}

		this.executeCommand(item, checkResult);

		return checkResult;
	}

	@SuppressWarnings("unchecked")
	private void executeCommand(RuleItem item, ItemExecutedResult result){
		Class<AbstractCommand> commandClass;
		try {
			if(StringUtils.isNotEmpty(item.getExecutor())){
				commandClass = (Class<AbstractCommand>) Class.forName(item.getExecutor());
				AbstractCommand command = commandClass.newInstance();
				command.execute(item, result);
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			log.debug(e.getMessage());
		}
	}

	//TODO: 该函数可以被覆盖，可以实现不同的参数传入效果
	
	/**
	 * 根据Param的类型和名称，把Object对象转换成实际的值。
	 * @param param
	 * @param type
	 * @return
	 */
	protected Object getParamValue( String param, String type){

		Object value = "";
		switch(type.toLowerCase()){

			case "object":
				value = this.object.toString();
				break;
				
			case "java.lang.integer":
			case "integer":
				value = Integer.parseInt(this.object.toString());
				break;
			case "java.lang.long":
			case "long":
				value = Long.parseLong(this.object.toString());
				break;
			case "java.lang.boolean":
			case "boolean":
				value = Boolean.parseBoolean(this.object.toString());
				break;		
			case "java.lang.byte":
			case "byte":
				value = Byte.parseByte(this.object.toString());
				break;		
			case "java.lang.double":
			case "dobule":
				value = Double.parseDouble(this.object.toString());
				break;		
			case "java.lang.float":
			case "float":
				value = Float.parseFloat(this.object.toString());
				break;						
			case "java.lang.date":
			case "date":
				SimpleDateFormat sdf = new SimpleDateFormat(PropertyUtil.getProperty("dateFormat"));
				value = sdf.format(this.object);
				break;			
			case "java.math.bigdecimal":
			case "bigdecimal":
				value = new BigDecimal(this.object.toString());
				break;		
			case "java.lang.character":
			case "char":
			case "character":
				value = Character.valueOf(this.object.toString().charAt(0));
				break;				
			default:
				value = this.object.toString();
				break;
		}
		
		return value;
	}


	/**
	 *
	 * @param sqlStr
	 * @return
	 * @throws SQLException
	 * @throws ReflectiveOperationException
	 * @throws RuleEngineException
	 */
	private Map<String, Object> executeSQL(String sqlStr, List<Object> paramList ) throws  RuleEngineException {

		PreparedStatement stmt = null;
        ResultSet res = null;

        Map<String, Object> retMap = new HashMap<String, Object>();

        try {

        	loadDBAccesser();
        	Connection	conn = accesser.getConnection();

            stmt = conn.prepareStatement(sqlStr);
            if(null != paramList && !paramList.isEmpty()){
	            for(int iLoop=0; iLoop < paramList.size(); iLoop++){

	            	Object param = paramList.get(iLoop);
	            	//从1开始计数。
	            	if(param  instanceof String){
	            		stmt.setString(iLoop + 1 , param.toString());
	            	} else if(param instanceof Integer){
	            		stmt.setInt(iLoop+1, ((Integer) param).intValue());
	            	} else if (param instanceof Long){
	            		stmt.setLong(iLoop+1 , ((Long) param).longValue());
	            	// java.util.Date类型对应于timestamp结构了类型。
	            	} else if (param instanceof Date){
	            		stmt.setTimestamp(iLoop + 1, new java.sql.Timestamp(((Date) param).getTime()));
	            	}  else if (param instanceof java.sql.Date){
	            		stmt.setDate(iLoop + 1, (java.sql.Date) param);
	            	}  else if (param instanceof java.sql.Time){
	            		stmt.setTime(iLoop + 1, (java.sql.Time) param);
	            	}  else if (param instanceof java.sql.Timestamp){
	            		stmt.setTimestamp(iLoop + 1, (java.sql.Timestamp) param);
	            	} else if (param instanceof Double){
	            		stmt.setDouble(iLoop +1, ((Double) param).doubleValue());
	            	} else if (param instanceof BigDecimal){
	            		stmt.setBigDecimal(iLoop + 1, (BigDecimal)param);
	            	} else if (param instanceof Boolean){
	            		stmt.setBoolean(iLoop+1, ((Boolean) param).booleanValue());
	            	} else if (param instanceof Byte){
	            		stmt.setByte(iLoop+1, ((Byte) param).byteValue());
	            	} else {
	            		stmt.setObject(iLoop+1, param);
	            	}
	            }
            }

            //stmt = conn.createStatement();
            res = stmt.executeQuery();
            ResultSetMetaData columnInfo = res.getMetaData();

            while(res.next())
            {
            	int columnCount = 	columnInfo.getColumnCount();
            	for(int iLoop =1; iLoop <= columnCount; iLoop++){
            		String colName = columnInfo.getColumnName(iLoop);
            		retMap.put(colName, res.getObject(iLoop));
            	}

            	//无条件break.只读取第一行记录。
            	break;
            }

            res.close();
            stmt.close();



        } catch (SQLException e) {
        	log.debug(e.getMessage());
        	throw new RuleEngineException(e.getCause());

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e){
        	log.debug(e.getMessage());
        	throw new RuleEngineException(e.getCause());

        } catch (Exception e) {
        	log.debug(e.getMessage());
        	throw new RuleEngineException(e.getCause());
		}


		return retMap;
	}


}
