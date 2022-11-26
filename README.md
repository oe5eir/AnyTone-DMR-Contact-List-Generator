# Contact List Generator for AnyTone DMR Radios

This application creates the DMR contact list based on the official data from https://radioid.net/database for AnyTone radios which can be imported to the radio via the CPS.

## Requirements
This program needs the Java Runtime Environment 8 to run.
If you want to change/recompile the program then using the IntelliJ IDE is recommended.

## Default Settings
By default the application generates a contact list with all Austrian and German callsigns with repeaters and club stations excluded.
To change this behaviour edit the SEARCH_STRING as documented in https://radioid.net/database/api and the SKIP_CALLSIGN_STARTS_WITH list according to your needs.

## Run the application
Open a shell on your machine and navigate to the folder containing the downloaded or self-created JAR file.
Then run `java -jar anytone-dmr-contactlist-generator.jar [OUTPUT FILE]`.
Example: `java -jar anytone-dmr-contactlist-generator.jar C:\Users\OE5EIR\Documents\contacts.csv`
