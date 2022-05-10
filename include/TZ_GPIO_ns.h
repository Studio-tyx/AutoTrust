#include <arm_cmse.h>
#include "NuMicro.h"
#include "partition_M2351.h"

void TZ_GPIO_set_secure(int port[]);
void TZ_GPIO_set_mode(int port, uint32_t u32PinMask, uint32_t u32Mode);
int TZ_GPIO_read(int port, int pin);
void TZ_GPIO_write(int port, int pin, int value);
