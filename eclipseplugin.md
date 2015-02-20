![](http://raml.org/images/logo.png)

# Eclipse Plugin Installation Instructions:

You can install the "RAML for JAX-RS" plug-in for Eclipse/Mule Studio by using the "Software Update" feature of your IDE. To do so, follow these steps:
- Start Eclipse or Mule Studio.
- Select Help > Install New Software.
- In the "New Software" window, enter the update site URL (https://s3.amazonaws.com/raml-tools-stage.mulesoft.com/raml-for-jax-rs/current/eclipse) into the "Work with" textbox, and press the enter.
- Select the checkbox next to "RAML Tools". Click Next.
- Click Next again.
- Read the license agreements, accept these, and click Finish.

At some point of the installation, you will see a pop-up warning that you are installing software that contains unsigned content.
This means that Eclipse cannot verify the identity of the plugin authors because some of the jars are not signed. Click 'Ok'.

**Note**: If you want to learn more about signing JARs, you can find useful information here: http://wiki.eclipse.org/JAR_Signing.

Once the installation finishes, you will see a dialog asking you to restart Eclipse. Click "Restart Now".

That's it. Now you are ready to start generating RAML APIs from your existing JAX-RS Applications!

### Aquiring staging build
You may use http://raml-tools-stage.mulesoft.com.s3.amazonaws.com/raml-for-jax-rs/current/eclipse as update site url to get latest staging build

## JAX-RS to RAML
 
 You may use  the "New Software" window, enter the update site URL (http://raml-tools.mulesoft.com/raml-for-jax-rs/eclipse) 

### Usage

- Select a Java class, package or source folder
- Right click to show the context menu.

![context-menu](/jaxrs-to-raml/doc/configuration-window.png)
- "Generate RAML from classes"

![configuration-window](/jaxrs-to-raml/doc/context-menu.png)

Choose the options according to your needs (check "Configuration Options" section for a detailed explanation) and click OK. **Your API is ready!**

####Configuration Options

The plugin supports the following configuration options:

- File name for your RAML file: Name for a generated RAML file
- Folder: Select the folder for your generated RAML files.
- Basic settings tab: Configure the default options for your RAML file:
  - API Title.
  - API Version.
  - Base URL.
  - Protocols selection.
- Response codes tab: Configure the default response codes for the HTTP methods.
- Generate an individual RAML per each Java Class: When checked, a separate RAML file will be generated for each Java class annotated with @Path.
- Sort resources alphabetically: When checked, the resources will be sorted alphabetically. When not, the resources are generated in the same order than the methods are written in the Java code.
- Skip resources with no methods: When checked, the tool won't generate a resource entry (in the RAML definition) for those URL path segments which are not exposing any HTTP method. This will result in a more flat resource tree.
- Generate schemas and examples in a single RAML file: When checked, schemas and examples will be placed in the generated RAML file,
 otherwise these will be placed in separate files and include links will be inserted in the generated RAML file. **Note:** This option is not available if you have selected "Generate an individual RAML per each Java Class" since there is not a single (not only one) RAML file where to include the schemas and examples.


## RAML to JAX-RS

###Usage

Select your RAML file in the Package Explorer. Invoke the context menu and click the "RAML to JAX-RS" item to open the configuration dialog.

![](/raml-to-jaxrs/eclipse-plugin/doc/popup.png)

Use the dialog to configure parameters and launch the generation process.

![](/raml-to-jaxrs/eclipse-plugin/doc/dialog.png)

####Configuration Options

-  JAX-RS version -version of JAX-RS framework to be used during generation
-  JSON Mapper - annotation framework used to map JSON
-  Use JSR 303 Annotations - Enable or not JSR 303 Java Bean validation
-  mapToVoid: If set this option to true methods with empty bodies will have void resource type, otherwise we still will generate response wrapper for them.
