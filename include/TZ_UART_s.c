#include "TZ_UART_s.h"

__NONSECURE_ENTRY
void TZ_scu_set_UART(int i){
	SCU_SET_PNSSET(UART0_Attr+i);
}

__NONSECURE_ENTRY
void TZ_s_UART_init(int uart, int32_t baud_rate){
	(UART0_S+uart*0x1000UL)->BAUD = UART_BAUD_MODE2 | UART_BAUD_MODE2_DIVIDER(__HIRC, baud_rate);
  (UART0_S+uart*0x1000UL)->LINE = UART_WORD_LEN_8 | UART_PARITY_NONE | UART_STOP_BIT_1;
}

__NONSECURE_ENTRY
void TZ_s_UART_write_char(int uart, char ch){
	while((UART0_S+uart*0x1000UL)->FIFOSTS & UART_FIFOSTS_TXFULL_Msk){}
	if((char)ch == '\n'){
		(UART0_S+uart*0x1000UL)->DAT = '\r';
		while((UART0_S+uart*0x1000UL)->FIFOSTS & UART_FIFOSTS_TXFULL_Msk){}
	}
	(UART0_S+uart*0x1000UL)->DAT = (uint32_t)ch;
}

__NONSECURE_ENTRY
int TZ_s_UART_available(int uart){
	return (((UART0_S+uart*0x1000UL)->FIFOSTS & UART_FIFOSTS_RXEMPTY_Msk) == 0);
}

__NONSECURE_ENTRY
char TZ_s_UART_read_char(int uart){
	while((UART0_S+uart*0x1000UL)->FIFOSTS & UART_FIFOSTS_RXEMPTY_Msk);
	return ((char)(UART0_S+uart*0x1000UL)->DAT);
}