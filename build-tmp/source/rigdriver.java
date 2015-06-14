import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 
import http.requests.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class rigDriver extends PApplet {

/* Rig Client Driver
UART Integration written by Jason VM Herbert
and Web Integration written by Mike Meaney,
in San Diego, CA (2015)

*/ 




float derpNumber = 0;
String serverURL = "http://localhost:3000";

//For the data
String inBuffer;
String outBuffer;
String elapsedBuffer;


Serial myPort; // Create object from Serial port 
int val; //Data recevied from the Serial port being declared 
public void setup(){ 
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



public void draw(){
	if (myPort.available() >0) {   // If open Serial port is reading data greater than 0 bytes 
			int inByte = myPort.read(); // Read byres in Serial port 
			// println(inByte);
			switch (inByte) {       // Switch statement of Calibration
				case '[' :
					println("CalStart");
					sendToServer(serverURL,"/status","state=calibrating");
					break;
				case ']' :
					println("CalEnded"); 
					sendToServer(serverURL, "/status", "state=waiting"); 
					break;
				case '\0' :
					println("Hand Inserted"); //When Arduino sends "\0" to being counting seconds passed
					sendToServer(serverURL, "/status", "state=testing"); 
					if (myPort.available()>0) { //
							inBuffer = myPort.readString();
							inBuffer = trim(inBuffer); //remove white space
							println("In buffer is: " + inBuffer);	
							break;
					}

					break;
				case '\t':
					println("Hand Removed"); // When Arduino send "\t" stop counting, and 
					if (myPort.available()>0) { //
							elapsedBuffer = myPort.readString();
							println(elapsedBuffer);	
							sendToServer(serverURL,"/status", "state=complete");
							
							//Send the data to the server
							String theData = "rig=Ernest&durration="+elapsedBuffer+"&in="+inBuffer;
							sendToServer(serverURL, "/data", theData);
							break;
					}
	
			}

		}
	}

public void sendToServer(String url,String route, String query){
	GetRequest theRequest = new GetRequest(url+route+"?"+query);
	theRequest.send();
	println(query + " sent to server");
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "rigDriver" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
