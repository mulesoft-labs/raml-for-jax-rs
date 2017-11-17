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
package org.raml.jaxrs.generator.v10;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.SetMultimap;
import org.raml.jaxrs.generator.GenerationException;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created. There, you have it.
 */
public class ExtensionManager {

  private SetMultimap<String, Class> info;

  ExtensionManager(SetMultimap<String, Class> info) {

    this.info = info;
  }

  public static ExtensionManager createExtensionManager() {

    return createExtensionManager("META-INF/ramltojaxrs-plugin.properties");
  }

  static ExtensionManager createExtensionManager(String pluginFileName) {

    try {
      SetMultimap<String, Class> info = LinkedHashMultimap.create();
      Enumeration<URL> resourcesFiles = ExtensionManager.class.getClassLoader().getResources(pluginFileName);

      while (resourcesFiles.hasMoreElements()) {
        URL url = resourcesFiles.nextElement();
        Properties properties = new Properties();
        loadProperties(url, properties);
        buildPluginNames(info, properties);
      }

      return new ExtensionManager(info);

    } catch (IOException e) {

      throw new GenerationException(e);
    }

  }


  public Set<Class> getClassesForName(String name) {

    return info.get(name);
  }

  private static void buildPluginNames(SetMultimap<String, Class> info, Properties properties) {

    for (String name : properties.stringPropertyNames()) {

      List<Class> classList = classList(name, properties.getProperty(name));
      if (info.containsKey(name)) {

        throw new GenerationException("duplicate name in plugins: " + name);
      }
      info.putAll(name, classList);
    }
  }

  private static List<Class> classList(String name, String property) {

    List<Class> classes = new ArrayList<>();
    for (String s : property.split("\\s*,\\s*")) {
      try {
        Class foundClass = Class.forName(s);
        classes.add(foundClass);
      } catch (ClassNotFoundException e) {
        throw new GenerationException("class " + s + " not found for plugin name " + name);
      }
    }

    return classes;
  }

  private static void loadProperties(URL url, Properties p) {
    try {
      p.load(url.openStream());
    } catch (IOException e) {

    }
  }
}
