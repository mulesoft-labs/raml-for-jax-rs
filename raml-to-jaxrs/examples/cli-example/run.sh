#!/bin/bash

echo "*** run mvn assembly:assembly -DdescriptorId=jar-with-dependencies"
echo "*** in raml-to-jaxrs/core first to make jar"

java -cp ..\..\core\target\raml-jaxrs-codegen-core-1.3.4-jar-with-dependencies.jar org.raml.jaxrs.codegen.core.Launcher -basePackageName com.somecompany.sample -outputDirectory output -sourceDirectory raml


