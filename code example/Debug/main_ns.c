#include "TZ_ns.h"
extern int32_t func_F_callback(int32_t (*)(uint32_t));
extern void SYS_Init(void);
extern void func_A();
extern void func_B();
extern void func_C();
extern void func_D();
extern void func_E();
void func_F(){
	printf("this is func_F\n");
}

extern void func_G();
int main()
{
	func_F_callback(&func_F);


    UART0->LINE = UART_PARITY_NONE | UART_STOP_BIT_1 | UART_WORD_LEN_8;
    UART0->BAUD = UART_BAUD_MODE2 | UART_BAUD_MODE2_DIVIDER(__HIRC, 115200);

    func_D();
	func_G();
		
	while(1){
		
	}
}
