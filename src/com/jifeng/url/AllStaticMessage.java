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
	 * ����ɹ���󣬷��ص�����
	 */
	public static String title = "";
	public static String url = "";
	public static String imgurl = "";

	// ������������true�������汾Ϊfalse
	public static final boolean DEVELOPER_MODE = false;
	static final String authValidate = "2bd6854b72bc11bed0fe";

	/**
	 * ΢��
	 */
	// APP_ID �滻Ϊ���Ӧ�ôӹٷ���վ���뵽�ĺϷ�appId
	public static final String APP_ID = "wx4625de2e23841ec2";// wx4625de2e23841ec2

	// APP_ID ��Ӧ��֧����Կ
	public static final String APP_KEY = "2bd6854b72bc11bed0fee951d2d001e3";// 2bd6854b72bc11bed0fee951d2d001e3

	// APP_MCH ΢�Źٷ���partnerId
	public static final String APP_MCH = "1230268701";// 1230268701

	// ���߿ͷ�
	// public static final String KeFu_key = "vnroth0kr5hqo";
	public static final String KeFu_key = "m7ua80gbur0lm";
	public static String KeFu_Token = "";
	// public static final String KeFu_seviceId = "KEFU1421983315052";
	public static final String KeFu_seviceId = "KEFU1428905067406";
	/**
	 * ����
	 */

	// �ٷ��Ż�ȯѡ��
	public static String guanquanid = "";
	public static String guanquan_name = "";
	public static String guanquan_value = "";

	// ���ﳵ��Ʒ������
	public static int Car_num = 0;
	// ���ﳵͼ��仯
	public static boolean ShoppingCar = false;

	// loading��־
	public static boolean LoadingShowFlag = true;

	// mypay�����жϱ�־
	public static boolean MyPayBack = false;

	// ��ַ�½���ɷ����б�ˢ��
	public static boolean AddressListFlag = false;

	// ����ѡ���ַ���ر�ǣ�����
	public static boolean JieSuan_Select_Address = false;
	public static JSONObject mJsonObject_select_address = null;

	// ��������¼�����ж�
	// public static boolean otherFlag=false;

	// ͷ��
	// public static Bitmap mBitmap=null;
	// �����¼��Ϣ
	public static String User_Name = null;
	public static String User_Psd = null;
	public static String Login_Flag = "";// �ж��Ƿ��¼�ı��
	public static String User_Id = "";
	public static String User_JiFen = "";
	public static String User_NickName = "";

	// ����ҳ��
	public static boolean Back_to_XianShiTm = false;
	public static boolean Back_to_ShoppingCar = false;
	public static boolean Back_to_Classion = false;
	public static boolean Back_to_Find = false;
	public static boolean Back_to_ZhangHu = false;

	// ��������¼
	public static String OpenId = "";
	public static String NickName = "";
	public static String Address = "";
	public static String Gender = "";
	public static String userImage = "";

	// ����״̬
	public static String[] zhifu = { "δ֧��", "�ѳɹ�", "�ѷ���", "���ջ�", "�����˻�",
			"�������", "�˿����" };

	public static String[] tuikuanyuanying = { "δ��Լ��ʱ�䷢��", "�ջ�����Ϣ����", "�����ظ�",
			"��������", "��������", "�۸�ԭ��", "����" };

	// ���ж����Ƿ�ˢ��
	public static boolean OrderFormFlag = false;

	// ��������ҳ���Ƿ�ˢ��
	public static boolean orderDetailFlag = false;
	// �ж϶���״̬
	public static String OrderId = "";
	public static String OrderPrice = "";
	public static String OrderStatus = "";

	// ΢��֧���������
	public static String WxOrder = "";
	// ֧����֧���������
	public static String zfb_Order = "";
	/**
	 * �ӿڲ���
	 */
	public static String URL_GBase = "http://www.jumeimiao.com";
	//public static String URL_GBase = "http://192.168.2.120:8010";

	// public static String URL_GBase = "http://wwwpre.jumeimiao.com";
	// public static String URL_GBase = "http://192.168.2.134:8010";
	public static String URL_Base = URL_GBase + "/api";
	// ����
	public static String class_url = URL_Base + "/p.ashx?m=category";
	// ����item
	public static String class_url_item = URL_Base
			+ "/p.ashx?m=categoryProduct&categoryId=";
	// ���ݻid��ȡ�����
	public static String active_url = URL_Base
			+ "/p.ashx?m=activedetail&activeId=";

	// ��ȡͼƬ��֤��
	public static String URL_GetImgCode = URL_GBase + "/getCode.aspx?code=";
	// ��¼
	public static String URL_Login = URL_Base
			+ "/u.ashx?a=gfeng&m=checkShopUser&username=";// 1000&password=C001";
	// http://jmm.csdt.net/api/u.ashx?a=gfeng&m=checkShopUser
	// &userName=1000&password=123456&loginType=tencent&openid=&deviceType=android&gender=1&nickName=�����û�&address=�Ϻ���
	// ��ȡ��֤��
	public static String URL_GetCode = URL_Base
			+ "/u.ashx?a=gfeng&m=getSafetyCode&tel=";// &getType= 1���� 2ע��

	// ע��
	public static String URL_Register = URL_Base
			+ "/u.ashx?a=gfeng&m=register&mobile=";// 18888888888&password=123456&sex=1&safetycode=123456

	// ��ȡ��������
	public static String URL_Get_person_msg = URL_Base
			+ "/u.ashx?a=gfeng&m=getUserInfo&userId=";// 1000

	// ���¸�����Ϣ
	public static String URL_Modify_person_msg = URL_Base
			+ "/u.ashx?a=gfeng&m=changeUserInfo&userId=";// 1&birthday=2014-12-08&nickname=����";

	// �޸�����
	public static String URL_Modify_Psd = URL_Base
			+ "/u.ashx?a=gfeng&m=changePwd&userId=";// 1&newPassword=123&oldPassword=123456

	// ��֤��У��
	public static String URL_Cheak_Code = URL_Base
			+ "/u.ashx?m=getmcode&mobile=";// 1&mcode="

	// ��������
	public static String URL_ChongZhi_Psd = URL_Base
			+ "/u.ashx?m=resetPwd&mobile=";// 1&newPwd=";

	// ��ҳ
	public static String URL_Shouye_1 = URL_Base
			+ "/p.ashx?a=gfeng&m=activelist&brandId=";

	// ��ҳ������
	public static String URL_Shouye_fenlei = URL_Base
			+ "/p.ashx?m=brandlist&parentid=1";

	// ��ҳ����ͼͼƬ
	public static String URL_Shouye_DiBu = URL_Base + "/s.ashx?m=ad";

	// ����
	public static String URL_FenLei = URL_Base
			+ "/p.ashx?m=catelist&parentid=0";

	// �����б�
	public static String URL_GetList_FenLei = URL_Base
			+ "/p.ashx?m=getCateProduct&pageSize=30&pageNum=";// 1&id=1";

	// ����
	public static String URL_Find = URL_GBase + "/wap/pb.html";// http://www.jumeimiao.com/api/p.ashx?m=getProductList&id=19&pageSize=&pageNum=&sort=&findGoods=1
	// ��Ʒ�б�
	public static String URL_Goods_List = URL_Base
			+ "/p.ashx?m=getProductList&id=";// 11&sort= 0 Ĭ�� | 1 ���� | 2 ���� | 3
	// �۸����� | 4 �۸���
	// ��Ʒ����
	public static String URL_Goods_detail = URL_GBase
			+ "/wap/productdetail.html?a=";// pid=";//1&id=";

	public static String URL_Goods_detail_share = URL_GBase
			+ "/wx_wap/productdetail-wx.html?a=";// pid=";//1&id=";
	//���������
	public static String URL_Goods_list_share = URL_GBase
			+ "/wx_wap/productlist.html?id=";//&activeName=;
	
	// Ʒ��ר��
	public static String URL_PinPai = URL_Base
			+ "/p.ashx?m=getBrandProduct&id=";// 12&pageNum=1";

	/**
	 * �ϴ��û�ͼ��
	 */

	public static String URL_UpUserPhoto = URL_Base + "/u.ashx?m=EditUserPhoto";
	/**
	 * ����ɹͼ
	 */
	public static String URL_SaveBaskOrder = URL_Base
			+ "/u.ashx?m=SaveBaskOrder";
	/**
	 * ɹͼ�����ֲ�ͼƬ�б�
	 */
	public static String URL_BannerList = URL_Base
			+ "/u.ashx?m=BannerList&Position=1";

	public static String URL_FirstBannerList = URL_Base
			+ "/u.ashx?m=BannerList&Position=2";

	/**
	 * ��
	 */
	public static String URL_Zan = URL_Base + "/u.ashx?m=Zan";
	/**
	 * ȡ����
	 */
	public static String URL_CancelZan = URL_Base + "/u.ashx?m=CancelZan";
	/**
	 * ��ע
	 */
	public static String URL_GuanZhu = URL_Base + "/u.ashx?m=AttentionExpert";

	/**
	 * ɹͼ�б�
	 */
	public static String URL_BaskOrderList = URL_Base
			+ "/u.ashx?m=BaskOrderList";
	/**
	 * ��ע�б�
	 */
	public static String URL_GuanZhuList = URL_Base + "/u.ashx?m=AttentionList";

	/**
	 * �����б�
	 */
	public static String URL_ReviewList = URL_Base + "/u.ashx?m=ReviewList";

	/**
	 * ��������
	 */
	public static String URL_CreateReview = URL_Base + "/u.ashx?m=CreateReview";

	// ����
	public static String URL_Search = URL_Base + "/p.ashx?m=searchProduct&key=";// &pageNum=";

	/**
	 * �Ż�ȯ
	 **/
	// ��ȡ�Ż�ȯ
	public static String URL_GetQuan = URL_Base + "/p.ashx?m=getCoupon&a=";// 1&userId=1&cid=1
																			// ��ȡ�ٷ��Ż�ȯ
	// ��ȡ�Ż�ȯnew
	public static String URL_GetQuan_new = URL_Base
			+ "/u.ashx?m=bindConupon&conuponCode=";
	// �Ż�ȯ�б�
	public static String URL_Quan_list = URL_Base
			+ "/p.ashx?m=getUserCoupon&uid=";// &type=0δʹ�� 1��ʹ�� 2�ѹ���

	// �Ż�ȯ�б�new
	public static String URL_Quan_list_New = URL_Base
			+ "/u.ashx?m=customerConuponList&status=";// &status=0δʹ�� 1��ʹ�� 2�ѹ���

	// ���ﳵ
	public static String URL_ShoppingCar = URL_GBase + "/wap/car.html?";
	// ��ӹ��ﳵ
	public static String URL_Add_ShoppingCar = URL_Base
			+ "/o.ashx?m=addlist&count=1&mobileType=3&pid=";// 1&spid=4&UserId=1&udid=123";
	// ��Ʒid ��� Ӧ������ ������

	// ��ȡ���ﳵ��Ʒ����
	public static String URL_GetShoppingCarNum = URL_Base
			+ "/o.ashx?m=getCartCount&UserId=";// 1&udid=";

	// ��ַ�б�
	public static String URL_Get_AddressList = URL_Base
			+ "/o.ashx?m=getAddress&UserId=";

	// ɾ��������ַ
	public static String URL_Delete_Address = URL_Base
			+ "/o.ashx?m=delAddress&UserId=";// 1&addressId=1

	// �½��ջ���ַ
	public static String URL_New_CreateAddress = URL_Base
			+ "/o.ashx?m=setAddress&UserId=";
	// http://jmm.csdt.net/api/o.ashx?m=setAddress&UserId=1&userName=�����û�2&tel=021-25632563&addressId=&IsDefault=0&accept=�ջ�ʱ�䲻��&Region=�й�&province=&city=�Ϻ���&country=&detailAddress=xx��xx·xxŪxx��

	// ��Ӷ���
	public static String URL_Add_Order = URL_Base
			+ "/o.ashx?m=submitOrder&UserId=";
	// 1&tel=&addressId=&couponId=&payMessage=&payWay=&orderAmount=&goodsAmount=
	// couponId �Ż�ȯid payWay ֧����ʽ orderAmount�����ܽ�� goodsAmount��Ʒ�ܽ��

	// �˷�
	public static String URL_YunFei = URL_Base + "/o.ashx?m=Settlement&UserId=";// 1&addressId=1&udid=123";

	// �����б�
	public static String URL_Order_List = URL_Base
			+ "/o.ashx?m=orderList&UserId=";// 1&orderState=0 0��գ�Ĭ�ϲ�ȫ�� | 1:������
	// | 2�������� | 3�����ջ� | 4��������
	// ���ж���
	public static String URL_Order = URL_Base
			+ "/o.ashx?m=newOrderList&UserId=";

	// ��������
	public static String URL_Order_Deatil = URL_Base
			+ "/o.ashx?m=orderDes&UserId=";// 1&orderId=1

	// ȡ������
	public static String URL_QuXiao_Order = URL_Base
			+ "/o.ashx?m=orderCancel&UserId=";// 1&OrderId=";

	// ��ѯ����״̬
	public static String URL_CheckOrderStatus = URL_Base
			+ "/o.ashx?m=queryOrderStatue&UserId=";// 1&OrderId=";

	// ����ղ�
	public static String URL_AddTo_Save = URL_Base
			+ "/o.ashx?m=favrite&UserId=";// 1&goodsId=1";

	// �ղ��б�
	public static String URL_Save_List = URL_Base
			+ "/o.ashx?m=favriteList&UserId=";// 1

	// ɾ���ղ�
	public static String URL_Delete_Save = URL_Base
			+ "/o.ashx?m=delFavrite&UserId=";// 1&favriteId=

	// �ջ�
	public static String URL_ok_Order = URL_Base + "/o.ashx?m=Received&UserId=";// 1&orderId=&productId=

	// �˻�
	public static String URL_TuiHuo = URL_Base + "/o.ashx?m=BackGoods&UserId=";// &OrderId=&goodsId=&reasonId=&remark=

	// �������
	public static String URL_Advice = URL_Base + "/p.ashx?m=advice&userId=";// &info=";

	// �汾����
	public static String URL_Modify_Apk = URL_Base
			+ "/home.ashx?m=updateVersion";

	// ��������
	public static String URL_AboutUs = URL_GBase + "/wap/about.html";

	// ����
	public static String URL_WuLiu = "http://m.kuaidi100.com/index_all.html?type=";// ";//shentong&postid=968195151333";

	// �ͷ�
	public static String URL_KeFu = URL_Base + "/u.ashx?&m=getToken&UserId=";

	// �ٷ��Ż�ȯ
	public static String URL_Guan_Quan = URL_Base
			+ "/p.ashx?m=getEnableCoupon&UserId=";

	// �ٷ��Ż�ȯnew
	public static String URL_Guan_Quan_New = URL_Base
			+ "/u.ashx?m=usableConuponList&UserId=";

	// �˳���¼
	// public static String
	// URL_Out_apk=URL_Base+"u.ashx?a=1&userId=1&m=shopLoginOut";
	// ͷ��
	// public static String
	// URL_Get_TouXiang="http://pic1.vip.com/upload/merchandise/252891/LUXLEAD-DCO2072400-3_473x598_80.jpg";

	// ֧����֪ͨ��̨�ӿ�
	public static String URL_notify = URL_GBase + "/pay/alipay/notify_url.aspx";
	// ΢��֧��
	public static String URL_Wx_pay = URL_GBase
			+ "/pay/wxpay/WXPay_pay.aspx?body=";
}