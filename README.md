An open-source web tool for translating UI. Distributed under BSD licence.

Core features are:

* Language files in java property format
* OpenId authentication with flexible access rules. See translation.properties.sample for configuration tips
* Votes and moderation
* Ajax interface

Requirements:
* A Java web server (such as Tomcat or Jetty)
* PostgreSQL 8.3+ server should be installed somewhere in your network
* Apache ANT 1.8+, JDK 1.6+, internet connection are required to build

Installation:

ant clean war

Put the dist/ROOT.war in a webserver's "webapps" directory. You can also rename ROOT.war in order to create a
web context, e.g. translations.war would be located update the /translations URL.

Copy the translation.properties.sample into translation.properties in the current working dir of the web server
(usually parent of the "webapps" directory). Change the settings in the translation.properties and (re)start the
webserver.

Note that you need to restart the server every time you change translation.properties.
