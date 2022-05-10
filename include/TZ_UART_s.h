#include <arm_cmse.h>
#include "NuMicro.h"
#include "partition_M2351.h"


extern void TZ_UART_init(int uart, int32_t baud_rate);

__NONSECURE_ENTRY
void TZ_scu_set_UART(int i);

__NONSECURE_ENTRY
void TZ_s_UART_init(int uart, int32_t baud_rate);

__NONSECURE_ENTRY
void TZ_s_UART_write_char(int uart, char ch);

__NONSECURE_ENTRY
int TZ_s_UART_available(int uart);

__NONSECURE_ENTRY
char TZ_s_UART_read_char(int uart);