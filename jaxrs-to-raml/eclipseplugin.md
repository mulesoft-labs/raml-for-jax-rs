# Eclipse Plugin Installation Instructions:

You can install the JAXRS-to-RAML plugin for Eclipse/Mule Studio by using the "Software Update" feature of your IDE. To do so, follow these steps:
- Start Eclipse or Mule Studio.
- Select Help > Install New Software.
- In the "New Software" window, enter the update site URL (http://raml-tools.mulesoft.com/jaxrs-to-raml/eclipse) into the "Work with" textbox, and press the enter.
- Select the checkbox next to "RAML Tools". Click Next.
- Click Next again.
- Read the license agreements, accept these, and click Finish.

At some point of the installation, you will see a pop-up warning that you are installing software that contains unsigned content.
This means that Eclipse cannot verify the identity of the plugin authors because some of the jars are not signed. Click 'Ok'.

**Note**: If you want to learn more about signing JARs, you can find useful information here: http://wiki.eclipse.org/JAR_Signing.

Once the installation finishes, you will see a dialog asking you to restart Eclipse. Click "Restart Now".

That's it. Now you are ready to start generating RAML APIs from your existing JAXRS Applications!

#Usage

- Select a Java class, package or source folder
- Right click to show the context menu.

![context-menu](https://raw.githubusercontent.com/mulesoft/jaxrs-to-raml/master/doc-images/context-menu.png?token=2273179__eyJzY29wZSI6IlJhd0Jsb2I6bXVsZXNvZnQvamF4cnMtdG8tcmFtbC9tYXN0ZXIvZG9jLWltYWdlcy9jb250ZXh0LW1lbnUucG5nIiwiZXhwaXJlcyI6MTQwNDQ4NTQ0NX0%3D--67c11aed28e27697a348a7d34fca6b231964911b)
- "Generate RAML from classes"

![configuration-window](https://raw.githubusercontent.com/mulesoft/jaxrs-to-raml/master/doc-images/configuration-window.png?token=2273179__eyJzY29wZSI6IlJhd0Jsb2I6bXVsZXNvZnQvamF4cnMtdG8tcmFtbC9tYXN0ZXIvZG9jLWltYWdlcy9jb25maWd1cmF0aW9uLXdpbmRvdy5wbmciLCJleHBpcmVzIjoxNDA0NDg1NTE4fQ%3D%3D--fc0943b62351bede5c049d8b07bd1a0f73fd96bc)

Choose the options according to your needs (check "Configuration Options" section for a detailed explanation) and click OK. **Your API is ready!**

##Configuration Options

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
