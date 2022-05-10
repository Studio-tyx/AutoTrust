#include <stdio.h>
#include "M2351.h"
#include "TZ.h"

void SYS_Init(void);

void SYS_Init@TrustZoneAll(void)
{
    /* Enable PLL */
    CLK->PLLCTL = CLK_PLLCTL_128MHz_HIRC;

    /* Waiting for PLL stable */
    while((CLK->STATUS & CLK_STATUS_PLLSTB_Msk) == 0);

    /* Set HCLK divider to 2 */
    CLK->CLKDIV0 = (CLK->CLKDIV0 & (~CLK_CLKDIV0_HCLKDIV_Msk)) | 1;

    /* Switch HCLK clock source to PLL */
    CLK->CLKSEL0 = (CLK->CLKSEL0 & (~CLK_CLKSEL0_HCLKSEL_Msk)) | CLK_CLKSEL0_HCLKSEL_PLL;

    CLK->PWRCTL |= CLK_PWRCTL_HIRC48EN_Msk;
    while((CLK->STATUS & CLK_STATUS_HIRC48STB_Msk) == 0);
    CLK->CLKSEL0 = CLK_CLKSEL0_HCLKSEL_HIRC48;

    /* Select IP clock source */
    CLK->CLKSEL1 = CLK_CLKSEL1_UART0SEL_HIRC | CLK_CLKSEL1_UART1SEL_HIRC;
    CLK->CLKSEL3 = CLK_CLKSEL3_UART2SEL_HIRC | CLK_CLKSEL3_UART3SEL_HIRC | CLK_CLKSEL3_UART5SEL_HIRC;

    /* Enable IP clock */
    CLK->APBCLK0 |= CLK_APBCLK0_UART0CKEN_Msk | CLK_APBCLK0_TMR0CKEN_Msk | CLK_APBCLK0_UART1CKEN_Msk |
                    CLK_APBCLK0_UART2CKEN_Msk | CLK_APBCLK0_UART3CKEN_Msk | CLK_APBCLK0_UART5CKEN_Msk;

	CLK->APBCLK0 |= CLK_APBCLK0_UART4CKEN_Msk;
    CLK->CLKSEL3 = (CLK->CLKSEL3 & (~CLK_CLKSEL3_UART3SEL_Msk)) | CLK_CLKSEL3_UART3SEL_HIRC;
	SYS->GPD_MFPL = (SYS->GPD_MFPL & (~(UART3_RXD_PD0_Msk | UART3_TXD_PD1_Msk))) | UART3_RXD_PD0 | UART3_TXD_PD1;

    /* Update System Core Clock */
    /* User can use SystemCoreClockUpdate() to calculate PllClock, SystemCoreClock and CycylesPerUs automatically. */
    //SystemCoreClockUpdate();
    PllClock        = 128000000;           // PLL
    SystemCoreClock = 128000000 / 2;       // HCLK
    CyclesPerUs     = 64000000 / 1000000;  // For SYS_SysTickDelay()

    /*---------------------------------------------------------------------------------------------------------*/
    /* Init I/O Multi-function                                                                                 */
    /*---------------------------------------------------------------------------------------------------------*/
    /* Set multi-function pins for UART0 RXD and TXD */
    SYS->GPB_MFPH = (SYS->GPB_MFPH & (~(UART0_RXD_PB12_Msk | UART0_TXD_PB13_Msk))) | UART0_RXD_PB12 | UART0_TXD_PB13;

}

int main()
{
    SYS_UnlockReg();
    SYS_Init();
	
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
