// ultrasonic sensor test code.c
// Authors: 
// Created: September 22,2020
// Description: 
// Test code for ultra-sonic sensor. 

#include <stdio.h>
#include <stdint.h>
#include "tm4c123gh6pm.h"
#include "ST7735.h"
#define COLOR                               (*((volatile unsigned long *)0x40025038))
#define OFF                                         0x00;
#define RED                                         0x02;
#define BLUE                                        0x04;
#define PINK                                        0x06;
#define GREEN                                       0x08;
#define YELLOW                                      0x0A;
#define LIGHTBLUE                                   0x0C;
#define WHITE                                       0x0E;

#define TRIGGER 		(*((volatile unsigned long *)0x40024004)) // PE0(Output)
#define ECHO	(*((volatile unsigned long *)0x40024008)) // PE1(Input)
#define MAX_TIME 7500

// function declarations
void PortF_Init(void);
void PortE_Init(void);
void microsecond_delay(int);
void measure_dist(void);
	
// const will place these structures in ROM
float distance, conv_dist = 0;
int counter = 0;
char conv_done = 0;
char preadd_val = 0;

int main(void){
	
  PortF_Init();
	PortE_Init();
	//PortE_Interrupt_Init();
	ST7735_InitR(INITR_REDTAB);
	distance = 0;
	COLOR = OFF;
	TRIGGER = 0;
	ST7735_FillScreen(0);
  ST7735_SetCursor(0, 0);
  ST7735_OutString("490A Senior Projecct");
  ST7735_SetCursor(0, 1);
  ST7735_OutString("HC-SR04 Ultrasonic");
	ST7735_SetCursor(0, 2);
  ST7735_OutString("Distance: ");
	ST7735_SetCursor(0, 3);
	ST7735_OutString("0 cm");
  while(1){
		measure_dist();
		microsecond_delay(500000);
	}
}


// PF4 is input
// Make PF2 an output, enable digital I/O, ensure alt. functions off
void PortF_Init(void){ volatile unsigned long delay;
  SYSCTL_RCGC2_R |= 0x00000020;     // 1) F clock
  delay = SYSCTL_RCGC2_R;           // delay
  GPIO_PORTF_LOCK_R = 0x4C4F434B;   // 2) unlock PortF
  GPIO_PORTF_CR_R |= 0x1F;           // allow changes to PF4-0
  GPIO_PORTF_AMSEL_R &= ~0x1F;        // 3) disable analog function
  GPIO_PORTF_PCTL_R &= ~0x000FFFFF;   // 4) GPIO clear bit PCTL
  GPIO_PORTF_DIR_R |= 0x0E;          // 5) PF4,PF0 input, PF3,PF2,PF1 output
  GPIO_PORTF_AFSEL_R &= ~0x1F;        // 6) no alternate function
  GPIO_PORTF_PUR_R |= 0x11;          // enable pullup resistors on PF0 PF4
  GPIO_PORTF_DEN_R |= 0x1F;          // 7) enable digital pins PF4-PF0

  GPIO_PORTF_IS_R &= ~0x11;//PF4 PF0 is edge sensitive
  GPIO_PORTF_IBE_R &= ~0x11;//Not both edges
  GPIO_PORTF_IEV_R &= ~0x11;//Falling edge event
  GPIO_PORTF_ICR_R = 0x11;//clear flag 4 & 0
  GPIO_PORTF_IM_R  |= 0x11;//arm interrupt PF4 PF0
  NVIC_PRI7_R = (NVIC_PRI7_R & 0xFF00FFFF) | 0x00A00000;//priority 5
  NVIC_EN0_R |= 0x40000000;//Enable interrupt 30 in NVIC
}

void GPIOPortF_Handler(void){
    if ((GPIO_PORTF_RIS_R&0x01) == 0x01){//switch 2
			GPIO_PORTF_ICR_R = 0x01;// acknowledge flag0
			COLOR = RED;
			distance++;
    }
    else if ((GPIO_PORTF_RIS_R&0x10) == 0x10){//switch 1
      GPIO_PORTF_ICR_R = 0x10;      // acknowledge flag
			COLOR = BLUE;
      distance--;
    }
}

void PortE_Init(void) {
	SYSCTL_RCGCGPIO_R |= 0x10; 									// 1) activate port E clock
	while((SYSCTL_RCGCGPIO_R&0x10)==0){}; 			// 2) allow time for clock to start
	GPIO_PORTE_PCTL_R &= ~0x000000FF; 					// 3) GPIO configure PE0-1 as GPIO
	GPIO_PORTE_AMSEL_R &= ~0x03;      				  // 4) disable analog functionality on PE1
	GPIO_PORTE_DIR_R |= 0x01;   								// 5) make PE0 output
  GPIO_PORTE_DIR_R &= ~0x02;   								// 5) make PE1 input
	GPIO_PORTE_PUR_R |= 0x02;         					// 5) pullup for PE1
  GPIO_PORTE_AFSEL_R &= ~0x03;								// 6) disable alt funct on PE0-1
  GPIO_PORTE_DEN_R |= 0x03;   								// 7) enable digital I/O on PE0-1 
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

void measure_dist(void){ 
	TRIGGER = 0;
	microsecond_delay(10);
	TRIGGER = 1;
	microsecond_delay(10);
	TRIGGER = 0;
	counter =0;
	while(ECHO==0) {
	distance = 0;
	}
	while((ECHO !=0) &(counter < MAX_TIME)) { 
		distance++; 
		microsecond_delay(1);
  } 
	if (preadd_val != distance) {
		ST7735_SetCursor(0, 3);
		ST7735_FillRect(0,30,128,10,0);
		ST7735_OutUDec(distance);
		ST7735_OutString(" mm");
	}
	
	preadd_val = distance;
} 
