// main.c
// Authors: Eric Nguyen
// Created: October 12,2020
// Description: 

// Hardware connections
// Bluetooth connections
// Tiva -------- HC05 Bluetooth Module
// PD6   ------  TXD
// PC7   ------  RXD
// 3.3v  ------  Vc
// gnd   ------  gnd
// ADC1 connections
// Tiva -------- Flex sensors
// PE0   ------  flex sensor 1
// PE1   ------  flex sensor 2
// PE2   ------  flex sensor 3
// PE3   ------  flex sensor 4
// PE4   ------  flex sensor 5

// 1. Pre-processor Directives Section
// Constant declarations to access port registers using 
// symbolic names instead of addresses

#include <stdint.h>
#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include "PLL.h"
#include "UART.h"
#include "SysTick.h"
#include "tm4c123gh6pm.h"

// 2. Declarations Section
#define INTERNALLED     (*((volatile unsigned long *)0x40025038))
#define EXTERNALLED     (*((volatile unsigned long *)0x4000723C))
#define OFF                                         0x00;
#define RED                                         0x02;
#define BLUE                                        0x04;
#define PINK                                        0x06;
#define GREEN                                       0x08;
#define YELLOW                                      0x0A;
#define LIGHTBLUE                                   0x0C;
#define WHITE                                       0x0E;
//   Global Variables
uint8_t i = 0;
uint32_t adcResult[5];
char rxChar[6];//for PC application,uncomment thi

//   Function Prototypes
void EnableInterrupts(void);
void WaitForInterrupt(void);  // low power mode

void PortF_Init(void) {
	SYSCTL_RCGCGPIO_R |= 0x20;	                // activate Port F clock
	while((SYSCTL_PRGPIO_R&0x20)==0){}; 				// allow time for clock to start
	GPIO_PORTF_CR_R |= 0x0E;											
	GPIO_PORTF_DIR_R |= 0x0E;	                	// PF3,2,1 are output
	GPIO_PORTF_DEN_R |= 0x0E;	                	// PF4-0 are digital
}

void PortE_Init(void) {
	SYSCTL_RCGCGPIO_R |= 0x10; 									// activate port E clock
	while((SYSCTL_RCGCGPIO_R&0x10)==0){}; 			// allow time for clock to start	
  GPIO_PORTE_DIR_R &= ~0x1F;                // make PE4 PE5 input
  GPIO_PORTE_AFSEL_R |= 0x1F;                 // enable alternate function on PE4 PE5
  GPIO_PORTE_DEN_R &= ~0x1F;                  // disable digital I/O on PE4 PE5
  GPIO_PORTE_PCTL_R = GPIO_PORTE_PCTL_R&0xFFF00000;
  GPIO_PORTE_AMSEL_R |= 0x1F;                 // enable analog functionality on PE4 PE5
}

void PortD_Init(void) {
  SYSCTL_RCGCGPIO_R |= 0x08;                  // activate port D
  while((SYSCTL_PRGPIO_R&0x08)==0){}; 				// allow time for clock to start
	GPIO_PORTD_LOCK_R  |= 0x4C4F434B;
  GPIO_PORTD_CR_R |= 0x80;		
  GPIO_PORTD_AMSEL_R &= ~0x8F;                // disable analog functionality on PD7,3-0
  GPIO_PORTD_PCTL_R &= ~0xF000FFFF;           // GPIO configure PD7,3-0 as GPIO
  GPIO_PORTD_DIR_R |= 0x8F;                   // make PD7,3-0 out
  GPIO_PORTD_AFSEL_R &= ~0x8F;                // disable alt funct on PD7,3-0
  GPIO_PORTD_DEN_R |= 0x8F;                   // enable digital I/O on PD7,3-0 

}
	
void ADC_EN(void){                        
  volatile uint32_t delay;                         
  SYSCTL_RCGCADC_R |= 0x00000001; // 1) activate ADC0
  delay = SYSCTL_RCGCGPIO_R;      // 2) allow time for clock to stabilize
  delay = SYSCTL_RCGCGPIO_R;
  ADC0_PC_R &= ~0xF;              // 8) clear max sample rate field
  ADC0_PC_R |= 0x1;               //    configure for 125K samples/sec
  ADC0_SSPRI_R = 0x3210;          // 9) Sequencer 3 is lowest priority
  ADC0_ACTSS_R &= ~0x0001;        // 10) disable sample sequencer 0
  ADC0_EMUX_R &= ~0x000F;         // 11) seq0 is software trigger
  ADC0_SSMUX0_R = 0x00090123;     // 12) set channels for SS0
  ADC0_SSCTL0_R = 0x00060000;     
  ADC0_IM_R &= ~0x0001;           // 14) disable SS0 interrupts
  ADC0_ACTSS_R |= 0x0001;         // 15) enable sample sequencer 0
}

void UART2_Handler()//this interrupt routine is for receiving data from bluetooth
{
  rxChar[i] = UART2_DR_R;
  i++;
	if(strcmp(rxChar,"0")==0){
		EXTERNALLED ^= 0x01;
		UART3_OutString("0");			
	}
	else if(strcmp(rxChar,"1")==0){
		EXTERNALLED ^= 0x02;
		UART3_OutString("1");
	}
	else if(strcmp(rxChar,"2")==0){
		EXTERNALLED ^= 0x04;
		UART3_OutString("2");
	}
	else if(strcmp(rxChar,"3")==0){
		EXTERNALLED ^= 0x08;
		UART3_OutString("3");	
	}
	else if(strcmp(rxChar,"4")==0){
		EXTERNALLED ^= 0xF0;
		UART3_OutString("4");		
	}
	i=0;
  UART2_ICR_R=UART_ICR_RXIC;//clear interrupt
}

void microsecond_delay(int time){
	int delay;
	SYSCTL_RCGCTIMER_R |= 1;	// enable Timer Block 0
	while((SYSCTL_RCGCTIMER_R&0x01)==0){}; 			// 2) allow time for clock to start
	TIMER0_CTL_R = 0;		// disable Timer before init
	TIMER0_CFG_R = 0x04;		// 16-bit mode
	TIMER0_TAMR_R = 0x01;	// one shot
	TIMER0_TAILR_R = 16-1;	// interval load value register
	TIMER0_ICR_R = 0x1;		// clear Timer0A timeout flag
	TIMER0_CTL_R |= 0x01;		// enable timer0A
	
	for (delay = 0; delay < time; delay++){
    TIMER0_ICR_R = 0x1;
	}

}

//------------ADC_Read------------
// Busy-wait Analog to digital conversion
// Input: none
// Output: two 12-bit result of ADC conversions
// Samples ADC8 and ADC9 
// 125k max sampling
// software trigger, busy-wait sampling
// data returned by reference
// data[0] is ADC8 (PE5) 0 to 4095
// data[1] is ADC9 (PE4) 0 to 4095
void ADC_Read(uint32_t data[5]){ 
  ADC0_PSSI_R = 0x0001;            // 1) initiate SS2
  while((ADC0_RIS_R&0x01)==0){};   // 2) wait for conversion done
  data[0] = ADC0_SSFIFO0_R&0xFFF;  // 3A) read first result
  data[1] = ADC0_SSFIFO0_R&0xFFF;  // 3B) read second result
	data[2] = ADC0_SSFIFO0_R&0xFFF;  // 3A) read third result
  data[3] = ADC0_SSFIFO0_R&0xFFF;  // 3B) read fourth result	
	data[4] = ADC0_SSFIFO0_R&0xFFF;  // 3B) read fifth result	
  ADC0_ISC_R = 0x0001;             // 4) acknowledge completion
}

// MAIN: Mandatory for a C Program to be executable
int main(void){ 
	char flexsensors[10];
	PLL_Init();	//80Mhz
	UART2_Init();	//d6
	UART3_Init();	//c7
  PortF_Init();
	PortE_Init();
	PortD_Init();	
	ADC_EN();
  INTERNALLED = OFF;
	UART3_OutString("\n\r");
	flexsensors[5] = '0';
	flexsensors[6] = '0';
	flexsensors[7] = '0';
	flexsensors[8] = '0';
	flexsensors[9] = '0';
  while(1){	
    ADC_Read(adcResult);
	  if (adcResult[0] > 2048) {  // Red LED on
			INTERNALLED = RED;
			flexsensors[0] = '1';
			
    }
		else {
			flexsensors[0] = '0';
		}
    if (adcResult[1] > 2048) { // Off
			INTERNALLED = BLUE;
			flexsensors[1] = '1';
    }
		else {
			flexsensors[1] = '0';
		}
		if (adcResult[2] > 2048) {  // Red LED on
			INTERNALLED = GREEN;
			flexsensors[2] = '1';
    }	
		else {
			flexsensors[2] = '0';
		}
    if (adcResult[3] > 2048) { // Off
			INTERNALLED = PINK;
			flexsensors[3] = '1';
    }
		else {
			flexsensors[3] = '0';
		}
		if (adcResult[4] > 2048) { // Off
			INTERNALLED = YELLOW;
			flexsensors[4] = '1';
    }
		else {
			flexsensors[4] = '0';
		}
		UART3_OutString(flexsensors);
		UART3_OutString(" ");
		microsecond_delay(500000);
		
  }
}

