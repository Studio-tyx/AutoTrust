#include <arm_cmse.h>
#include "NuMicro.h"
#include "partition_M2351.h"

typedef __NONSECURE_CALL int32_t (*NonSecure_funcptr)(uint32_t);
#define NEXT_BOOT_BASE  0x10040000
#define JUMP_HERE       0xe7fee7ff

void TZ_Boot_Init(uint32_t u32BootBase);
