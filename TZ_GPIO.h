#include <arm_cmse.h>
#include "NuMicro.h"                      /* Device header */
#include "partition_M2351.h"

int secure[8]={0,0,0,0,0,0,0,0};

void PA_set_secure(){secure[0]=1;}

/*
void setSecure(int no){
	if(no>=0&&no<=5){
		secure[no]=1;
	}
}
*/

__NONSECURE_ENTRY
void GPIO_Init(){
	for(int i=0;i<6;i++){
		if(!secure[i]){
			SCU_SET_IONSSET(SCU_IONSSET_PA_Msk<<(SCU_IONSSET_PA_Pos+i));
		}
	}
}

__NONSECURE_ENTRY
void PA_S_set_mode(uint32_t u32PinMask, uint32_t u32Mode){
	GPIO_SetMode(PA_S, u32PinMask, u32Mode);
}

void PA_set_mode(uint32_t u32PinMask, uint32_t u32Mode){
	if(secure[0]){
		PA_S_set_mode(u32PinMask, u32Mode);
	}
	else{
		GPIO_SetMode(PA_NS, u32PinMask, u32Mode);
	}
}

__NONSECURE_ENTRY
int PA_secure_read(int pin){
	return GPIO_PIN_DATA_S(0,pin);
}

int PA_read(int pin){
	if(secure[0]) return PA_secure_read(pin);
	else return GPIO_PIN_DATA_NS(0,pin);
}

__NONSECURE_ENTRY
void PA_secure_write(int pin, int value){
	GPIO_PIN_DATA_S(0,pin)=value;
}

void PA_write(int pin, int value){
	if(secure[0]) return PA_secure_write(pin,value);
	else GPIO_PIN_DATA_NS(0,pin)=value;
	return;
}
