#include "TZ_ns.h"

#ifndef OPTION
# define OPTION      0 // 0: Play sin, 1: rec/play loopback
#endif
#define I2C_PORT        I2C2
#define I2S             I2S0

#if (OPTION == 0)

static __attribute__((aligned(32))) int16_t s_ai16sin[96] =
{
    0, 0, -134, -134, -266, -266, -393, -393, -513, -513, -625, -625, -726, -726, -814, -814, -889, -889, -948, -948, -991, -991, -1018, -1018, -1026, -1026, -1018, -1018, -991, -991, -948, -948, -889, -889, -814, -814, -726, -726, -625, -625, -513, -513, -393, -393, -266, -266, -134, -134, 0, 0, 134, 134, 266, 266, 393, 393, 513, 513, 625, 625, 726, 726, 814, 814, 889, 889, 948, 948, 991, 991, 1018, 1018, 1026, 1026, 1018, 1018, 991, 991, 948, 948, 889, 889, 814, 814, 726, 726, 625, 625, 513, 513, 393, 393, 266, 266, 134, 134
};

static uint32_t *g_pu32sin = (uint32_t *)(uint32_t)&s_ai16sin[0];
static uint32_t *g_pu32sin;
static int32_t g_i32Idx = 0;

void I2S0_IRQHandler(void)
{
    uint32_t u32I2SIntFlag;

    u32I2SIntFlag = I2S->STATUS0;//  I2S_GET_INT_FLAG(I2S, I2S_STATUS_I2STXINT_Msk | I2S_STATUS_I2SRXINT_Msk);
    if(u32I2SIntFlag & I2S_STATUS0_TXTHIF_Msk)
    {
        /* Force to play sin wave */
        /* Fill 4 word data when it is TX threshold interrupt */
        /* Play to I2S */
        I2S_WRITE_TX_FIFO(I2S, g_pu32sin[g_i32Idx++]);
        if(g_i32Idx >= 48) g_i32Idx = 0;
        I2S_WRITE_TX_FIFO(I2S, g_pu32sin[g_i32Idx++]);
        if(g_i32Idx >= 48) g_i32Idx = 0;
        I2S_WRITE_TX_FIFO(I2S, g_pu32sin[g_i32Idx++]);
        if(g_i32Idx >= 48) g_i32Idx = 0;
        I2S_WRITE_TX_FIFO(I2S, g_pu32sin[g_i32Idx++]);
        if(g_i32Idx >= 48) g_i32Idx = 0;

    }

    if(u32I2SIntFlag & I2S_STATUS0_RXTHIF_Msk)
    {
        I2S_READ_RX_FIFO(I2S);
        I2S_READ_RX_FIFO(I2S);
        I2S_READ_RX_FIFO(I2S);
        I2S_READ_RX_FIFO(I2S);
    }
}


#else

static volatile uint32_t s_au32Tmp[8] = {0};
void I2S0_IRQHandler(void)
{
    uint32_t u32I2SIntFlag;

    u32I2SIntFlag = I2S->STATUS0;//  I2S_GET_INT_FLAG(I2S, I2S_STATUS_I2STXINT_Msk | I2S_STATUS_I2SRXINT_Msk);
    if(u32I2SIntFlag & I2S_STATUS0_TXTHIF_Msk)
    {
        /* Force to play sin wave */
        /* Fill 4 word data when it is TX threshold interrupt */
        /* Play to I2S */
        I2S_WRITE_TX_FIFO(I2S, s_au32Tmp[0]);
        I2S_WRITE_TX_FIFO(I2S, s_au32Tmp[1]);
        I2S_WRITE_TX_FIFO(I2S, s_au32Tmp[2]);
        I2S_WRITE_TX_FIFO(I2S, s_au32Tmp[3]);
    }

    if(u32I2SIntFlag & I2S_STATUS0_RXTHIF_Msk)
    {
        s_au32Tmp[0] = I2S_READ_RX_FIFO(I2S);
        s_au32Tmp[1] = I2S_READ_RX_FIFO(I2S);
        s_au32Tmp[2] = I2S_READ_RX_FIFO(I2S);
        s_au32Tmp[3] = I2S_READ_RX_FIFO(I2S);
    }
}

#endif

extern void SYS_Init(void);
void UART0_Init(void)
{

    /* Configure UART0 and set UART0 Baudrate */
    UART0->BAUD = UART_BAUD_MODE2 | UART_BAUD_MODE2_DIVIDER(__HIRC, 115200);
    UART0->LINE = UART_WORD_LEN_8 | UART_PARITY_NONE | UART_STOP_BIT_1;
}


void I2C_Init(void)
{
    /* Open I2C and set clock to 100k */
    I2C_Open(I2C_PORT, 100000);

    /* Get I2C Bus Clock */
    printf("I2C clock %d Hz\n", I2C_GetBusClockFreq(I2C_PORT));
}

extern void Codec_Delay(uint32_t delayCnt);
extern uint8_t I2cWrite_MultiByteforNAU88L25(uint8_t chipadd, uint16_t subaddr, const uint8_t *p, uint32_t len);
extern uint8_t I2C_WriteNAU88L25(uint16_t addr, uint16_t dat);
extern void NAU88L25_Setup(void);

/*---------------------------------------------------------------------------------------------------------*/
/*  Main Function                                                                                          */
/*---------------------------------------------------------------------------------------------------------*/
int32_t main(void)
{

    /* Initial UART0 for debug message */
    UART0_Init();

    printf("\n");
    printf("+-------------------------------------------------------+\n");
    printf("|          NuMicro USB Audio CODEC Sample Code          |\n");
    printf("+-------------------------------------------------------+\n");

    /* Init I2C to access NAU88L25 */
    I2C_Init();

    I2S_Open(I2S0, I2S_MODE_SLAVE, 48000, I2S_DATABIT_16, I2S_STEREO, I2S_FORMAT_I2S);

    /* Set MCLK and enable MCLK */
    I2S_EnableMCLK(I2S0, 12000000);

    I2S_SetFIFO(I2S0, 4, 4);

    /* Fill dummy data to I2S TX for start I2S iteration */
#if OPTION == 0
	int32_t i;
    for(i = 0; i < 16; i++)
        I2S_WRITE_TX_FIFO(I2S, 0);
#endif

    /* Start I2S play iteration */
    I2S_EnableInt(I2S0, I2S_IEN_TXTHIEN_Msk | I2S_IEN_RXTHIEN_Msk);

    NVIC_EnableIRQ(I2S0_IRQn);

    I2S_ENABLE_TX(I2S0);
    I2S_ENABLE_RX(I2S0);

    /* Initialize NAU88L25 codec */
    NAU88L25_Setup();

    while(1);

}
