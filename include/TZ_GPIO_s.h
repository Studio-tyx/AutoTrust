#include <arm_cmse.h>
#include "NuMicro.h"
#include "partition_M2351.h"

__NONSECURE_ENTRY
void TZ_scu_set_GPIO(int i);

__NONSECURE_ENTRY
void TZ_s_GPIO_set_mode(int port, uint32_t u32PinMask, uint32_t u32Mode);

__NONSECURE_ENTRY
int TZ_s_GPIO_read(int port, int pin);

__NONSECURE_ENTRY
void TZ_s_GPIO_write(int port, int pin, int value);

