#/bin/sh

# NOTE:
# This is a temporary script to prototype an acceptable CI process.
# All of the logic below can, and should, be re-implemented in maven.
# Please delete this script once this has happened, otherwise
# this script will break as soon as POM versions change. :-/

# S3 buckets:
# http://raml-tools.mulesoft.com
#   -> http://raml-tools.mulesoft.com.s3-website-us-east-1.amazonaws.com
#   -> s3://raml-tools.mulesoft.com
# http://raml-tools-stage.mulesoft.com
#   -> http://raml-tools-stage.mulesoft.com.s3-website-us-east-1.amazonaws.com
#   -> s3://raml-tools-stage.mulesoft.com

set -e -x

mkdir -p target/raml-for-jax-rs

#
# Eclipse Plugin
#

mkdir -p target/raml-for-jax-rs/eclipse
cp -r eclipse/updateSite/target/repository/* target/raml-for-jax-rs/eclipse
# ?? do we need ./eclipse/plugins/snakeyaml*.jar ??


#
# Command Line Interface (CLI)
#
mkdir -p target/raml-for-jax-rs/CLI
cp raml-to-jaxrs/core/target/raml-jaxrs-codegen-core-1.0.6-SNAPSHOT-jar-with-dependencies.jar target/raml-for-jax-rs/CLI/raml-to-jaxrs.jar
cp jaxrs-to-raml/com.mulesoft.jaxrs.raml.generator.annotations/target/com.mulesoft.jaxrs.raml.generator.annotations-0.0.1-SNAPSHOT-jar-with-dependencies.jar target/raml-for-jax-rs/CLI/jax-rs-to-raml.jar

#
# Maven Plug-ins
#

# not sure how to stage this (is this is a nexus upload?)


