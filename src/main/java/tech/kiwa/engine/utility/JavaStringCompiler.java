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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import tech.kiwa.engine.utility.MemoryJavaFileManager.MemoryInputJavaFileObject;
/**
 * Java字符串编译器，将字符串对象的Java文件编译成class文件。
 * @author Hale.Li
 * @since  2018-01-28
 * @version 0.1
 */
public class JavaStringCompiler {

    private JavaCompiler compiler;
    private StandardJavaFileManager stdManager;
    private MemoryJavaFileManager fileManager = null;

    private MemoryClassLoader classLoader = null;

    private List<MemoryInputJavaFileObject> javaList = new ArrayList<MemoryInputJavaFileObject>();

    public JavaStringCompiler() {
        this.compiler = ToolProvider.getSystemJavaCompiler();
        this.stdManager = compiler.getStandardFileManager(null,null ,null );
    }

    /**
     * Compile a Java source file in memory.
     *
     * @param fileName
     *            Java file name, e.g. "Test.java"
     * @param source
     *            The source code as String.
     * @return The compiled results as Map that contains class name as key,
     *         class binary as value.
     * @throws IOException
     *             If compile error.
     */
    public Map<String, byte[]> compile(String fileName, String source) throws IOException {

    	if(fileManager == null){
    		fileManager = new MemoryJavaFileManager(stdManager);
    	}

    	MemoryInputJavaFileObject javaFileObject = (MemoryInputJavaFileObject)fileManager.makeStringSource(fileName, source);
    	boolean bFound = false;
    	for(MemoryInputJavaFileObject java : javaList){
    		if(fileName.equals(java.getClassName())){
    			bFound = true;
    			break;
    		}
    	}
        if(!bFound){
        	javaList.add(javaFileObject);
        }
        CompilationTask task = compiler.getTask(null, fileManager,null , null, null, javaList);

        Boolean result = task.call();
        if (result == null || !result.booleanValue()) {
            throw new RuntimeException("Compilation failed.");
        }

        return fileManager.getClassBytes();

    }

    /**
     * Load class from compiled classes.
     *
     * @param name
     *            Full class name.
     * @param classBytes
     *            Compiled results as a Map.
     * @return The Class instance.
     * @throws ClassNotFoundException
     *             If class not found.
     * @throws IOException
     *             If load error.
     */
    public Class<?> loadClass(String name, Map<String, byte[]> classBytes) throws ClassNotFoundException, IOException {

    	if(classLoader == null){
        	classLoader = new MemoryClassLoader(classBytes);
    	}else{
    		classLoader.appendClass(classBytes);
    	}

        return classLoader.loadClass(name);
    }

    /**
     * 根据Java类名查找Java类。
     * @param name	-- Java类名，简称/
     * @return      -- Class对象
     * @throws ClassNotFoundException
     */
    public Class<?> queryLoadedClass(String name) throws ClassNotFoundException{

    	//MemoryClassLoader classLoader = new MemoryClassLoader();
    	if(classLoader == null){
    		return classLoader.queryLoadedClass(name);
    	}

    	return null;

    }


}