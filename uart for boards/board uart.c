// board uart.c
// Authors: Eric Nguyen
// Created: November 13,2020
// Description: 
// Test code for board to board UART. 
// Uses on board push buttons to send a 1 or 0 
// to control LEDs on the other board

/* hardware connections
 uart connections
 B0 UART1 Rx
 B1 UART1 Tx
*/
#include <stdio.h>
#include <stdint.h>
#include "tm4c123gh6pm.h"

#define LIGHT                               (*((volatile unsigned long *)0x40025038))
#define OFF                                         0x00;
#define RED                                         0x02;
#define BLUE                                        0x04;
#define PINK                                        0x06;
#define GREEN                                       0x08;
#define YELLOW                                      0x0A;
#define LIGHTBLUE                                   0x0C;
#define WHITE                                       0x0E;

// function declarations
void UART1_Init(void);
void UART1_SendString( const char * );
void UART1_TX( char c );
void PortF_Init(void);
void PortF_Interrupt_Init(void);
void GPIOPortF_Handler(void);
void chaser(void);
	
// const will place these structures in ROM
int8_t rx_data_byte, rx_data_flag;

int main(){
	UART1_Init();
	PortF_Init();
	PortF_Interrupt_Init();
	LIGHT = OFF;
	while(1){
		if( rx_data_flag ){	
			switch (rx_data_byte){
				// echo back received data
				case 0: LIGHT = RED; break;
				case 1: LIGHT = BLUE; break;
				default: LIGHT = GREEN; break; 
			}
			rx_data_flag = 0;
		}
	}
}

void UART1_Handler(void){
	volatile int32_t readback;
	if(UART1_MIS_R & 0x0010){
		rx_data_byte = UART1_DR_R;
		rx_data_flag = 1;
		UART1_ICR_R = 0x0010;
		readback = UART1_ICR_R;
	}
	else{
		UART1_ICR_R = UART1_MIS_R;	// clear all interrutp flags
		readback = UART0_ICR_R;			// force flags clear
	}	
}

void UART1_SendString( const char *string ){
	int n;
	for (n = 0; string[n] != 0; n++) {
		UART1_TX(string[n]);
	}
}

void UART1_TX( char c ){
	while( (UART1_FR_R & 0x20 ) != 0 );
	UART1_DR_R = c;
}

void UART1_Init(void) {
	volatile uint32_t delay;
	SYSCTL_RCGCGPIO_R |= 0x02;		// enable clock to PORTB
	delay = SYSCTL_RCGCGPIO_R;
	GPIO_PORTB_DEN_R = 0x03;			// PB1,0 are digital
	GPIO_PORTB_AFSEL_R = 0x03;		// PB1,0 alternate function enable
	GPIO_PORTB_PCTL_R = 0x00000011;	// PB1,0 configured for UARTf
	SYSCTL_RCGCUART_R |= 0x02;		// enable clock to UART1
	delay = SYSCTL_RCGCGPIO_R;
	UART1_CTL_R = 0;							// disable UART0
	UART1_IBRD_R = 104;						// 9600 baud integer portion
	UART1_FBRD_R = 11;						// 9600 baud fraction portion
	UART1_CC_R = 0;								// UART0 timed using System clock
	UART1_LCRH_R = 0x60;					// 8-bit, no parity, 1-stop, no FIFO
	UART1_IM_R |= 0x0010;					// enable TX, RX interrupt
	UART1_CTL_R = 0x301;					// enable UART0, TX, RX
	NVIC_PRI1_R |= 3 << 21;				// set interrupt priority to 3
	NVIC_EN0_R |= 0x00000040;			// enable IRQ5 for UART0
	delay = NVIC_EN0_R; 						//Necessary Time Delay
}
void PortF_Interrupt_Init(void){
	GPIO_PORTF_IS_R &= ~0x11;		// PF4,0 are edge sensitive
	GPIO_PORTF_IBE_R &= ~0x11;	// PF4,0 are not both edge sensitive
	GPIO_PORTF_IEV_R &= ~0x11;	// PF4,0 are negedge triggered
	GPIO_PORTF_ICR_R = 0x11;		// clear flag on PF4,0
	GPIO_PORTF_IM_R |= 0x11;		// interrupt on PF4,0
	NVIC_PRI7_R = ( NVIC_PRI7_R & 0xFF00FFFF ) | 0x00A00000;	// Set interrupt priority to 5
	NVIC_EN0_R = 1 << 30;	      // enable IRQ30
}

 void PortF_Init(void) {
	volatile uint32_t x;
	SYSCTL_RCGCGPIO_R |= 0x20;	      // activate Port F clock
	x = SYSCTL_RCGCGPIO_R;
	GPIO_PORTF_LOCK_R  = 0x4C4F434B;	// unlock commit register
	GPIO_PORTF_CR_R = 0x1F;						// make PF4 and PF0 configurable
	GPIO_PORTF_DIR_R &= ~0x11;	      // PF4,0 are input
	GPIO_PORTF_DIR_R |= 0x0E;		      // PF3,2,1 are output
	GPIO_PORTF_DEN_R |= 0x1F;		      // PF4-0 are digital
	GPIO_PORTF_PUR_R |= 0x11;		      // enable pull up for PF4,0
}
 
void GPIOPortF_Handler(void){
	int temp = 0;
	while (temp < 1000000){temp += 1;}  //Hard coded delay removes any bounces
	if((GPIO_PORTF_RIS_R&0x01) == 0x01){ 
		GPIO_PORTF_ICR_R = 0x01;
		UART1_TX(0);
	}
	else if((GPIO_PORTF_RIS_R&0x10) == 0x10){
		GPIO_PORTF_ICR_R = 0x10;
    UART1_TX(1);
	}
} 
