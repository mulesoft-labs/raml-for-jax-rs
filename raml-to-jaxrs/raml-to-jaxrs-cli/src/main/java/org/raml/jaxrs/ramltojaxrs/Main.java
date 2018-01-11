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
package org.raml.jaxrs.ramltojaxrs;

import com.google.common.base.Optional;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jsonschema2pojo.AnnotationStyle;
import org.raml.jaxrs.generator.Configuration;
import org.raml.jaxrs.generator.GenerationException;
import org.raml.jaxrs.generator.RamlScanner;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 11/10/16. Just potential zeroes and ones
 */
public class Main {

  public static void main(String[] args) throws IOException, GenerationException, ParseException {

    Options options = new Options();
    options.addOption("j", "json-mapper", true, "jsonschema2pojo annotation types (jackson, jackson2 or gson)");
    options.addOption("m", "model-package", true, "model package");
    options.addOption("s", "support-package", true, "support package");
    options.addOption("g", "generate-types-with", true, "generate types with plugins (jackson, gson, jaxb, javadoc, jsr303)");
    options.addOption(Option.builder("r").required().longOpt("resource-package").hasArg().desc("resource package").build());
    options.addOption(Option.builder("d").required().longOpt("directory").hasArg().desc("generation directory").build());

    try {

      CommandLineParser parser = new DefaultParser();
      CommandLine command = parser.parse(options, args);
      String modelDir = command.getOptionValue("m");
      String supportDir = command.getOptionValue("s");
      String resourceDir = command.getOptionValue("r");
      String directory = command.getOptionValue("d");
      String extensions = command.getOptionValue("g");
      Optional<String> jsonMapper = Optional.fromNullable(command.getOptionValue("j"));

      List<String> ramlFiles = command.getArgList();

      Configuration configuration = new Configuration();
      configuration.setModelPackage(modelDir);
      configuration.setResourcePackage(resourceDir);
      configuration.setSupportPackage(supportDir);
      configuration.setOutputDirectory(new File(directory));
      configuration.setJsonMapper(AnnotationStyle.valueOf(jsonMapper.or("jackson2").toUpperCase()));

      if (extensions != null) {
        configuration.setTypeConfiguration(extensions.split(("\\s*,\\s*")));
      }

      RamlScanner scanner = new RamlScanner(configuration);

      for (String ramlFile : ramlFiles) {

        URLClassLoader ucl =
            new URLClassLoader(new URL[] {new File(ramlFile).getParentFile().toURL()}, Main.class.getClassLoader());
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
          Thread.currentThread().setContextClassLoader(ucl);
          scanner.handle(new File(ramlFile));
        } finally {

          Thread.currentThread().setContextClassLoader(loader);
        }
      }

    } catch (ParseException e) {

      HelpFormatter formatter = new HelpFormatter();
      System.err.println(e.getMessage());
      formatter.printHelp("ramltojaxrs", options, true);
    }

    System.getProperties().remove("ramltojaxrs");
  }
}
