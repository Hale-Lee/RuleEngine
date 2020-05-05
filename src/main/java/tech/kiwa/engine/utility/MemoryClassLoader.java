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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 类加载器，把Java类加载到内存中。
 * @author Hale.Li
 * @since  2018-01-28
 * @version 0.1
 */
public class MemoryClassLoader extends ClassLoader {
    // class name to class bytes:
    private Map<String, byte[]> classBytes = new HashMap<String, byte[]>();

    public MemoryClassLoader(Map<String, byte[]> classBytes) {
        super(MemoryClassLoader.class.getClassLoader());
        this.classBytes.putAll(classBytes);
    }

    public MemoryClassLoader(){

    }
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] buf = classBytes.get(name);
        if (buf == null) {
            return super.findClass(name);
        }
        //classBytes.remove(name);
        return defineClass(name, buf, 0, buf.length);
    }

    public Class<?> queryLoadedClass(String name) throws ClassNotFoundException {
    	return super.findClass(name);
    }

    public void appendClass( Map<String, byte[]> classSet){

    	Set<String> keySet = classSet.keySet();
    	for(String key: keySet){
    		if(!classBytes.containsKey(key)){
    			classBytes.put(key, classSet.get(key));
    		}
    	}
    }

}
