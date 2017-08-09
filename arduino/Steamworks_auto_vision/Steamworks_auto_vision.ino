//
// begin license header
//
// This file is part of Pixy CMUcam5 or "Pixy" for short
//
// All Pixy source code is provided under the terms of the
// GNU General Public License v2 (http://www.gnu.org/licenses/gpl-2.0.html).
// Those wishing to use Pixy source code, software and/or
// technologies under different licensing terms should contact us at
// cmucam@cs.cmu.edu. Such licensing terms are available for
// all portions of the Pixy codebase presented here.
//
// end license header
//
// This sketch is a good place to start if you're just getting started with 
// Pixy and Arduino.  This program simply prints the detected object blocks 
// (including color codes) through the serial console.  It uses the Arduino's 
// ICSP port.  For more information go here:
//
// http://cmucam.org/projects/cmucam5/wiki/Hooking_up_Pixy_to_a_Microcontroller_(like_an_Arduino)
//
// It prints the detected blocks once per second because printing all of the 
// blocks for all 50 frames per second would overwhelm the Arduino's serial port.
//
   
#include <SPI.h>  
#include <Pixy.h>

// This is the main Pixy object 
Pixy pixy;

void setup()
{
  Serial1.begin(9600,SERIAL_8N1);
  Serial1.print("Starting...\n");

  pixy.init();
}

void loop()
{ 
  static int i = 0;
  int j;
  uint16_t blocks;
  char buf[32]; 

  // set to 1 to start, it will get set to 0 if we don't have any blocks after the first time the arduino sends 0's
  static boolean printNoBlocks = 1;
  static int gotBlocksFrame = 1;
  static int lastPrintFrame = 0;
  static int printInterval = 25;
  
  // grab blocks!
  blocks = pixy.getBlocks();
  
    i++;

    if (blocks) {

    // do this (print) every 10 frames because printing every
    // frame would bog down the Arduino
    if (i % printInterval == 0)
    {

       gotBlocksFrame = i;
       printNoBlocks = 2;
                          
        // only print 1 or 2 blocks
        if (blocks == 1) {
          sprintf(buf,"B,%d,%d,%d,%d,%d,0,0,0,0,E\n",blocks,pixy.blocks[0].x,pixy.blocks[0].y,pixy.blocks[0].width,pixy.blocks[0].height);
           Serial1.print(buf);
        } else if (blocks >= 2) {
          sprintf(buf,"B,%d,%d,%d,%d,%d,%d,%d,%d,%d,E\n",blocks,
          pixy.blocks[0].x,pixy.blocks[0].y,pixy.blocks[0].width,pixy.blocks[0].height,
          pixy.blocks[1].x,pixy.blocks[1].y,pixy.blocks[1].width,pixy.blocks[1].height);       
           Serial1.print(buf); 
        }
    }
    } else {
    // print if we have no blocks, but only at least printInterval frames after we saw blocks
     //if ((printNoBlocks >= 1) && ((i - gotBlocksFrame) > printInterval*1600)) {
     if ((i - lastPrintFrame) > printInterval) {
         sprintf(buf,"B,0,0,0,0,0,0,0,0,0,N\n");
         Serial1.print(buf);
         lastPrintFrame = i;
         //printNoBlocks -= 1;
         //delay(50);
     }
  }
  delay(20);  
} 


