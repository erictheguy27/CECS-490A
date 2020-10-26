// main.c
// Authors: Eric Nguyen
// Created: October 12,2020
// Description: 
// Test code for VL53L0X sensor. 
// Library used: https://github.com/ZeeLivermorium/VL53L0X_TM4C123G
// Libray author: Zee Livermorium

/* hardware connections
 ST7735
 Backlight (pin 10) connected to +3.3 V
 MISO (pin 9) unconnected/ SCK (pin 8) connected to PA2 (SSI0Clk)
 MOSI (pin 7) connected to PA5 (SSI0Tx)
 TFT_CS (pin 6) connected to PA3 (SSI0Fss)
 CARD_CS (pin 5) unconnected
 Data/Command (pin 4) connected to PA6 (GPIO), high for data, low for command
 RESET (pin 3) connected to PA7 (GPIO)
 VCC (pin 2) connected to +3.3 V
 Gnd (pin 1) connected to ground
 VL53L0X
 VCC (pin 1) connected to +3.3 V
 Gnd (pin 2) connected to ground
 SCL (pin 3) connected to PB2 (I2C0SCL)
 SDA (pin 4) connected to PB3 (I2C0SDA)
 XSHUT (pin 5) unconnected
*/
#include <stdint.h>
#include "PLL.h"
#include "I2C.h"
#include "ST7735.h"
#include "VL53L0X.h"
#include "VL53L0X_DEBUG.h"
#include "tm4c123gh6pm.h" 

int main(void) {
		char OutOfRange = 0;
    PLL_Init(Bus80MHz);                             // bus clock at 80 MHz
    VL53L0X_DEBUG_INIT();
    
    ST7735_InitR(INITR_REDTAB);
    ST7735_SetCursor(0, 0);
    ST7735_FillScreen(ST7735_BLACK);
    
		// VL53L0X initiziation
    if(!VL53L0X_Init(0)) {
        ST7735_OutString("Fail to init VL53L0X");
        delay(1);
        return 0;
    } else {
        ST7735_OutString("VL53L0X Ready~ ");
        ST7735_OutChar('\n');
    }
    if(!VL53L0X_SingleRanging_Init(0)) {
        ST7735_OutString("SRD Mode init failed :(");
        ST7735_OutChar('\n');
        delay(1);
        return 0;
    } else {
        ST7735_OutString("SRD Mode Ready~ ");
        ST7735_OutChar('\n');
    }
    
		// ST7735 display setup
    ST7735_SetCursor(0, 0);
    ST7735_FillScreen(ST7735_BLACK);
    ST7735_OutString("490A TOF TEST\n");
    ST7735_OutString("--------------------\n");
    
    VL53L0X_RangingMeasurementData_t measurement;
    
    // Main loop
    while(1) {
				// take measurement
        VL53L0X_getSingleRangingMeasurement(&measurement, 0);
				// check if in range
        if (measurement.RangeStatus != 4) {
            ST7735_OutString("Distance: ");
            ST7735_OutUDec(measurement.RangeMilliMeter);
            ST7735_OutString(" mm \n");
					  OutOfRange = 0;
        } else {
						// print error only once to avoid lcd flickering 
					  // caused by reprinting error
						if (OutOfRange == 0) {
							ST7735_FillRect(0,20,128,10,0);
							ST7735_OutString("Out of range \n");
							OutOfRange = 1;
						}
        }
        ST7735_SetCursor(0, 2);
    }
}
