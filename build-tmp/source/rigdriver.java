import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 

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


Serial myPort; // Create object from Serial port 
int val; //Data recevied from the Serial port being declared 
public void setup(){ 
	println(Serial.list());  // List avaliable serial ports 
	myPort = new Serial(this,Serial.list()[2], 115200); // List Serial Port 2 as port being used 
	// open port, set baud rate to 115200 
	}
public void draw(){
	if (myPort.available() >0) {   // If open Serial port is reading data greater than 0 bytes 
			int inByte = myPort.read(); // Read byres in Serial port 
			// println(inByte);
			switch (inByte) {       // Switch statement of Calibration
				case '[' :
					println("CalStart");
					break;
				case ']' :
					println("CalEnded");  
					break;
				case '\0' :
					println("Hand Inserted"); //When Arduino sends "\0" to being counting seconds passed
					println(inByte);
					break;
				case '\t':
					println("Hand Removed"); // When Arduino send "\t" stop counting, and 
					if (myPort.available()>0) { //
							String elapsedBuffer = myPort.readString();
							println(elapsedBuffer);	
							myServer.write (elapsedBuffer);
							break;
					}

				
			}

		}



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
