/* Rig Client Driver
UART Integration written by Jason VM Herbert
and Web Integration written by Mike Meaney,
in San Diego, CA (2015)


//6.21.15 Rev. 
	-Identify to server the ID of the RIG 
		-Will require a hard-coded ID in the EEPROM of the Arduino

*/ 

import processing.serial.*;
import http.requests.*;
import java.util.Date;

float derpNumber = 0;
String serverURL = "http://rigley-meaneymiked.rhcloud.com";
String RIG_NAME = "Flannery"; 
String RIG_NAME_Q = "rig="+RIG_NAME;

//For the data
String inBuffer;
long IN = 0;
String elapsedBuffer;


Serial myPort; // Create object from Serial port 
int val; //Data recevied from the Serial port being declared 
void setup(){ 
	println("----The Serial Ports are as follows------");
	println(Serial.list());  // List avaliable serial ports 
	println("Configuring port: " + Serial.list()[2] + " This should start with \"/dev/tty/usbmodem\" on a Mac at least :/ ");
	myPort = new Serial(this,Serial.list()[2], 115200); // List Serial Port 2 as port being used 
	// open port, set baud rate to 115200 
	//How to get data from Server
  	println("----Starting Server Connection----");
  	GetRequest get = new GetRequest(serverURL);
  	get.send(); // program will wait untill the request is completed
  	println("Server Response: " + get.getContent());
	}



void draw(){
	background(0, 155, 155);
	if (myPort.available() >0) {   // If open Serial port is reading data greater than 0 bytes 
			int inByte = myPort.read(); // Read byres in Serial port 
			// println(inByte);
			switch (inByte) {       // Switch statement of Calibration
				case '[' :
					println("CalStart");
					sendToServer(serverURL,"/status","state=Calibrating&"+RIG_NAME_Q);
					break;
				case ']' :
					println("CalEnded"); 
					sendToServer(serverURL, "/status", "state=Waiting&"+RIG_NAME_Q); 
					break;
				case '\0' :
					println("Hand Inserted"); //When Arduino sends "\0" to being counting seconds passed
					sendToServer(serverURL, "/status", "state=Testing&"+RIG_NAME_Q); 
					Date in = new Date();
					IN = in.getTime();
					elapsedBuffer = "";
					break;
				case '\t':
					println("Hand Removed"); // When Arduino send "\t" stop counting, and 
					if (myPort.available()>0) { //
							elapsedBuffer = myPort.readString();
							println("The Fucking buffer:" +trim(elapsedBuffer));	
							sendToServer(serverURL,"/status", "state=complete");
							
							Date out = new Date();
							//Send the data to the server
							String theData = RIG_NAME_Q+"&durration="+elapsedBuffer+"&inTime="+IN+"&outTime="+out.getTime();
							//println("The fucking data query:" + theData);
							sendToServer(serverURL, "/data", theData);
							delay(500);
							sendToServer(serverURL, "/status", RIG_NAME_Q+"&state=Done");
							delay(2000);
							sendToServer(serverURL, "/status", RIG_NAME_Q+"&state=Waiting"); 
							break;
					}
			}

		}
	}

void sendToServer(String url,String route, String query){
	GetRequest theRequest = new GetRequest(url+route+"?"+query);
	theRequest.send();
	println("--------------------------------------");
	println(theRequest.getContent());
//	println(theRequest.getHeader("Content-Length"));

	println("--------------------------------------");
	println(query + " sent to server");
	//delay(1000);
}

