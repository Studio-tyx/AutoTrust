#include "TZ_UART_ns.h"


extern void TZ_scu_set_UART(int i);
extern void TZ_s_UART_init(int uart, int32_t baud_rate);
extern void TZ_s_UART_write_char(int uart, char ch);
extern int TZ_s_UART_available(int uart);
extern char TZ_s_UART_read_char(int uart);

int UART_security[6]={0,0,0,0,0,0};

void TZ_UART_set_secure(int uart[]){
	for(int i=0;i<6;i++){
		UART_security[i]=uart[i];
		if(!UART_security[i]){
			TZ_scu_set_UART(i);
		}
	}
}

void TZ_UART_init(int uart, int32_t baud_rate){
	if(uart<=5&&uart>=0){
		if(UART_security[0]){
			TZ_s_UART_init(uart,baud_rate);
		}
		else{
			(UART0_NS+uart*0x1000UL)->BAUD = UART_BAUD_MODE2 | UART_BAUD_MODE2_DIVIDER(__HIRC, baud_rate);
			(UART0_NS+uart*0x1000UL)->LINE = UART_WORD_LEN_8 | UART_PARITY_NONE | UART_STOP_BIT_1;
		}
	}
}

void TZ_UART_write_char(int uart, char ch){
	if(uart<=5&&uart>=0){
		if(UART_security[uart]){
			TZ_s_UART_write_char(uart, ch);
		}
		else{
			while(UART0_NS->FIFOSTS & UART_FIFOSTS_TXFULL_Msk){}
			if((char)ch == '\n'){
				UART0_NS->DAT = '\r';
				while(UART0_NS->FIFOSTS & UART_FIFOSTS_TXFULL_Msk){}
			}
			UART0_NS->DAT = (uint32_t)ch;
		}
	}
}

void TZ_UART_write(int uart, const char* str){
	if(uart<=5&&uart>=0){
		int32_t i=0;
		while(str[i]!='\0'){
			TZ_UART_write_char(uart, str[i]);
			i++;
		}
	}
	else return;
}

int TZ_UART_available(int uart){
	if(uart<=5&&uart>=0){
		if(UART_security[uart]) return TZ_s_UART_available(uart);
		else return ((UART0->FIFOSTS & UART_FIFOSTS_RXEMPTY_Msk) == 0);
		}
	else return -1;
}

char TZ_UART_read_char(int uart){
	if(uart<=5&&uart>=0){
		if(UART_security[uart]) return TZ_s_UART_read_char(uart);
		else{
			while((UART0_NS+uart*0x1000UL)->FIFOSTS & UART_FIFOSTS_RXEMPTY_Msk);
			return ((char)(UART0_NS+uart*0x1000UL)->DAT);
		}
	}
	else return -1;
}