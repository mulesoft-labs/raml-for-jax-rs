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

Select java class, or package or source folder, then do a right click to show popup menu. You should see something like this: http://prntscr.com/3h39zr

Then select "Generate RAML from classes"

Now you may configure some options:

http://prntscr.com/3h3b7g

Click finish and you RAML api is here...

