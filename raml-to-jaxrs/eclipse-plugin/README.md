![](http://raml.org/images/logo.png)

# RAML to JAX-RS converter - Eclipse Plug-in

This Eclipse plug-in generates JAX-RS annotated interfaces and supporting classes based on one or multiple RAML files.

_NB. The following documentation will soon be superseded by the Maven-generated plug-in documentation._

## Usage

Select your RAML file in the Package Explorer. Invoke the context menu and click the "RAML to JAX-RS" item to open the configuration dialog.

![](/raml-to-jaxrs/eclipse-plugin/doc/popup.png)

Use the dialog to configure parameters and launch the generation process.

![](/raml-to-jaxrs/eclipse-plugin/doc/dialog.png)

| Option      | Description   |
| -------------- |-------------| 
| JAX-RS version | Version of JAX-RS framework to be used during generation |
| JSON Mapper    | Annotation framework used to map JSON      |
| Use JSR 303 Annotations   | Enable or not JSR 303 Java Bean validation    |
