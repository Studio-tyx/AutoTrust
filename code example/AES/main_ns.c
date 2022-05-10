#include "TZ_ns.h"
extern void SYS_Init(void);

/* timer ticks - 100 ticks per second */
static volatile uint32_t  s_u32TickCnt;

void SysTick_Handler(void);
void enable_sys_tick(int ticks_per_second);
void start_timer0(void);
uint32_t  get_timer0_counter(void);
int AESTest(void);
extern unsigned char** read_aes_test_cfb128_key();
extern void write_aes_test_cfb128_key(unsigned char** value);
extern unsigned char** read_aes_test_cfb128_iv();
extern void write_aes_test_cfb128_iv(unsigned char** value);

void SysTick_Handler(void)
{
    s_u32TickCnt++;
}

void enable_sys_tick(int ticks_per_second)
{
    s_u32TickCnt = 0;
    SystemCoreClock = 64000000;         /* HCLK is 64 MHz */
    if(SysTick_Config(SystemCoreClock / (uint32_t)ticks_per_second))
    {
        /* Setup SysTick Timer for 1 second interrupts  */
        printf("Set system tick error!!\n");
        while(1);
    }
}

void start_timer0()
{
    /* Start TIMER0  */
    CLK->CLKSEL1 = (CLK->CLKSEL1 & (~CLK_CLKSEL1_TMR0SEL_Msk)) | CLK_CLKSEL1_TMR0SEL_HXT;
    CLK->APBCLK0 |= CLK_APBCLK0_TMR0CKEN_Msk;    /* enable TIMER0 clock                  */
    TIMER0->CTL = 0;                   /* disable timer                                  */
    TIMER0->INTSTS = (TIMER_INTSTS_TWKF_Msk | TIMER_INTSTS_TIF_Msk);  /* clear interrupt status */
    TIMER0->CMP = 0xFFFFFE;            /* maximum time                                   */
    TIMER0->CNT = 0;                   /* clear timer counter                            */
    /* start timer */
    TIMER0->CTL = (11 << TIMER_CTL_PSC_Pos) | TIMER_ONESHOT_MODE | TIMER_CTL_CNTEN_Msk;
}

uint32_t  get_timer0_counter()
{
    return TIMER0->CNT;
}

unsigned char aes_test_cfb128_pt[64] =
{
    0x6B, 0xC1, 0xBE, 0xE2, 0x2E, 0x40, 0x9F, 0x96,
    0xE9, 0x3D, 0x7E, 0x11, 0x73, 0x93, 0x17, 0x2A,
    0xAE, 0x2D, 0x8A, 0x57, 0x1E, 0x03, 0xAC, 0x9C,
    0x9E, 0xB7, 0x6F, 0xAC, 0x45, 0xAF, 0x8E, 0x51,
    0x30, 0xC8, 0x1C, 0x46, 0xA3, 0x5C, 0xE4, 0x11,
    0xE5, 0xFB, 0xC1, 0x19, 0x1A, 0x0A, 0x52, 0xEF,
    0xF6, 0x9F, 0x24, 0x45, 0xDF, 0x4F, 0x9B, 0x17,
    0xAD, 0x2B, 0x41, 0x7B, 0xE6, 0x6C, 0x37, 0x10
};

unsigned char aes_test_cfb128_ct[3][64] =
{
    {   0x3B, 0x3F, 0xD9, 0x2E, 0xB7, 0x2D, 0xAD, 0x20,
        0x33, 0x34, 0x49, 0xF8, 0xE8, 0x3C, 0xFB, 0x4A,
        0xC8, 0xA6, 0x45, 0x37, 0xA0, 0xB3, 0xA9, 0x3F,
        0xCD, 0xE3, 0xCD, 0xAD, 0x9F, 0x1C, 0xE5, 0x8B,
        0x26, 0x75, 0x1F, 0x67, 0xA3, 0xCB, 0xB1, 0x40,
        0xB1, 0x80, 0x8C, 0xF1, 0x87, 0xA4, 0xF4, 0xDF,
        0xC0, 0x4B, 0x05, 0x35, 0x7C, 0x5D, 0x1C, 0x0E,
        0xEA, 0xC4, 0xC6, 0x6F, 0x9F, 0xF7, 0xF2, 0xE6
    },
    {   0xCD, 0xC8, 0x0D, 0x6F, 0xDD, 0xF1, 0x8C, 0xAB,
        0x34, 0xC2, 0x59, 0x09, 0xC9, 0x9A, 0x41, 0x74,
        0x67, 0xCE, 0x7F, 0x7F, 0x81, 0x17, 0x36, 0x21,
        0x96, 0x1A, 0x2B, 0x70, 0x17, 0x1D, 0x3D, 0x7A,
        0x2E, 0x1E, 0x8A, 0x1D, 0xD5, 0x9B, 0x88, 0xB1,
        0xC8, 0xE6, 0x0F, 0xED, 0x1E, 0xFA, 0xC4, 0xC9,
        0xC0, 0x5F, 0x9F, 0x9C, 0xA9, 0x83, 0x4F, 0xA0,
        0x42, 0xAE, 0x8F, 0xBA, 0x58, 0x4B, 0x09, 0xFF
    },
    {   0xDC, 0x7E, 0x84, 0xBF, 0xDA, 0x79, 0x16, 0x4B,
        0x7E, 0xCD, 0x84, 0x86, 0x98, 0x5D, 0x38, 0x60,
        0x39, 0xFF, 0xED, 0x14, 0x3B, 0x28, 0xB1, 0xC8,
        0x32, 0x11, 0x3C, 0x63, 0x31, 0xE5, 0x40, 0x7B,
        0xDF, 0x10, 0x13, 0x24, 0x15, 0xE5, 0x4B, 0x92,
        0xA1, 0x3E, 0xD0, 0xA8, 0x26, 0x7A, 0xE2, 0xF9,
        0x75, 0xA3, 0x85, 0x74, 0x1A, 0xB9, 0xCE, 0xF8,
        0x20, 0x31, 0x62, 0x3D, 0x55, 0xB1, 0xE4, 0x71
    }
};

int AESTest(void)
{
    int verbose = 1;
    int ret = 0, i, u, mode;
    unsigned int keybits;
    unsigned char key[32];
    unsigned char buf[64];
    const unsigned char *aes_tests;
    unsigned char iv[16];
    size_t offset;
    uint32_t u32Time;

    mbedtls_aes_context ctx;

    memset( key, 0, 32 );
    mbedtls_aes_init( &ctx );


    /*
     * CFB128 mode
     */
    printf(" AES CFB Mode:\n\n");
    for( i = 0; i < 6; i++ )
    {
        u = i >> 1;
        keybits = 128 + (unsigned int)u * 64;
        mode = i & 1;

        if( verbose != 0 )
            printf( "  AES-CFB128-%3d (%s): ", keybits,
                    ( mode == MBEDTLS_AES_DECRYPT ) ? "dec" : "enc" );

        memcpy( iv,  read_aes_test_cfb128_iv()[u], 16 );
        memcpy( key, read_aes_test_cfb128_key()[u], keybits / 8 );

        offset = 0;

        enable_sys_tick(1000);
        start_timer0();
        /* Sets the AES encryption key */
        ret = mbedtls_aes_setkey_enc( &ctx, key, keybits );

        if( ret == MBEDTLS_ERR_PLATFORM_FEATURE_UNSUPPORTED && keybits == 192 )
        {
            printf( "skipped\n" );
            continue;
        }
        else if( ret != 0 )
        {
            goto exit;
        }

        if( mode == MBEDTLS_AES_DECRYPT )
        {
            memcpy( buf, aes_test_cfb128_ct[u], 64 );
            aes_tests = aes_test_cfb128_pt;
        }
        else
        {
            memcpy( buf, aes_test_cfb128_pt, 64 );
            aes_tests = aes_test_cfb128_ct[u];
        }

        /* AES-CFB128 buffer encryption/decryption */
        ret = mbedtls_aes_crypt_cfb128( &ctx, mode, 64, &offset, iv, buf, buf );
        if( ret != 0 )
            goto exit;

        if( memcmp( buf, aes_tests, 64 ) != 0 )
        {
            ret = 1;
            goto exit;
        }

        if( verbose != 0 )
        {
            printf("passed");
            u32Time = get_timer0_counter();

            /* TIMER0->CNT is the elapsed us */
            printf("     takes %d us,  %d ticks\n", u32Time, s_u32TickCnt);
        }
    }

    if( verbose != 0 )
        printf( "\n" );

    ret = 0;

exit:
    if( ret != 0 && verbose != 0 )
        printf( "failed\n" );

    mbedtls_aes_free( &ctx );

    return( ret );
}

int32_t main(void)
{
    int  i32Ret = MBEDTLS_EXIT_SUCCESS;

    printf("\n AES test start...\n\n");
    i32Ret = AESTest();
    printf("\n AES test done ...\n");

    if(i32Ret == MBEDTLS_EXIT_SUCCESS)
    {
        printf("\nTest OK\n");
    }
    else
    {
        printf("\nTest fail\n");
    }
	
    while(1);
}
