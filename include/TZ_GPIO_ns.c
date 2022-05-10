#include "TZ_GPIO_ns.h"

extern void TZ_scu_set_GPIO();
extern void TZ_s_GPIO_set_mode(int port, uint32_t u32PinMask, uint32_t u32Mode);
extern int TZ_s_GPIO_read(int port, int pin);
extern void TZ_s_GPIO_write(int port, int pin, int value);
extern void transfer_GPIO(int port[]);

int GPIO_security[8]={0,0,0,0,0,0,0,0};

void TZ_GPIO_set_secure(int port[]){
	for(int i=0;i<8;i++){
		GPIO_security[i]=port[i];
		if(!port[i]){	// port[i]==0
			TZ_scu_set_GPIO(i);	// set to ns
		}
	}
	transfer_GPIO(port);
}

void TZ_GPIO_set_mode(int port, uint32_t u32PinMask, uint32_t u32Mode){
	if(port<=5&&port>=0){
		if(GPIO_security[port]){
			TZ_s_GPIO_set_mode(port,u32PinMask, u32Mode);
		}
		else{
			GPIO_SetMode(PA_NS+port*0x40UL, u32PinMask, u32Mode);
		}
	}
}

int TZ_GPIO_read(int port, int pin){
	if(port<=5&&port>=0){
		if(GPIO_security[port]) return TZ_s_GPIO_read(port,pin);
		else return GPIO_PIN_DATA_NS(port,pin);
	}
	else return -1;
}


void TZ_GPIO_write(int port, int pin, int value){
	if(port<=5&&port>=0){
		if(GPIO_security[port]) return TZ_s_GPIO_write(port,pin,value);
		else GPIO_PIN_DATA_NS(port,pin)=value;
		return;
	}
}
