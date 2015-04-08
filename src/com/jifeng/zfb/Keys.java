/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 * 
 *  提示：如何获取安全校验码和合作身份者id
 *  1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *  2.点击“商家服务”(https://b.alipay.com/order/myorder.htm)
 *  3.点击“查询合作者身份(pid)”、“查询安全校验码(key)”
 */

package com.jifeng.zfb;

//
// 请参考 Android平台安全支付服务(msp)应用开发接口(4.2 RSA算法签名)部分，并使用压缩包中的openssl RSA密钥生成工具，生成一套RSA公私钥。
// 这里签名时，只需要使用生成的RSA私钥。
// Note: 为安全起见，使用RSA私钥进行签名的操作过程，应该尽量放到商家服务器端去进行。
public final class Keys {

	// 合作身份者id，以2088开头的16位纯数字
	public static final String DEFAULT_PARTNER = "2088711884547922";

	// //收款支付宝账号
	public static final String DEFAULT_SELLER = "lixiaojia@jumeimiao.com";

	// 商户私钥，自助生成
	public static final String PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALx1AGJzTPneqnzaIFZhcVcTj2pINUa3GaV3VH0jxmA+PnRfYqVRBdesN42D1BFFclfKLnlwwXu57icfBs5BUuE1IrECyvnT6+b5oprS7xi4EYYo1QnyMocfmrON8SAJi3uLUzbkdQEG5rPfXJYuWbbo3djd+6iiY68I/bWZTYDvAgMBAAECgYAI9dcq1yiVyMx6WzSmZsOozDlJaF2AGzyQ7XeK66SG9u6pQc/C5HNOqdWKWPZAS/j/y5EiHQZWB6UXHxaHv/tcvPB+8SAVMf0PkxS1aXkYUN2DPZ33bBFcBO+5708SePChEe2tH1K6MauseKh1odbhvBZC4e4+ABmHlUaKVG61YQJBAOJxcDBDxhqdXjb8dcbGVO725rdfb8UmIIQoNqIhLtyWL1Rd/DOXr1T1vaGo4VnBeZCktrxdTM66QGyeCJHzuaMCQQDVDkOlliDSbPfTDpuAnZvcV70XUQoSCW+ewR9XhN5xYuTQ26ykY5pJ8R1SABaSf6CsrjWBKA8QfxC4yi7rqShFAkEAks+tRv2J4ROKU+gWjAtmYZAeQWFU2+M+TbCsyyzsbwPIJ9DPhLuhZ1nz2m3dvQ2dPVQtTa7H73f98O387HOyNQJBAMgO++Bp52oQHmmV5tjPpkIyNQHG/ADR0Xkt+OMyhbnM1fV0wVkfmgpimVidcpSfuV1MvDsKRZME7cvHNmXdUk0CQCUXHEQwdFMJNDInyCXNLolGY18He/vlHLm2iXt5wW60WtP9JQ7hNm3rPrQBx3lc0IdjNHDrPRjAIa/U0LD3BP0=";

	//public static final String PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC8dQBic0z53qp82iBWYXFXE49qSDVGtxmld1R9I8ZgPj50X2KlUQXXrDeNg9QRRXJXyi55cMF7ue4nHwbOQVLhNSKxAsr50+vm+aKa0u8YuBGGKNUJ8jKHH5qzjfEgCYt7i1M25HUBBuaz31yWLlm26N3Y3fuoomOvCP21mU2A7wIDAQAB";

}
