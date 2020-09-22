// Flex_Sensor_Trigger_Code.c
// Authors: 
// Created: September 16,2020
// Description: 
// Test code for test sensor. 
// Turns a red LED on and off

#include <stdio.h>
#include <stdint.h>
#include "tm4c123gh6pm.h"
#define COLOR                               (*((volatile unsigned long *)0x40025038))
#define OFF                                         0x00;
#define RED                                         0x02;
#define BLUE                                        0x04;
#define PINK                                        0x06;
#define GREEN                                       0x08;
#define YELLOW                                      0x0A;
#define LIGHTBLUE                                   0x0C;
#define WHITE                                       0x0E;
// function declarations
void PortF_Init(void);
void PortE_Init(void);
void ADC1_Init(void);
	
// const will place these structures in ROM
volatile static uint32_t adcResult = 0;
int ON = 0;

int main(void){
  PortF_Init();
	PortE_Init();
  ADC1_Init();
	COLOR = OFF;
	GPIO_PORTF_DATA_R = OFF;
  while(1){
	  if (adcResult > 3401) {  // Red LED on
			COLOR = RED;
    }
    else if (adcResult < 3400) { // Off
			COLOR = OFF;
    }
	}
}


// PF4 is input
// Make PF2 an output, enable digital I/O, ensure alt. functions off
void PortF_Init(void) {
	SYSCTL_RCGCGPIO_R |= 0x20;	                // activate Port F clock
	while((SYSCTL_PRGPIO_R&0x20)==0){}; 				// allow time for clock to start
	GPIO_PORTF_CR_R = 0x0E;											// make PF4 and PF0 configurable
	GPIO_PORTF_DIR_R |= 0x0E;	                	// PF3,2,1 are output
	GPIO_PORTF_DEN_R |= 0x0E;	                	// PF4-0 are digital
}

void PortE_Init(void) {
	SYSCTL_RCGCGPIO_R |= 0x10; 									// 1) activate port E clock
	while((SYSCTL_RCGCGPIO_R&0x10)==0){}; 			// 2) allow time for clock to start
	GPIO_PORTE_LOCK_R  |= 0x4C4F434B;     			// unlock GPIO port E
	GPIO_PORTE_PCTL_R &= ~0x000000F0; 					// 3) GPIO configure PE1 as GPIO
	GPIO_PORTE_AMSEL_R |= 0x02;      						// 4) disable analog functionality on PE1
	GPIO_PORTE_PUR_R |= 0x02;         					// 5) pullup for PE1
  GPIO_PORTE_DIR_R &= ~0x02;   								// 5) make PE1 input
  GPIO_PORTE_AFSEL_R |= 0x02;								  // 6) enable alt funct on PE1
  GPIO_PORTE_DEN_R &= ~0x02;   								// 7) disable digital I/O on PE1 
}

void ADC1_Init(void){
	SYSCTL_RCGCADC_R |= 0x02;	                  // activate ADC1 clock
	while((SYSCTL_RCGCADC_R&0x02)==0){}; 				// allow time for clock to start
	SYSCTL_RCGCADC_R &= ~0x300;             		// configure for 125K samples/second
	ADC1_SSPRI_R   &= ~0x3000;            			// Highest Priority = 0
	ADC1_ACTSS_R &= ~0x0008;        						// 9) disable sample sequencer 3
  ADC1_EMUX_R |= 0xF000;          						// 
	ADC1_SSMUX3_R |=0x02 ;          						// 11) Ain9 (PE1)
  ADC1_SSCTL3_R |= 0x6;         
	ADC1_IM_R = (1<<3);
	ADC1_ACTSS_R |= 0x0008;         						// 14) enable sample sequencer 3
  ADC1_ISC_R = (1<<3);
  NVIC_EN1_R |=  0x00080000; //Enable Int
}

void ADC1Seq3_Handler(void) {
	adcResult = ADC1_SSFIFO3_R & 0xFFF;  // read data from ADC
	ADC1_ISC_R = (1<<3);
}
