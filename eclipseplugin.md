# Installation instructions:

You can install the JAXRS-to-RAML plugin for Eclipse/Mule Studio by using the "Software Update" feature from your IDE.

- Start Eclipse or Mule Studio.
- Select Help > Install New Software.
- In the emerging dialog, enter the update site URL (http://jaxrstoraml.appspot.com/site/) into the "Work with" textbox, and press the enter.
- Select the checkbox next to "RAML Tools". Click Next.
- Click Next again.
- Read the license agreements, accept these, and click Finish.

At some point of the installation, you will see a pop-up warning that you are installing software that contains unsigned content.
This means that eclipse cannot verify the identity of the plugin authors because some of the jars are not signed. Click 'Ok'.

**Note**: If you want to learn more about signing JARs, you can find useful information here: http://wiki.eclipse.org/JAR_Signing.

After that you will see a dialog asking you to restart Eclipse. Click "Restart Now".

That's it. Now you are ready to start generating RAML APIs from your existing JAXRS Applications!

#Usage

- Select a java class, package or source folder
- Right click to show popup menu.
- "Generate RAML from classes"

![alt tag](http://prntscr.com/3wpqzo)

Configuration

![alt tag](http://prntscr.com/3wqd8s)


Click finish and you RAML API is ready.

##Configuration Options

The plugin supports the following configuration options:

- File name for your RAML file: Name for a generated RAML file
- Folder: Select the folder for your generated RAML files.
- Basic settings tab: Configure the default options for your RAML file:
  - API Title
  - API Version
  - Base Url
  - Protocols selection.
- Response codes tab: Configure the default response codes for the HTTP methods.
- Generate an individual RAML per each Java Class: When checked, a separate RAML file will be generated for each Java class annotated with @Path.
- Sort resources alphabetically: When checked, the resources will be sorted alphabetically. When not, the resources are generated in the same order than the methods are written in the java code.
- Skip resources with no methods: When checked, the tool won't generate a resource entry (in the RAML definition) for those url path segments which are not exposing any HTTP method. This will result in more flat resource tree.
- Generate schemas and examples in a single RAML file: When checked, schemas and examples will be placed in the generated RAML file,
 otherwise these will be placed in separate files and include links will be inserted in the generated RAML file. **Note:** This option is not available if you have selected "Generate an individual RAML per each Java Class" since there is no single RAML file where to include the schemas and examples.
