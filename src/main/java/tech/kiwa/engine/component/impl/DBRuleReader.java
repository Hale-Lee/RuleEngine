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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tech.kiwa.engine.component.AbstractRuleReader;
import tech.kiwa.engine.entity.RuleItem;
import tech.kiwa.engine.exception.RuleEngineException;
import tech.kiwa.engine.framework.DBAccesser;
import tech.kiwa.engine.utility.PropertyUtil;

/**
 * @author Hale.Li
 * @since  2018-01-28
 * @version 0.1
 */
public class DBRuleReader extends AbstractRuleReader {

	private static Logger log = LoggerFactory.getLogger(DBRuleReader.class);

	private static volatile DBAccesser accesser = null;		//数据库访问类。

	@SuppressWarnings("unchecked")
	private DBAccesser loadDBAccesser() throws ClassNotFoundException, InstantiationException, IllegalAccessException{

		if(accesser == null){

			synchronized(DBAccesser.class){
				String className = PropertyUtil.getProperty("db.accesser");
				Class<DBAccesser> DBClass = (Class<DBAccesser>) Class.forName(className);

				accesser = DBClass.newInstance();
			}
		}

		return accesser;
	}

	@Override
	public List<RuleItem> readRuleItemList() throws RuleEngineException {


        Statement stmt = null;
        ResultSet res = null;

        List<RuleItem> retList = new ArrayList<RuleItem>();

        String sqlStr = " select * from "+ PropertyUtil.getProperty("db.rule.table") +" where ENABLE_FLAG = 1 order by PRIORITY desc ";
        try {

        	loadDBAccesser();
        	Connection	conn = accesser.getConnection();

            stmt = conn.prepareStatement(sqlStr);
            //stmt = conn.createStatement();
            res = stmt.executeQuery(sqlStr);
            while(res.next())
            {
            	RuleItem rule = new RuleItem();
            	rule.setItemNo(res.getString("ITEM_NO"));
            	rule.setContent(res.getString("content"));
                rule.setExeSql(res.getString("EXE_SQL"));
                rule.setExeClass(res.getString("exe_class"));
                rule.setParamType(res.getString("param_type"));
                rule.setParamName(res.getString("PARAM_NAME"));
                rule.setComparisonCode(res.getString("comparison_code"));
                rule.setComparisonValue(res.getString("comparison_value"));
                rule.setBaseline(res.getString("BASELINE"));
                rule.setResult(res.getString("result"));
                rule.setExecutor(res.getString("EXECUTOR"));
                rule.setPriority(res.getString("PRIORITY"));
                rule.setParentItemNo(res.getString("PARENT_ITEM_NO"));
                rule.setGroupExpress(res.getString("group_express"));
                rule.setContinueFlag(res.getString("CONTINUE_FLAG"));
                rule.setRemark(res.getString("REMARK"));
                rule.setComments(res.getString("COMMENTS"));
                rule.setEnableFlag(res.getString("ENABLE_FLAG"));
                rule.setCreateTime(res.getDate("CREATE_TIME"));
                rule.setUpdateTime(res.getDate("UPDATE_TIME"));
        		if(!preCompile(rule)){
        			log.debug("database rule format error.");
        			throw new RuleEngineException("rule format error.");
        			//return null;
        		}

                retList.add(rule);
            }

            res.close();
            stmt.close();

            //accesser.closeConnection(conn);

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

        return retList;

	}

	@Override
	public Long getRuleItemCount() throws RuleEngineException {


        Statement stmt = null;
        ResultSet res = null;

        long  count = 0;

        String sqlStr = " select count(*) from "+ PropertyUtil.getProperty("db.rule.table") +" where ENABLE_FLAG = 1 ";
        try {

        	loadDBAccesser();
        	Connection	conn = accesser.getConnection();

            stmt = conn.prepareStatement(sqlStr);
            //stmt = conn.createStatement();
            res = stmt.executeQuery(sqlStr);
            res.next();
            count = res.getLong(1);

            res.close();
            stmt.close();

            //accesser.closeConnection(conn);

        } catch (SQLException e) {
        	log.debug(e.getMessage());
        	throw new RuleEngineException(e.getCause());
        } catch (Exception e) {
        	log.debug(e.getMessage());
        	throw new RuleEngineException(e.getCause());
		}


		return count;
	}

	@Override
	public RuleItem getRuleItem(String ruleId) throws RuleEngineException {


		PreparedStatement stmt = null;
        ResultSet res = null;

        List<RuleItem> retList = new ArrayList<RuleItem>();

        String sqlStr = " select * from "+ PropertyUtil.getProperty("db.rule.table") +" where ENABLE_FLAG = 1  and ITEM_NO = ? ";
        try {

        	loadDBAccesser();
        	Connection	conn = accesser.getConnection();

            stmt = conn.prepareStatement(sqlStr);
            stmt.setString(1, ruleId);
            res = stmt.executeQuery();

            while(res.next())
            {
            	RuleItem rule = new RuleItem();
            	rule.setItemNo(res.getString("ITEM_NO"));
            	rule.setContent(res.getString("content"));
                rule.setExeSql(res.getString("EXE_SQL"));
                rule.setExeClass(res.getString("exe_class"));
                rule.setParamType(res.getString("param_type"));
                rule.setParamName(res.getString("PARAM_NAME"));
                rule.setComparisonCode(res.getString("comparison_code"));
                rule.setComparisonValue(res.getString("comparison_value"));
                rule.setBaseline(res.getString("BASELINE"));
                rule.setResult(res.getString("result"));
                rule.setExecutor(res.getString("EXECUTOR"));
                rule.setPriority(res.getString("PRIORITY"));
                rule.setParentItemNo(res.getString("PARENT_ITEM_NO"));
                rule.setGroupExpress(res.getString("group_express"));
                rule.setContinueFlag(res.getString("CONTINUE_FLAG"));
                rule.setRemark(res.getString("REMARK"));
                rule.setComments(res.getString("COMMENTS"));
                rule.setEnableFlag(res.getString("ENABLE_FLAG"));
                rule.setCreateTime(res.getDate("CREATE_TIME"));
                rule.setUpdateTime(res.getDate("UPDATE_TIME"));
        		if(!preCompile(rule)){
        			log.debug("database rule format error.");
        			throw new RuleEngineException("rule format error.");
        			//return null;
        		}

                retList.add(rule);
                //只读一条记录。
                break;
            }

            res.close();
            stmt.close();

            //accesser.closeConnection(conn);

        } catch (SQLException e) {
        	log.debug(e.getMessage());
        	throw new RuleEngineException(e.getCause());
        } catch (Exception e) {
        	log.debug(e.getMessage());
        	throw new RuleEngineException(e.getCause());
		}

        if(retList.isEmpty()){
        	return null;
        }

        return retList.get(0);

	}



	public static void main(String[] args){
		DBRuleReader reader = new DBRuleReader();

		try {
			reader.getRuleItem("3");
			reader.readRuleItemList();
		} catch (RuleEngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

