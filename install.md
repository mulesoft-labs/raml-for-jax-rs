# Installation instructions:

You can install the JAX RS to RAML plugin for Eclipse using the Software Update feature of Eclipse/Mulestudio.

To do it you can may just use the update site URL below. 
http://jaxrstoraml.appspot.com/site/

Detailed instructions:

Start Eclipse or Mulestudio, then select Help > Install New Software... 
In the dialog that appears, enter the update site URL into the Work with text box:

http://jaxrstoraml.appspot.com/site/

And press the enter key.

The required component is placed in RAML Tools category . Select the checkbox next to RAML Tools. Click Next.
Click Next again. Read the license agreements and then select I accept the terms of the license agreements. Click Finish.

Then you will see security warning saying that you are installing software that contains unsigned content. 
This warning means that plugin is unsigned and eclipse can not verify identity of plugin authors because some
of the jars are not signed.


Click 'Ok'.

After that you will see dialog asking if you would like to restart Eclipse. Click Restart Now.

Now you are ready to generate RAML apis for your JAX RS Applications! 

#Usage

Select java class, or package or source folder, then do a right click to show popup menu. You should see something like this: http://prntscr.com/3wpqzo

Then select "Generate RAML from classes"

Now you may configure some options:

http://prntscr.com/3wqd8s

Click finish and you RAML api is here...

##Configuration Options

Eclipse plugin supports following configuration options

 * Please type the file name for your RAML file - this allows you to type a name for a generated RAML file

 * Folder - this setting allows you to select folder for generated RAML files.

 * Basic settings - section allows you to configure defaults for your RAML file. At the moment following defaults are
   supported:

  * API Title
  * API Version
  * Base Url 
  * Protocols selection.
 
 * Response codes tab - this tab allows to configure default response codes for various HTTP methods

 * Generate an individual RAML per each Java Class - when this option is turned on, separate RAML file will be generated for each java class
   with associated @Path annotation
   
 * Sort resources alphabetically - this options allows to control order of resources in a RAML file. If it is turned off resources are generated
   in the same sequence as a methods in java code. If it is turned on - resources will be sorted alphabetically
   
 * Skip resources with no methods - If this options is turned on the tool will not generate resources for url path segments which did not expose any HTTP methods. 
   This will result in more flat resource tree. 
 
 * Generate schemas and examples in a signle RAML file - if this option is turned on schemas and examples will be placed inside of generated RAML file, 
 otherwise they will be placed in separate files and include links will be inserted in a generated RAML file.







