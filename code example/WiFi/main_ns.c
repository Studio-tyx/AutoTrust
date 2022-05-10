#include "TZ_GPIO_ns.h"
#include "TZ_UART_ns.h"
#include "TZ_ns.h"
extern void SYS_Init(void);
int main()
{
	
	int GPIO_security[8]={0,0,0,1,0,0,0,0};	// set PD to be secure
	TZ_GPIO_set_secure(GPIO_security);
	TZ_GPIO_set_mode(0,BIT11,GPIO_MODE_OUTPUT);
	TZ_GPIO_set_mode(3,BIT6|BIT7,GPIO_MODE_OUTPUT);
		
	int UART_security[6]={1,0,0,1,0,0};		// set UART0 & UART3 to be secure
	TZ_UART_set_secure(UART_security);
	
    TZ_UART_init(0,115200);
	TZ_UART_init(3,115200);
			
	TZ_GPIO_write(0,11,0);	// green LED
	TZ_GPIO_write(3,7,1);	// PWR_OFF
	TZ_GPIO_write(3,6,1);	// FW_UPDATE_OFF

    CLK_SysTickLongDelay(3000000);

	TZ_GPIO_write(0,11,1);	// green LED
    TZ_GPIO_write(3,6,1); // Set 1 to Disable WIFI module firmware update.
    CLK_SysTickLongDelay(1000000);
	TZ_GPIO_write(3,7,0);

    /* Bypass AT commands from debug port to WiFi port */
    while(1)
    {
        if(TZ_UART_available(3)!=0)
        {
            TZ_UART_write_char(0,TZ_UART_read_char(3);
        }

        if(TZ_UART_available(0)!=0)
        {
            TZ_UART_write_char(3,TZ_UART_read_char(0);
        }
    }
}
