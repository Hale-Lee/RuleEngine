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

package tech.kiwa.engine.component.drools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tech.kiwa.engine.entity.RuleItem;
import tech.kiwa.engine.utility.JavaStringCompiler;
/**
 * @author Hale.Li
 * @since  2018-01-28
 * @version 0.1
 */
public class DroolsBuilder  {


	//private static DroolsBuilder instance = new DroolsBuilder();

	public DroolsBuilder(){

	}

	//public static DroolsBuilder getInstance(){
	//	return instance;
	//}

	private Logger log = LoggerFactory.getLogger(DroolsBuilder.class);

	private List<FunctionCreator> functionList = new ArrayList<FunctionCreator>();
	private List<GlobalCreator> globalList = new ArrayList<GlobalCreator>();
	private List<ImportCreator> importList = new ArrayList<ImportCreator>();
	private List<QueryCreator>  queryList = new ArrayList<QueryCreator>();
	private List<RuleCreator> ruleList = new ArrayList<RuleCreator>();
	private List<DeclareCreator> declareList = new ArrayList<DeclareCreator>();

	//private ClassFileManager<?> fileManager = null;// new ClassFileManager(compiler.getStandardFileManager(null, null, null));
	//private List<JavaFileObject> fileList = new ArrayList<JavaFileObject>();
	private JavaStringCompiler compiler = new JavaStringCompiler();

	private PackageCreator pack = null;

	public List<ImportCreator> getImportList(){
		return importList;
	}

	public PackageCreator getPackage(){
		return pack;
	}

	public List<FunctionCreator> getFunctionList() {
		return functionList;
	}

	public List<GlobalCreator> getGlobalList() {
		return globalList;
	}

	public List<QueryCreator> getQueryList() {
		return queryList;
	}

	public List<RuleCreator> getRuleList() {
		return ruleList;
	}

	public List<DeclareCreator> getDeclareList() {
		return declareList;
	}



    public void build(List<String> phases) {

    	for(String item : phases){

    		item = item.trim();
    		if(item.startsWith("rule")){
	    		ruleList.add(RuleCreator.create(item, this));

    		}else if(item.startsWith("function")){

    			functionList.add(FunctionCreator.create(item, this));

    		}else if(item.startsWith("global")){

    			this.globalList.add(GlobalCreator.create(item, this));

    		} else if(item.startsWith("import")){

    			this.importList.add(ImportCreator.create(item, this));

    		}else if(item.startsWith("query")){

    			this.queryList.add(QueryCreator.create(item, this));

    		}else if(item.startsWith("package")){

    			this.pack = PackageCreator.create(item, this);

    		}else if(item.startsWith("declare")){
    			this.declareList.add(DeclareCreator.create(item, this));
    		}

    	}

    }

   // public ClassFileManager<?> getFileManager(){
   // 	return this.fileManager;
   // }

    public List<RuleItem>  getRuleItemList(){

    	List<RuleItem> retList = new ArrayList<RuleItem>();
    	for(RuleCreator creator: ruleList){
    		retList.add(creator.getItem());
    	}

    	return retList;
    }
/*
	public Class<?> queryClass(String className) throws ClassNotFoundException{

    	return compiler.queryLoadedClass(className);

    	//return compiler.loadClass(className, classBytes);
    }
*/
    private boolean compiled = false;

    public boolean compile(){

    	if(compiled){
    		return compiled;
    	}

		for (DeclareCreator declare : declareList){

			// 生成源代码的JavaFileObject
			//SimpleJavaFileObject fileObject = new JavaSourceFromString(declare.getName(), declare.toJavaString());
			//fileList.add(fileObject);
			try {

				//String clsName = this.getPackage().getName() + "."  + declare.getName();
				String fileName = declare.getName() + ".java";
				compiler.compile(fileName, declare.toJavaString());
			} catch (IOException e) {

			}

		}
		if(!functionList.isEmpty()){
			//String clsName = this.getPackage().getName() + ".DroolsFunctions" ;
			String fileName = "DroolsFunctions.java";
			//SimpleJavaFileObject fileObject = new JavaSourceFromString(clsName, functionList.get(0).toJavaString());
			//fileList.add(fileObject);
			try {
				compiler.compile(fileName, functionList.get(0).toJavaString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.debug(e.getMessage());
				e.printStackTrace();
			}
		}

		return compiled;


    }


    @SuppressWarnings({ "unchecked" })
    public DroolsPartsObject createObject(String className, String javaContent) {

    	try {
    		String fileName = "";
    		int pos = className.lastIndexOf('.');
    		if(pos > 0){
    			fileName = className.substring(pos+1) + ".java";
    		}else{
    			fileName = className  + ".java";
    		}

    		Map<String, byte[]> clsMap = compiler.compile(fileName, javaContent);

			Class<DroolsPartsObject> cls = (Class<DroolsPartsObject>) compiler.loadClass(className, clsMap);
			DroolsPartsObject  obj = (DroolsPartsObject) cls.newInstance();

			return obj;
		} catch (ClassNotFoundException | IOException | InstantiationException | IllegalAccessException e) {
			log.debug(e.getMessage());
			e.printStackTrace();
		}

    	return null;

    }

}
