#include <stdio.h>
#include "NuMicro.h"
#include "TZ_s.h"

static NonSecure_funcptr func_F_pf = (NonSecure_funcptr)NULL;
__NONSECURE_ENTRY
int32_t func_F_callback(NonSecure_funcptr *callback){
	func_F_pf = (NonSecure_funcptr)cmse_nsfptr_create(callback);
	return 0;
}
__NONSECURE_ENTRY
void SYS_Init(void)
{
    /* Enable PLL */
    CLK->PLLCTL = CLK_PLLCTL_128MHz_HIRC;

    /* Waiting for PLL stable */
    while((CLK->STATUS & CLK_STATUS_PLLSTB_Msk) == 0);

    /* Set HCLK divider to 2 */
    CLK->CLKDIV0 = (CLK->CLKDIV0 & (~CLK_CLKDIV0_HCLKDIV_Msk)) | 1;

    /* Switch HCLK clock source to PLL */
    CLK->CLKSEL0 = (CLK->CLKSEL0 & (~CLK_CLKSEL0_HCLKSEL_Msk)) | CLK_CLKSEL0_HCLKSEL_PLL;

    /* Select IP clock source */
    CLK->CLKSEL1 = CLK_CLKSEL1_UART0SEL_HIRC;

    /* Enable IP clock */
    CLK->APBCLK0 |= CLK_APBCLK0_UART0CKEN_Msk;


    /* Update System Core Clock */
    /* User can use SystemCoreClockUpdate() to calculate PllClock, SystemCoreClock and CycylesPerUs automatically. */
    //SystemCoreClockUpdate();
    PllClock        = 128000000;            // PLL
    SystemCoreClock = 128000000 / 2;        // HCLK
    CyclesPerUs     = 64000000 / 1000000;   // For SYS_SysTickDelay()

    /*---------------------------------------------------------------------------------------------------------*/
    /* Init I/O Multi-function                                                                                 */
    /*---------------------------------------------------------------------------------------------------------*/
    /* Set multi-function pins for UART0 RXD and TXD */
    SYS->GPB_MFPH = (SYS->GPB_MFPH & (~(UART0_RXD_PB12_Msk | UART0_TXD_PB13_Msk))) | UART0_RXD_PB12 | UART0_TXD_PB13;

}

__NONSECURE_ENTRY
void func_A(){
	printf("this is func_A\n");
}

__NONSECURE_ENTRY
void func_B(){
	printf("this is func_B\n");
}

__NONSECURE_ENTRY
void func_C(){
	printf("this is func_C\n");
}

__NONSECURE_ENTRY
void func_D(){
	printf("this is func_D\n");
	func_A();
	func_B();
	func_C();
}

__NONSECURE_ENTRY
void func_E(){
	printf("this is func_E\n");
}

__NONSECURE_ENTRY
void func_G(){
	printf("this is func_G\n");
	func_E();
	func_F_pf(1u);
}

int main(void){
    SYS_UnlockReg();
    SYS_Init();
    TZ_Boot_Init(NEXT_BOOT_BASE);
}
