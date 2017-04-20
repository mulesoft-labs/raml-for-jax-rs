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
package org.raml.jaxrs.cli;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.raml.jaxrs.converter.RamlConfiguration;
import org.raml.jaxrs.raml.core.DefaultRamlConfiguration;
import org.raml.jaxrs.raml.core.OneStopShop;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class Main {

  public static void main(String[] args) throws Exception {

    Options options = new Options();
    options.addOption(Option.builder("a").required().longOpt("applicationDirectory").hasArg().desc("application path").build());
    options.addOption(Option.builder("o").required().longOpt("output").hasArg().desc("RAML output file").build());
    options.addOption("s", "sourceRoot", true, "JaxRs source root");
    options.addOption("t", "translatedAnnotations", true, "translated annotation list (comma separated");

    try {

      CommandLineParser parser = new DefaultParser();
      CommandLine command = parser.parse(options, args);

      Path jaxRsResourceFile = Paths.get(command.getOptionValue('a'));
      Path ramlOutputFile = Paths.get(command.getOptionValue('o'));

      Path jaxRsSourceRoot = null;
      if (command.hasOption('s')) {

        jaxRsSourceRoot = Paths.get(command.getOptionValue('s'));
      }


      RamlConfiguration ramlConfiguration =
          DefaultRamlConfiguration.forApplication(jaxRsResourceFile.getFileName().toString(),
                                                  Collections.<Class<? extends Annotation>>emptySet());

      OneStopShop.Builder builder =
          OneStopShop.builder().withJaxRsClassesRoot(jaxRsResourceFile)
              .withRamlOutputFile(ramlOutputFile).withRamlConfiguration(ramlConfiguration);

      if (null != jaxRsSourceRoot) {
        builder.withSourceCodeRoot(jaxRsSourceRoot);
      }

      if (command.hasOption('t')) {

        String[] classes = command.getOptionValue('t').split(",");
        List<Class<? extends Annotation>> c =
            FluentIterable.of(classes).transform(
                                                 new Function<String, Class<? extends Annotation>>() {

                                                   @Nullable
                                                   @Override
                                                   public Class<? extends Annotation> apply(@Nullable String input) {

                                                     try {
                                                       return (Class<? extends Annotation>) Class.forName(input);
                                                     } catch (ClassNotFoundException e) {
                                                       throw new IllegalArgumentException(
                                                                                          "while building translated annotations list",
                                                                                          e);
                                                     }
                                                   }
                                                 }).toList();

        builder.withTranslatedAnnotations(c);
      }
      OneStopShop oneStopShop = builder.build();

      oneStopShop.parseJaxRsAndOutputRaml();
    } catch (ParseException e) {
      HelpFormatter formatter = new HelpFormatter();
      System.err.println(e.getMessage());
      formatter.printHelp("jaxrstoraml", options, true);

    }
  }
}
