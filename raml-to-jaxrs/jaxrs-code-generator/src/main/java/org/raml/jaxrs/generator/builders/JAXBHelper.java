/*
 * Copyright 2013-2017 (c) MuleSoft, Inc.
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
package org.raml.jaxrs.generator.builders;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.ErrorReceiver;
import com.sun.tools.xjc.Language;
import com.sun.tools.xjc.ModelLoader;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.util.ErrorReceiverFilter;
import org.raml.jaxrs.generator.GenerationException;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 11/20/16. Just potential zeroes and ones
 */
public class JAXBHelper {


  public static File saveSchema(String schema, File directory) throws IOException {

    File tmpFile = File.createTempFile("schema", ".xml", directory);
    FileWriter tmp = new FileWriter(tmpFile);
    tmp.write(schema);
    tmp.close();
    return tmpFile;
  }


  public static Map<String, JClass> generateClassesFromXmlSchemas(String pack, File file,
                                                                  JCodeModel codeModel) throws GenerationException {

    HashMap<String, JClass> classNameToKeyMap = new HashMap<>();

    List<JDefinedClass> classes = generateClassesFromXmlSchemas(pack, codeModel, file);
    for (JDefinedClass aClass : classes) {

      String className = aClass.name();
      classNameToKeyMap.put(className, aClass);
    }

    return classNameToKeyMap;
  }

  private static List<JDefinedClass> generateClassesFromXmlSchemas(String pack,
                                                                   JCodeModel codeModel, File schemaFile)
      throws GenerationException {
    try {
      ArrayList<JDefinedClass> classList = new ArrayList<JDefinedClass>();

      ArrayList<String> argList = new ArrayList<>();
      argList.add("-mark-generated");
      argList.add("-p");
      argList.add(pack);
      argList.add(schemaFile.getAbsolutePath());

      String[] args = argList.toArray(new String[argList.size()]);

      final Options opt = new Options();
      opt.setSchemaLanguage(Language.XMLSCHEMA);
      opt.parseArguments(args);

      ErrorReceiver receiver = new ErrorReceiverFilter() {

        @Override
        public void info(SAXParseException exception) {
          if (opt.verbose)
            super.info(exception);
        }

        @Override
        public void warning(SAXParseException exception) {
          if (!opt.quiet)
            super.warning(exception);
        }
      };

      Model model = ModelLoader.load(opt, codeModel, receiver);
      Outline outline = model.generateCode(opt, receiver);
      for (ClassOutline co : outline.getClasses()) {
        JDefinedClass cl = co.implClass;
        if (cl.outer() == null) {
          classList.add(cl);
        }
      }
      return classList;
    } catch (Exception e) {
      throw new GenerationException(e);
    }
  }

}
