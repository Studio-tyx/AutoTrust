#include "TZ_GPIO_ns.h"
#include "TZ_ns.h"
extern void SYS_Init(void);
int main(){
	int GPIO_security[8]={1,0,0,0,0,0,0,0};
	TZ_GPIO_set_secure(GPIO_security);
	TZ_GPIO_set_mode(0,BIT10|BIT11,GPIO_MODE_OUTPUT);
	TZ_GPIO_write(0,10,1);
	TZ_GPIO_write(0,11,0);
	CLK_SysTickLongDelay(200000);
	TZ_GPIO_write(0,10,0);
	TZ_GPIO_write(0,11,1);
}
