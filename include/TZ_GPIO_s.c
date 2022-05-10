#include "TZ_GPIO_s.h"

int GPIO_security[8]={0,0,0,0,0,0,0,0};

__NONSECURE_ENTRY
void TZ_scu_set_GPIO(int i){
	SCU_SET_IONSSET(SCU_IONSSET_PA_Msk<<(SCU_IONSSET_PA_Pos+i));
}

__NONSECURE_ENTRY
void transfer_GPIO(int port[]){
	for(int i=0;i<8;i++){
		GPIO_security[i]=port[i];
	}
}

__NONSECURE_ENTRY
void TZ_s_GPIO_set_mode(int port, uint32_t u32PinMask, uint32_t u32Mode){
	if(GPIO_security[port])GPIO_SetMode(PA_NS+port*0x40UL, u32PinMask, u32Mode);
	else GPIO_SetMode(PA_S+port*0x40UL, u32PinMask, u32Mode);
}

__NONSECURE_ENTRY
int TZ_s_GPIO_read(int port, int pin){
	if(GPIO_security[port])return GPIO_PIN_DATA_NS(port,pin);
	else return GPIO_PIN_DATA_S(port,pin);
}

__NONSECURE_ENTRY
void TZ_s_GPIO_write(int port, int pin, int value){
	if(GPIO_security[port])GPIO_PIN_DATA_NS(0,pin)=value;
	else GPIO_PIN_DATA_S(0,pin)=value;
}
