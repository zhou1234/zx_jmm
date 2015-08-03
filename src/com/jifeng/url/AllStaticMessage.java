package com.jifeng.url;

import org.json.JSONObject;

public class AllStaticMessage {

	public static final String SPNE = "jumeimiao";
	public static final String NAME = "userName";
	public static final String PSW = "passWord";
	public static final String TYPE = "loginType";
	public static final String OPEN_ID = "openId";
	public static final String GENDER = "gender";
	public static final String NICK_NAME = "nickName";
	public static final String ADDRESS = "address";

	public static final String USER_PATH = "user_path";

	public static boolean user_flag = false;
	public static boolean isShare = false;

	public static String qudaoString = "";
	public static boolean guide = false;
	public static boolean guideRegister = false;
	public static boolean Register = false;

	/**
	 * 发布晒单后，返回的数据
	 */
	public static String title = "";
	public static String url = "";
	public static String imgurl = "";

	// 开发环境启用true，生产版本为false
	public static final boolean DEVELOPER_MODE = false;
	static final String authValidate = "2bd6854b72bc11bed0fe";

	/**
	 * 微信
	 */
	// APP_ID 替换为你的应用从官方网站申请到的合法appId
	public static final String APP_ID = "wx4625de2e23841ec2";// wx4625de2e23841ec2

	// APP_ID 对应的支付密钥
	public static final String APP_KEY = "2bd6854b72bc11bed0fee951d2d001e3";// 2bd6854b72bc11bed0fee951d2d001e3

	// APP_MCH 微信官方的partnerId
	public static final String APP_MCH = "1230268701";// 1230268701

	// 在线客服
	// public static final String KeFu_key = "vnroth0kr5hqo";
	public static final String KeFu_key = "m7ua80gbur0lm";
	public static String KeFu_Token = "";
	// public static final String KeFu_seviceId = "KEFU1421983315052";
	public static final String KeFu_seviceId = "KEFU1428905067406";
	/**
	 * 变量
	 */

	// 官方优惠券选择
	public static String guanquanid = "";
	public static String guanquan_name = "";
	public static String guanquan_value = "";

	// 购物车商品总数量
	public static int Car_num = 0;
	// 购物车图标变化
	public static boolean ShoppingCar = false;

	// loading标志
	public static boolean LoadingShowFlag = true;

	// mypay返回判断标志
	public static boolean MyPayBack = false;

	// 地址新建完成返回列表刷新
	public static boolean AddressListFlag = false;

	// 结算选择地址返回标记，数据
	public static boolean JieSuan_Select_Address = false;
	public static JSONObject mJsonObject_select_address = null;

	// 第三方登录返回判断
	// public static boolean otherFlag=false;

	// 头像
	// public static Bitmap mBitmap=null;
	// 保存登录信息
	public static String User_Name = null;
	public static String User_Psd = null;
	public static String Login_Flag = "";// 判断是否登录的标记
	public static String User_Id = "";
	public static String User_JiFen = "";
	public static String User_NickName = "";

	// 返回页面
	public static boolean Back_to_XianShiTm = false;
	public static boolean Back_to_ShoppingCar = false;
	public static boolean Back_to_Classion = false;
	public static boolean Back_to_Find = false;
	public static boolean Back_to_ZhangHu = false;

	// 第三方登录
	public static String OpenId = "";
	public static String NickName = "";
	public static String Address = "";
	public static String Gender = "";
	public static String userImage = "";

	// 订单状态
	public static String[] zhifu = { "未支付", "已成功", "已发货", "已收货", "申请退货",
			"交易完成", "退款完成" };

	public static String[] tuikuanyuanying = { "未按约定时间发货", "收货人信息错误", "订购重复",
			"订购错误", "不想买了", "价格原因", "其他" };

	// 所有订单是否刷新
	public static boolean OrderFormFlag = false;

	// 订单详情页面是否刷新
	public static boolean orderDetailFlag = false;
	// 判断订单状态
	public static String OrderId = "";
	public static String OrderPrice = "";
	public static String OrderStatus = "";

	// 微信支付订单编号
	public static String WxOrder = "";
	// 支付宝支付订单编号
	public static String zfb_Order = "";
	/**
	 * 接口部分
	 */
	public static String URL_GBase = "http://www.jumeimiao.com";
	//public static String URL_GBase = "http://192.168.2.120:8010";

	// public static String URL_GBase = "http://wwwpre.jumeimiao.com";
	// public static String URL_GBase = "http://192.168.2.134:8010";
	public static String URL_Base = URL_GBase + "/api";
	// 分类
	public static String class_url = URL_Base + "/p.ashx?m=category";
	// 分类item
	public static String class_url_item = URL_Base
			+ "/p.ashx?m=categoryProduct&categoryId=";
	// 根据活动id获取活动详情
	public static String active_url = URL_Base
			+ "/p.ashx?m=activedetail&activeId=";

	// 获取图片验证码
	public static String URL_GetImgCode = URL_GBase + "/getCode.aspx?code=";
	// 登录
	public static String URL_Login = URL_Base
			+ "/u.ashx?a=gfeng&m=checkShopUser&username=";// 1000&password=C001";
	// http://jmm.csdt.net/api/u.ashx?a=gfeng&m=checkShopUser
	// &userName=1000&password=123456&loginType=tencent&openid=&deviceType=android&gender=1&nickName=测试用户&address=上海市
	// 获取验证码
	public static String URL_GetCode = URL_Base
			+ "/u.ashx?a=gfeng&m=getSafetyCode&tel=";// &getType= 1重置 2注册

	// 注册
	public static String URL_Register = URL_Base
			+ "/u.ashx?a=gfeng&m=register&mobile=";// 18888888888&password=123456&sex=1&safetycode=123456

	// 获取个人资料
	public static String URL_Get_person_msg = URL_Base
			+ "/u.ashx?a=gfeng&m=getUserInfo&userId=";// 1000

	// 更新个人信息
	public static String URL_Modify_person_msg = URL_Base
			+ "/u.ashx?a=gfeng&m=changeUserInfo&userId=";// 1&birthday=2014-12-08&nickname=测试";

	// 修改密码
	public static String URL_Modify_Psd = URL_Base
			+ "/u.ashx?a=gfeng&m=changePwd&userId=";// 1&newPassword=123&oldPassword=123456

	// 验证码校验
	public static String URL_Cheak_Code = URL_Base
			+ "/u.ashx?m=getmcode&mobile=";// 1&mcode="

	// 密码重置
	public static String URL_ChongZhi_Psd = URL_Base
			+ "/u.ashx?m=resetPwd&mobile=";// 1&newPwd=";

	// 首页
	public static String URL_Shouye_1 = URL_Base
			+ "/p.ashx?a=gfeng&m=activelist&brandId=";

	// 首页—分类
	public static String URL_Shouye_fenlei = URL_Base
			+ "/p.ashx?m=brandlist&parentid=1";

	// 首页顶部图图片
	public static String URL_Shouye_DiBu = URL_Base + "/s.ashx?m=ad";

	// 分类
	public static String URL_FenLei = URL_Base
			+ "/p.ashx?m=catelist&parentid=0";

	// 分类列表
	public static String URL_GetList_FenLei = URL_Base
			+ "/p.ashx?m=getCateProduct&pageSize=30&pageNum=";// 1&id=1";

	// 发现
	public static String URL_Find = URL_GBase + "/wap/pb.html";// http://www.jumeimiao.com/api/p.ashx?m=getProductList&id=19&pageSize=&pageNum=&sort=&findGoods=1
	// 商品列表
	public static String URL_Goods_List = URL_Base
			+ "/p.ashx?m=getProductList&id=";// 11&sort= 0 默认 | 1 最新 | 2 热销 | 3
	// 价格升序 | 4 价格降序
	// 商品详情
	public static String URL_Goods_detail = URL_GBase
			+ "/wap/productdetail.html?a=";// pid=";//1&id=";

	public static String URL_Goods_detail_share = URL_GBase
			+ "/wx_wap/productdetail-wx.html?a=";// pid=";//1&id=";
	//活动分享连接
	public static String URL_Goods_list_share = URL_GBase
			+ "/wx_wap/productlist.html?id=";//&activeName=;
	
	// 品牌专店
	public static String URL_PinPai = URL_Base
			+ "/p.ashx?m=getBrandProduct&id=";// 12&pageNum=1";

	/**
	 * 上传用户图像
	 */

	public static String URL_UpUserPhoto = URL_Base + "/u.ashx?m=EditUserPhoto";
	/**
	 * 发布晒图
	 */
	public static String URL_SaveBaskOrder = URL_Base
			+ "/u.ashx?m=SaveBaskOrder";
	/**
	 * 晒图顶部轮播图片列表
	 */
	public static String URL_BannerList = URL_Base
			+ "/u.ashx?m=BannerList&Position=1";

	public static String URL_FirstBannerList = URL_Base
			+ "/u.ashx?m=BannerList&Position=2";

	/**
	 * 赞
	 */
	public static String URL_Zan = URL_Base + "/u.ashx?m=Zan";
	/**
	 * 取消赞
	 */
	public static String URL_CancelZan = URL_Base + "/u.ashx?m=CancelZan";
	/**
	 * 关注
	 */
	public static String URL_GuanZhu = URL_Base + "/u.ashx?m=AttentionExpert";

	/**
	 * 晒图列表
	 */
	public static String URL_BaskOrderList = URL_Base
			+ "/u.ashx?m=BaskOrderList";
	/**
	 * 关注列表
	 */
	public static String URL_GuanZhuList = URL_Base + "/u.ashx?m=AttentionList";

	/**
	 * 评论列表
	 */
	public static String URL_ReviewList = URL_Base + "/u.ashx?m=ReviewList";

	/**
	 * 发布评论
	 */
	public static String URL_CreateReview = URL_Base + "/u.ashx?m=CreateReview";

	// 搜索
	public static String URL_Search = URL_Base + "/p.ashx?m=searchProduct&key=";// &pageNum=";

	/**
	 * 优惠券
	 **/
	// 获取优惠券
	public static String URL_GetQuan = URL_Base + "/p.ashx?m=getCoupon&a=";// 1&userId=1&cid=1
																			// 领取官方优惠券
	// 获取优惠券new
	public static String URL_GetQuan_new = URL_Base
			+ "/u.ashx?m=bindConupon&conuponCode=";
	// 优惠券列表
	public static String URL_Quan_list = URL_Base
			+ "/p.ashx?m=getUserCoupon&uid=";// &type=0未使用 1已使用 2已过期

	// 优惠券列表new
	public static String URL_Quan_list_New = URL_Base
			+ "/u.ashx?m=customerConuponList&status=";// &status=0未使用 1已使用 2已过期

	// 购物车
	public static String URL_ShoppingCar = URL_GBase + "/wap/car.html?";
	// 添加购物车
	public static String URL_Add_ShoppingCar = URL_Base
			+ "/o.ashx?m=addlist&count=1&mobileType=3&pid=";// 1&spid=4&UserId=1&udid=123";
	// 商品id 规格 应用类型 机器号

	// 获取购物车商品数量
	public static String URL_GetShoppingCarNum = URL_Base
			+ "/o.ashx?m=getCartCount&UserId=";// 1&udid=";

	// 地址列表
	public static String URL_Get_AddressList = URL_Base
			+ "/o.ashx?m=getAddress&UserId=";

	// 删除单个地址
	public static String URL_Delete_Address = URL_Base
			+ "/o.ashx?m=delAddress&UserId=";// 1&addressId=1

	// 新建收货地址
	public static String URL_New_CreateAddress = URL_Base
			+ "/o.ashx?m=setAddress&UserId=";
	// http://jmm.csdt.net/api/o.ashx?m=setAddress&UserId=1&userName=测试用户2&tel=021-25632563&addressId=&IsDefault=0&accept=收货时间不限&Region=中国&province=&city=上海市&country=&detailAddress=xx区xx路xx弄xx号

	// 添加订单
	public static String URL_Add_Order = URL_Base
			+ "/o.ashx?m=submitOrder&UserId=";
	// 1&tel=&addressId=&couponId=&payMessage=&payWay=&orderAmount=&goodsAmount=
	// couponId 优惠券id payWay 支付方式 orderAmount订单总金额 goodsAmount商品总金额

	// 运费
	public static String URL_YunFei = URL_Base + "/o.ashx?m=Settlement&UserId=";// 1&addressId=1&udid=123";

	// 订单列表
	public static String URL_Order_List = URL_Base
			+ "/o.ashx?m=orderList&UserId=";// 1&orderState=0 0或空，默认查全部 | 1:待付款
	// | 2：待发货 | 3：待收货 | 4：待评价
	// 所有订单
	public static String URL_Order = URL_Base
			+ "/o.ashx?m=newOrderList&UserId=";

	// 订单详情
	public static String URL_Order_Deatil = URL_Base
			+ "/o.ashx?m=orderDes&UserId=";// 1&orderId=1

	// 取消订单
	public static String URL_QuXiao_Order = URL_Base
			+ "/o.ashx?m=orderCancel&UserId=";// 1&OrderId=";

	// 查询订单状态
	public static String URL_CheckOrderStatus = URL_Base
			+ "/o.ashx?m=queryOrderStatue&UserId=";// 1&OrderId=";

	// 添加收藏
	public static String URL_AddTo_Save = URL_Base
			+ "/o.ashx?m=favrite&UserId=";// 1&goodsId=1";

	// 收藏列表
	public static String URL_Save_List = URL_Base
			+ "/o.ashx?m=favriteList&UserId=";// 1

	// 删除收藏
	public static String URL_Delete_Save = URL_Base
			+ "/o.ashx?m=delFavrite&UserId=";// 1&favriteId=

	// 收货
	public static String URL_ok_Order = URL_Base + "/o.ashx?m=Received&UserId=";// 1&orderId=&productId=

	// 退货
	public static String URL_TuiHuo = URL_Base + "/o.ashx?m=BackGoods&UserId=";// &OrderId=&goodsId=&reasonId=&remark=

	// 意见反馈
	public static String URL_Advice = URL_Base + "/p.ashx?m=advice&userId=";// &info=";

	// 版本更新
	public static String URL_Modify_Apk = URL_Base
			+ "/home.ashx?m=updateVersion";

	// 关于我们
	public static String URL_AboutUs = URL_GBase + "/wap/about.html";

	// 物流
	public static String URL_WuLiu = "http://m.kuaidi100.com/index_all.html?type=";// ";//shentong&postid=968195151333";

	// 客服
	public static String URL_KeFu = URL_Base + "/u.ashx?&m=getToken&UserId=";

	// 官方优惠券
	public static String URL_Guan_Quan = URL_Base
			+ "/p.ashx?m=getEnableCoupon&UserId=";

	// 官方优惠券new
	public static String URL_Guan_Quan_New = URL_Base
			+ "/u.ashx?m=usableConuponList&UserId=";

	// 退出登录
	// public static String
	// URL_Out_apk=URL_Base+"u.ashx?a=1&userId=1&m=shopLoginOut";
	// 头像
	// public static String
	// URL_Get_TouXiang="http://pic1.vip.com/upload/merchandise/252891/LUXLEAD-DCO2072400-3_473x598_80.jpg";

	// 支付宝通知后台接口
	public static String URL_notify = URL_GBase + "/pay/alipay/notify_url.aspx";
	// 微信支付
	public static String URL_Wx_pay = URL_GBase
			+ "/pay/wxpay/WXPay_pay.aspx?body=";
}