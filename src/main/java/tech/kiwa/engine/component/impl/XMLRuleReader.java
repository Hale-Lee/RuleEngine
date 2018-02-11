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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tech.kiwa.engine.component.AbstractRuleReader;
import tech.kiwa.engine.entity.RuleItem;
import tech.kiwa.engine.exception.RuleEngineException;
import tech.kiwa.engine.utility.PropertyUtil;

/**
 * @author Hale.Li
 * @since  2018-01-28
 * @version 0.1
 */
public class XMLRuleReader extends AbstractRuleReader {

	private static Logger log = LoggerFactory.getLogger(XMLRuleReader.class);

	private List<RuleItem> itemList = null;

	@Override
	public List<RuleItem> readRuleItemList() throws RuleEngineException {

		itemList = new ArrayList<RuleItem>();

		try {
			// 创建DOM文档对象
			DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dFactory.newDocumentBuilder();
			Document doc;

			String configFile = PropertyUtil.getProperty("xml.rule.filename");
			if(!configFile.startsWith(File.separator)){
				File dir = new File(PropertyUtil.class.getClassLoader().getResource("").getPath());
				configFile = dir + File.separator + configFile;
			}

			doc = builder.parse(new File(configFile));

			// 获取包含类名的文本节点
			NodeList ruleList = doc.getElementsByTagName("rule");
			for(int iLoop =0; iLoop < ruleList.getLength();iLoop++){

				RuleItem item = new RuleItem();

				Node rule = ruleList.item(iLoop);
				NamedNodeMap attributes = rule.getAttributes();
				if(null == attributes || attributes.getNamedItem("id") == null){
					log.debug("rule id must not be null. rule.context = {}", rule.getTextContent());
					return null;
				}

				String xmlRuleId = attributes.getNamedItem("id").getNodeValue();

				item.setItemNo(xmlRuleId);
				for(int jLoop =0 ; jLoop < attributes.getLength(); jLoop++){
					Node  node = attributes.item(jLoop);
					item.setMappedValue(node.getNodeName(), node.getNodeValue());
				}

				// alias of attribute name.
				if(attributes.getNamedItem("class") != null){
					item.setExeClass(attributes.getNamedItem("class").getNodeValue());
				}
				if(attributes.getNamedItem("method") != null){
					item.setExecutor(rule.getAttributes().getNamedItem("method").getNodeValue());
				}
				if(attributes.getNamedItem("parent") != null){
					item.setParentItemNo(rule.getAttributes().getNamedItem("parent").getNodeValue());
				}

				if(rule.hasChildNodes()){
					Node child = rule.getFirstChild();
					while(child != null){

						if("property".equalsIgnoreCase(child.getNodeName())){
							NamedNodeMap childAttrs = child.getAttributes();
							Node nameNode = childAttrs.getNamedItem("name");
							Node valueNode = childAttrs.getNamedItem("value");
							if(valueNode == null || nameNode == null){
								throw new RuleEngineException("rule format error, attribute value or name must existed.");
							}
							Node typeNode = childAttrs.getNamedItem("type");
							item.setMappedValue(nameNode.getNodeValue(), valueNode.getNodeValue());

							if("param".equalsIgnoreCase(nameNode.getNodeValue())){
								item.setParamName(valueNode.getNodeValue());
								item.setParamType(typeNode.getNodeValue());
							}
							if("comparison".equalsIgnoreCase(nameNode.getNodeValue())){
								Node codeNode = childAttrs.getNamedItem("code");
								item.setComparisonCode(codeNode.getNodeValue());
								item.setComparisonValue(valueNode.getNodeValue());
							}

						}

						child = child.getNextSibling();
					}
				}	// endif hasChildNodes.

				if(!compile(item)){
					log.debug("xml rule format error.");
					throw new RuleEngineException("rule format error.");
					//return null;
				}

				itemList.add(item);

			}	// end for


		} catch (Exception e) {
			log.debug(e.getMessage());
			throw new RuleEngineException(e.getCause());
			//return null;
		}

		return itemList;

	}

	@Override
	public Long getRuleItemCount() throws RuleEngineException {

		if(itemList == null){
			this.readRuleItemList();
			return (long) itemList.size();
		}

		return 0L;
	}

	@Override
	public RuleItem getRuleItem(String ruleId) throws RuleEngineException {

		if(itemList == null){
			this.readRuleItemList();
		}

		for(RuleItem rule: itemList){
			if(rule.getItemNo().equalsIgnoreCase(ruleId)){
				return rule;
			}
		}


		RuleItem item = new RuleItem();
		try {
			// 创建DOM文档对象
			DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dFactory.newDocumentBuilder();
			Document doc;

			String configFile = PropertyUtil.getProperty("xml.rule.filename");
			if(!configFile.startsWith(File.separator)){
				File dir = new File(PropertyUtil.class.getClassLoader().getResource("").getPath());
				configFile = dir + File.separator + configFile;
			}

			doc = builder.parse(new File(configFile));

			// 获取包含类名的文本节点
			NodeList ruleList = doc.getElementsByTagName("rule");
			for(int iLoop =0; iLoop < ruleList.getLength();iLoop++){

				Node rule = ruleList.item(iLoop);
				NamedNodeMap attributes = rule.getAttributes();
				if(null == attributes || attributes.getNamedItem("id") == null){
					log.debug("rule id must not be null.");
					return null;
				}

				String xmlRuleId = attributes.getNamedItem("id").getNodeValue();


				if(!ruleId.equalsIgnoreCase(xmlRuleId)){
					continue;
				}

				item.setItemNo(xmlRuleId);
				for(int jLoop =0 ; jLoop < attributes.getLength(); jLoop++){
					Node  node = attributes.item(jLoop);
					item.setMappedValue(node.getNodeName(), node.getNodeValue());
				}

				// alias attribute name.
				if(attributes.getNamedItem("class") != null){
					item.setExeClass(attributes.getNamedItem("class").getNodeValue());
				}
				if(attributes.getNamedItem("method") != null){
					item.setExecutor(rule.getAttributes().getNamedItem("method").getNodeValue());
				}
				if(attributes.getNamedItem("parent") != null){
					item.setParentItemNo(rule.getAttributes().getNamedItem("parent").getNodeValue());
				}

				if(rule.hasChildNodes()){
					Node child = rule.getFirstChild();
					while(child != null){

						if("property".equalsIgnoreCase(child.getNodeName())){
							NamedNodeMap childAttrs = child.getAttributes();
							Node nameNode = childAttrs.getNamedItem("name");
							Node valueNode = childAttrs.getNamedItem("value");
							item.setMappedValue(nameNode.getNodeValue(), valueNode.getNodeValue());
						}

						child = child.getNextSibling();
					}
				}

				if(!compile(item)){
					log.debug("xml rule format error.");
					throw new RuleEngineException("rule format error.");
					//return null;
				}

				// found , then return.
				if(ruleId.equalsIgnoreCase(xmlRuleId)){
					return item;
					//break;
				}
			}


		} catch (Exception e) {
			log.debug(e.getMessage());
			throw new RuleEngineException(e.getCause());
			//return null;
		}

		return null;
	}




	public static void main(String[] args){
		XMLRuleReader reader = new XMLRuleReader();
		try {
			reader.getRuleItem("blacklist");
		} catch (RuleEngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
