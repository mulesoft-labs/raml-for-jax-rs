/*
 * Copyright 2013-2015 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.jaxrs.codegen.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.jci.readers.ResourceReader;


public final class FileResourceReader2 implements ResourceReader {

    private final File root;

    public FileResourceReader2( final File pRoot ) {
        root = pRoot;        
    }
    
    public boolean isAvailable(  String pResourceName ) {
    	pResourceName=transform(pResourceName);
        return new File(root, pResourceName).exists();
    }

    private String transform(String pResourceName) {
    	pResourceName=pResourceName.replace('.','/' );
    	int lastIndexOf = pResourceName.lastIndexOf('/');
    	pResourceName=pResourceName.substring(0,lastIndexOf)+'.'+pResourceName.substring(lastIndexOf+1);
		return pResourceName;
	}

	public byte[] getBytes( String pResourceName ) {
        try {
        	pResourceName=transform(pResourceName);
            return FileUtils.readFileToString(new File(root, pResourceName), "UTF-8").getBytes();
        } catch(Exception e) {
            return null;
        }
    }
    
    /**
     * @deprecated
     */
    @Deprecated
    public String[] list() {
        final List<String> files = new ArrayList<String>();
        list(root, files);
        return files.toArray(new String[files.size()]);
    }

    /**
     * @deprecated
     */
    @Deprecated
    private void list( final File pFile, final List<String> pFiles ) {
        if (pFile.isDirectory()) {
            final File[] directoryFiles = pFile.listFiles();
            for (int i = 0; i < directoryFiles.length; i++) {
                list(directoryFiles[i], pFiles);
            }
        } else {
            pFiles.add(pFile.getAbsolutePath().substring(root.getAbsolutePath().length()+1));
        }
    }   
}
