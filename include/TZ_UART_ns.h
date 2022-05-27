#include <arm_cmse.h>
#include "NuMicro.h" 
#include "partition_M2351.h"

void TZ_UART_set_secure(int uart[]);
void TZ_UART_init(int uart, int32_t baud_rate);
void TZ_UART_write_char(int uart, char ch);
void TZ_UART_write(int uart, const char* str);
int TZ_UART_available(int uart);
char TZ_UART_read_char(int uart);