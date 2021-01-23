OpenHtmlToPdf Server
====================
A thin Spring Boot server "wrapper" around [Open HTML to PDF](https://github.com/danfickle/openhtmltopdf). Allows you to create PDF documents via REST (POST) requests. It also employs a templating engine to enable the creation of dynamic PDFs (template parameters are passed as JSON in the POST request). Currenty only Handlebars (via [Handlebars.java](https://github.com/jknack/handlebars.java)) is supported, but it is easily possible to add further templating engines (see Extensibility). The server itself barely contains any code but mostly combines the following projects:

* [Open HTML to PDF](https://github.com/danfickle/openhtmltopdf)
* [Handlebars.java](https://github.com/jknack/handlebars.java)
* [Spring Boot](https://spring.io)

### Requirements
* Only Java is required to run the server

### Setup
* Download the Zip file from the latest release, extract it and place the Jar file in any directory
* Create a subdirectory named "documents" (this can be customized, see Configuration)
* Place an .html or .hbs (Handlebars) files in that directory
* Start the server via "java -jar openhtmltopdf-server-X.X.X.jar"
* Send a POST request (e.g. via Postman*) to ``http://localhost:8080/pdf/<name of a file in the documents folder>``

You should receive a PDF with the contents of the file. When you request a Handlebars file you have to supply the template parameters as JSON in the POST request. You can also create any subfolders inside the "documents" directory which you then, of course, have to supply in the request. Suppose you have created a subfolder named "Handlebars" in the "documents" folder. Inside that folder you have a file name "test.bhs". The request would have to go to ``http://localhost:8080/pdf/Handlebars/test.hbs``. Which folder structure you use is completely up to you.

\* if you don't have Postman see Template development

### Configuration
As a standard Spring Boot server is used, you can create a file named "application.properties" in the same folder as the Jar file. There you can, for example, change the port by adding the following property:
``server.port=<other port>``
The server itself only allows to configure three custom properties. To change the root folder to something other than "documents", add the property ``root.dir=<name of the new root folder>``. Apart from that you can customize which producer name should be set in the generated PDF documents. This value defaults to "openhtmltopdf.com". You can change it by adding the property ``pdf.producer=<different producer>``. Lastly you can set ``handlebars.template.hot-reload=<true|false>`` (default is false) which causes changes to Handlebars templates to be directly picked up by the server. This has a slight performance penalty but is very useful when developing a template.

### Template development
Developing templates can be a tedious process because you always have to check how a change in the HTML looks in the actual PDF. For this reason this project contains the file 'template-dev.html' which is automatically served if you start the server in dev mode (pass ``-Dspring.profiles.active=dev`` as additional command line parameter). You can then open the file in your browser if you go to http://localhost:8080/template-dev.html (by default). You should see a webpage with a textfield at the top where you can enter the URL to your template. Below is a text area were you can put the template parameters (e.g. JSON). When you click the 'Generate' button, a preview of the PDF loads in the bottom part of the page. You can also trigger that button with the shortcut Alt + r or Alt + Shift + r depending on your browser (this makes use of the HTML accesskey property). The shortcut does not work when the PDF preview is focussed.

### Extensibility
In the sources you will find the subpackage "document". In there are currently two strategies to create the documents: "DirectHtmlDocumentStrategy" and "HandlebarsHtmlDocumentStrategy". Both implement the interface "HtmlDocumentStrategy". To add another templating engine, add a new class implementing that interface. There is only one method to implement, which takes the path to the template file and the template parameters (a simple string which you have to parse yourself if necessary) as arguments. It returns a W3C document which you can easily get via jsoup (have a look in the HandlebarsHtmlDocumentStrategy class). The only other thing you must to, so that the templating engine is picked up, is to add a method to your class that is annotated with "@PostConstruct" and registers the engine for a file extension. You have to inject the "HtmlDocumentStrategyFactory" for that and call the "registerStrategy" method. Make sure you don't overwrite existing file extensions (currently .html and .hbs) because one file extension can only have one templating engine associated.
