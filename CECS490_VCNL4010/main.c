//*************************************************************************************
// Naomi George (017207136) 
// CECS 490A

//*************************************************************************************
#include "tm4c123gh6pm.h"
#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include <stdint.h>
#include "i2c_prox.h"
#include "__proximity_driver.h"


#define LEDS                    (*(volatile unsigned int *)0x400253FC)
#define RED                     0x02
#define BLUE                    0x04
#define GREEN                    0x08

  
unsigned int  ProxiValue=0;
unsigned char ID=0;
void SystemInit(void){
}
	
int main(void)
{
	I2C_Init();
	SYSCTL_RCGCGPIO_R |= 0x20;   // enable clock to GPIOF
	GPIO_PORTF_LOCK_R = 0x4C4F434B;   // unlockGPIOCR register
	GPIO_PORTF_CR_R = 0x01; // Enable GPIOPUR register enable to commit
	GPIO_PORTF_PUR_R |= 0x10;        // Enable Pull Up resistor PF4
	GPIO_PORTF_DIR_R |= 0x0E;  //set PF1 as an output and PF4 as an input pin
	GPIO_PORTF_DEN_R |= 0x1E;  // Enable PF1 and PF4 as a digital GPIO pins 
  ReadID (&ID); 
	SetCurrent (20);
	
	while(1){
		ReadProxiOnDemand (&ProxiValue);
		if (ProxiValue < 3000){
			 LEDS = RED;
				}
		else if (ProxiValue < 5000)
				LEDS = GREEN;
		else
				LEDS = BLUE;
		};
}
	

