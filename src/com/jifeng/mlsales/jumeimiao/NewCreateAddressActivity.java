package com.jifeng.mlsales.jumeimiao;

import java.util.ArrayList;
import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.framework.ShareSDK;

import com.jifeng.city.DBManager;
import com.jifeng.city.MyListItem;
import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class NewCreateAddressActivity extends BaseActivity implements
		OnWheelChangedListener {
	private EditText mText_Name, mText_Phone, mText_Detail;
	// private Button mBtn_Ok;//mBtn_Time,
	private ImageView mImageView;
	// private String[] time;
	private String province = null, city = null, district = null, name = null,
			phonenum = null, detail = null, isDefault = "0", sanjiId = "";// addtime
																			// =
	private DBManager dbm_province, dbm_city, dbm_district;
	private SQLiteDatabase db_province, db_city, db_district; // null,
	private int themeCheckedId = 0;
	private boolean imgFlag = false;
	private LoadingDialog dialog;
	private String id;
	private WheelView mViewProvince;
	private WheelView mViewCity;
	private WheelView mViewDistrict;
	private Button mBtnConfirm;
	String[] provicedata, citydata, districtdata;
	List<MyListItem> listProvice, listCity, listDistrict;
	private Button mBtn_Provice, mBtn_City, mBtn_District;
	View mView;
	AlertDialog alertDialog;
	boolean firstIn = true;

	private PopupWindow popupWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_address);
		// time = getResources().getStringArray(R.array.shouhuoTime);
		dialog = new LoadingDialog(this);
		findView();
		initData();
	}

	// 查找控件
	private void findView() {
		mText_Name = (EditText) findViewById(R.id.create_address_name);
		mText_Phone = (EditText) findViewById(R.id.create_address_phone);
		mText_Detail = (EditText) findViewById(R.id.create_address_detail);
		// mBtn_Time = (Button) findViewById(R.id.create_address_time);
		// mBtn_Ok = (Button) findViewById(R.id.create_address_ok);
		mImageView = (ImageView) findViewById(R.id.create_address_isdefault);
		mBtn_Provice = (Button) findViewById(R.id.create_address_province);
		mBtn_City = (Button) findViewById(R.id.create_address_city);
		mBtn_District = (Button) findViewById(R.id.create_address_country);
		mView = LayoutInflater.from(NewCreateAddressActivity.this).inflate(
				R.layout.address_select, null);

		mViewProvince = (WheelView) mView.findViewById(R.id.id_province);
		mViewCity = (WheelView) mView.findViewById(R.id.id_city);
		mViewDistrict = (WheelView) mView.findViewById(R.id.id_district);
		mBtnConfirm = (Button) mView.findViewById(R.id.wv_ok);
		// setUpListener();
		// setUpData();

		mBtnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// alertDialog.cancel();
				popupWindow.dismiss();
				province = listProvice.get(mViewProvince.getCurrentItem())
						.getName().toString().trim();
				city = listCity.get(mViewCity.getCurrentItem()).getName()
						.toString().trim();
				if (listDistrict != null
						&& listDistrict.size() > mViewDistrict.getCurrentItem()) {
					district = listDistrict.get(mViewDistrict.getCurrentItem())
							.getName().toString().trim();
					sanjiId = listDistrict.get(mViewDistrict.getCurrentItem())
							.getPcode().toString().trim();
				} else {
					district = "";
				}

				// mBtn_Provice.setText(mCurrentProviceName + " "
				// + mCurrentCityName + " " + mCurrentDistrictName);
				mBtn_Provice.setText(province + " " + city + " " + district);
				// mBtn_City.setText(city);
				// mBtn_District.setText(district);
			}
		});

	}

	private void registerListen() {
		// 添加change事件
		mViewProvince.addChangingListener(this);
		// 添加change事件
		mViewCity.addChangingListener(this);
		// 添加change事件
		mViewDistrict.addChangingListener(this);
		initSpinner1();
		// 设置可见条目数量
		mViewProvince.setVisibleItems(7);
		mViewCity.setVisibleItems(7);
		mViewDistrict.setVisibleItems(7);
	}

	// private void setUpListener() {
	// // 添加change事件
	// mViewProvince.addChangingListener(this);
	// // 添加change事件
	// mViewCity.addChangingListener(this);
	// // 添加change事件
	// mViewDistrict.addChangingListener(this);
	//
	// }

	// private void setUpData() {
	// initProvinceDatas();
	// mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(
	// NewCreateAddressActivity.this, mProvinceDatas));
	// // 设置可见条目数量
	// mViewProvince.setVisibleItems(7);
	// mViewCity.setVisibleItems(7);
	// mViewDistrict.setVisibleItems(7);
	// updateCities();
	// updateAreas();
	// }

	/*
	 * 初始化数据（修改时）
	 */
	private void initData() {
		if (!getIntent().getStringExtra("data").equals("")) {
			try {
				JSONObject jsonObject = new JSONObject(getIntent()
						.getStringExtra("data").toString());
				mText_Name.setText(jsonObject.getString("TrueName"));
				mText_Phone.setText(jsonObject.getString("PhoneTel"));
				mText_Detail.setText(jsonObject.getString("DetailAddress"));
				// mBtn_Time.setText(jsonObject.getString("GoodsTime"));
				// addtime = jsonObject.getString("GoodsTime");
				province = jsonObject.getString("Province");
				city = jsonObject.getString("City");
				district = jsonObject.getString("Country");
				sanjiId = jsonObject.getString("CountryCode");
				mBtn_Provice.setText(province + " " + city + " " + district);
				// mBtn_City.setText(city);
				// mBtn_District.setText(district);

				if (jsonObject.getString("IsDefault").toString().equals("1")) {
					isDefault = "1";
					imgFlag = true;
					mImageView.setImageDrawable(getResources().getDrawable(
							R.drawable.register_select_2));
				} else {
					isDefault = "0";
					imgFlag = false;
					mImageView.setImageDrawable(getResources().getDrawable(
							R.drawable.register_select_1));
				}
				id = jsonObject.getString("Id");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	// //xml注册点击事件的实现
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.add_address_back:// 返回
			finish();
			break;
		case R.id.create_address_province:

		case R.id.create_address_city:

		case R.id.create_address_country:
			registerListen();
			popupWindowShow();
			// if (firstIn) {
			// AlertDialog.Builder builder = new AlertDialog.Builder(
			// NewCreateAddressActivity.this);
			// alertDialog = builder.create();
			// alertDialog.show();
			// WindowManager manager = getWindowManager();
			// int height = manager.getDefaultDisplay().getHeight();
			// int width = manager.getDefaultDisplay().getWidth();
			// alertDialog.getWindow().setLayout((width - 60), height / 2);
			// alertDialog.getWindow().setContentView(mView);
			// firstIn = false;
			// } else {
			// alertDialog.show();
			// }
			break;

		case R.id.create_address_isdefault:
			if (imgFlag) {
				isDefault = "0";
				imgFlag = false;
				mImageView.setImageDrawable(getResources().getDrawable(
						R.drawable.register_select_1));
			} else {
				imgFlag = true;
				isDefault = "1";
				mImageView.setImageDrawable(getResources().getDrawable(
						R.drawable.register_select_2));
			}
			break;
		case R.id.create_address_ok_2:
		case R.id.create_address_ok:

			name = mText_Name.getText().toString().replace(" ", "").trim();
			phonenum = mText_Phone.getText().toString().replace(" ", "").trim();
			detail = mText_Detail.getText().toString().replace(" ", "").trim();
			if (name == null || name.equals("")) {
				Toast.makeText(NewCreateAddressActivity.this, "请输入收货人姓名", 500)
						.show();
				return;
			}
			if (phonenum == null || phonenum.equals("")) {
				Toast.makeText(NewCreateAddressActivity.this, "请输入收货人联系电话", 500)
						.show();
				return;
			}
			if (phonenum.length() < 7 || phonenum.length() > 11) {
				Toast.makeText(NewCreateAddressActivity.this, "收货人联系电话格式错误",
						500).show();
				return;
			}
			// if (addtime == null || addtime.equals("")) {
			// Toast.makeText(NewCreateAddressActivity.this, "请选择收货时间", 500)
			// .show();
			// return;
			// }
			// if (province == null || province.equals("")) {
			// Toast.makeText(NewCreateAddressActivity.this, "请选择省份", 500)
			// .show();
			// return;
			// }
			if (mBtn_Provice.getText().toString().trim() == null
					|| mBtn_Provice.getText().toString().trim().equals("")) {
				Toast.makeText(NewCreateAddressActivity.this, "请选择所在地区", 500)
						.show();
				return;
			}
			// if (city == null || city.equals("")) {
			// Toast.makeText(NewCreateAddressActivity.this, "请选择城市", 500)
			// .show();
			// return;
			// }
			// if (district == null || district.equals("")) {
			// Toast.makeText(NewCreateAddressActivity.this, "请选择县区", 500)
			// .show();
			// return;
			// }
			if (detail == null || detail.equals("")) {
				Toast.makeText(NewCreateAddressActivity.this, "请输入详细地址", 500)
						.show();
				return;
			}
			if (getIntent().getStringExtra("data").equals("")) {
				getData("");
			} else {
				getData(id);
			}

			break;
		default:
			break;
		}
	}

	private void getData(String id) {
		dialog.loading();
		if (province.equals("上海市")) {
			city = "上海市";
		}
		// String url = AllStaticMessage.URL_New_CreateAddress
		// + AllStaticMessage.User_Id + "&userName=" + name + "&tel="
		// + phonenum + "&accept=" + "" + "&province="
		// + mCurrentProviceName + "&city=" + mCurrentCityName
		// + "&country=" + mCurrentDistrictName + "&detailAddress="
		// + detail + "&IsDefault=" + isDefault + "&addressId=" + id
		// + "&Region=" + "&zoneCode=" + mCurrentZipCode;

		String url = AllStaticMessage.URL_New_CreateAddress
				+ AllStaticMessage.User_Id + "&userName=" + name + "&tel="
				+ phonenum + "&accept=" + "" + "&province=" + province
				+ "&city=" + city + "&country=" + district + "&detailAddress="
				+ detail + "&IsDefault=" + isDefault + "&addressId=" + id
				+ "&Region=" + "&zoneCode=" + sanjiId;

		HttpUtil.get(url, NewCreateAddressActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").equals("true")) {
								Toast.makeText(
										NewCreateAddressActivity.this,
										response.getString("Results")
												.toString(), 500).show();
								AllStaticMessage.AddressListFlag = true;
								setResult(RESULT_CANCELED);
								finish();
							} else {
								Toast.makeText(
										NewCreateAddressActivity.this,
										response.getString("Results")
												.toString(), 500).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						if (dialog != null) {
							dialog.stop();
						}
					}

					@Override
					public void onStart() {
						super.onStart();
						// 请求开始
					}

					@Override
					public void onFinish() {
						super.onFinish();
						// 请求结束
					}

					@Override
					public void onFailure(int statusCode,
							@SuppressWarnings("deprecation") Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						// 错误返回JSONObject
						if (dialog != null) {
							dialog.stop();
						}
					}
				});
	}

	// /**
	// * 根据当前的省，更新市WheelView的信息
	// */
	// private void updateCities() {
	// int pCurrent = mViewProvince.getCurrentItem();
	// mCurrentProviceName = mProvinceDatas[pCurrent];
	// String[] cities = mCitisDatasMap.get(mCurrentProviceName);
	// if (cities == null) {
	// cities = new String[] { "" };
	// }
	// mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
	// mViewCity.setCurrentItem(0);
	// updateAreas();
	// }
	//
	// /**
	// * 根据当前的市，更新区WheelView的信息
	// */
	// private void updateAreas() {
	// int pCurrent = mViewCity.getCurrentItem();
	// mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
	// String[] areas = mDistrictDatasMap.get(mCurrentCityName);
	//
	// if (areas == null) {
	// areas = new String[] { "" };
	// }
	// mViewDistrict
	// .setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
	// mViewDistrict.setCurrentItem(0);
	// updateDistrict();
	// }
	//
	// /**
	// * 根据当前的区，更新邮编WheelView的信息
	// */
	// private void updateDistrict() {
	// int pCurrent = mViewDistrict.getCurrentItem();
	// mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[pCurrent];
	// mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
	//
	// }

	// @Override
	// public void onChanged(WheelView wheel, int oldValue, int newValue) {
	// if (wheel == mViewProvince) {
	// updateCities();
	// } else if (wheel == mViewCity) {
	// updateAreas();
	// } else if (wheel == mViewDistrict) {
	// mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
	// mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
	// }
	// }
	public void initSpinner1() {
		dbm_province = new DBManager(this);
		dbm_province.openDatabase();
		db_province = dbm_province.getDatabase();
		if (listProvice != null) {
			listProvice.clear();
		}
		listProvice = new ArrayList<MyListItem>();
		try {

			String sql = "select * from T_Province";
			Cursor cursor = db_province.rawQuery(sql, null);
			startManagingCursor(cursor);
			cursor.moveToFirst();
			while (!cursor.isLast()) {
				String code = cursor
						.getString(cursor.getColumnIndex("ProSort"));
				// String name =
				// cursor.getString(cursor.getColumnIndex("ProName"));
				byte bytes[] = cursor.getBlob(0);
				String name = new String(bytes, "UTF-8");
				MyListItem myListItem = new MyListItem();
				myListItem.setName(name);
				myListItem.setPcode(code);
				listProvice.add(myListItem);
				cursor.moveToNext();
			}
			String code = cursor.getString(cursor.getColumnIndex("ProSort"));
			// String name = cursor.getString(cursor.getColumnIndex("ProName"));
			byte bytes[] = cursor.getBlob(0);
			String name = new String(bytes, "UTF-8");
			MyListItem myListItem = new MyListItem();
			myListItem.setName(name);
			myListItem.setPcode(code);
			listProvice.add(myListItem);

		} catch (Exception e) {
			e.toString();
		}
		dbm_province.closeDatabase();
		db_province.close();
		provicedata = new String[listProvice.size()];
		for (int i = 0; i < listProvice.size(); i++) {
			provicedata[i] = listProvice.get(i).getName();
		}
		mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(
				NewCreateAddressActivity.this, provicedata));
		mViewProvince.setCurrentItem(0);
		initSpinner2(listProvice.get(0).getPcode());

	}

	/**
	 * 根据当前的省，更新市WheelView的信息
	 */
	public void initSpinner2(String pcode) {
		dbm_city = new DBManager(this);
		dbm_city.openDatabase();
		db_city = dbm_city.getDatabase();
		if (listCity != null) {
			listCity.clear();
		}
		listCity = new ArrayList<MyListItem>();

		try {
			String sql = "select * from T_City where ProID='" + pcode + "'";
			Cursor cursor = db_city.rawQuery(sql, null);
			startManagingCursor(cursor);
			cursor.moveToFirst();
			while (!cursor.isLast()) {
				String code = cursor.getString(cursor
						.getColumnIndex("CitySort"));
				// String name =
				// cursor.getString(cursor.getColumnIndex("CityName"));
				byte bytes[] = cursor.getBlob(0);
				String name = new String(bytes, "UTF-8");
				MyListItem myListItem = new MyListItem();
				myListItem.setName(name);
				myListItem.setPcode(code);
				listCity.add(myListItem);
				cursor.moveToNext();
			}
			String code = cursor.getString(cursor.getColumnIndex("CitySort"));
			// String name =
			// cursor.getString(cursor.getColumnIndex("CityName"));
			byte bytes[] = cursor.getBlob(0);
			String name = new String(bytes, "UTF-8");
			MyListItem myListItem = new MyListItem();
			myListItem.setName(name);
			myListItem.setPcode(code);
			listCity.add(myListItem);

		} catch (Exception e) {
		}

		dbm_city.closeDatabase();
		db_city.close();
		if (citydata != null) {
			citydata = null;
		}
		citydata = new String[listCity.size()];
		for (int i = 0; i < listCity.size(); i++) {
			citydata[i] = listCity.get(i).getName();
		}
		mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(
				NewCreateAddressActivity.this, citydata));
		mViewCity.setCurrentItem(0);
		initSpinner3(listCity.get(0).getPcode());
	}

	/**
	 * 根据当前的市，更新县WheelView的信息
	 */
	public void initSpinner3(String pcode) {
		dbm_district = new DBManager(this);
		dbm_district.openDatabase();
		db_district = dbm_district.getDatabase();
		if (listDistrict != null) {
			listDistrict.clear();
		}
		listDistrict = new ArrayList<MyListItem>();
		try {
			String sql = "select * from T_Zone where CityID='" + pcode + "'";
			Cursor cursor = db_district.rawQuery(sql, null);
			startManagingCursor(cursor);// 获取的Cursor对象交给Activity管理，这样Cursor的生命周期便能和Activity自动同步
			cursor.moveToFirst();
			while (!cursor.isLast()) {
				String code = cursor.getString(cursor.getColumnIndex("ZoneID"));
				// String name =
				// cursor.getString(cursor.getColumnIndex("ZoneName"));
				byte bytes[] = cursor.getBlob(1);
				String name = new String(bytes, "UTF-8");
				MyListItem myListItem = new MyListItem();
				myListItem.setName(name);
				myListItem.setPcode(code);
				listDistrict.add(myListItem);
				cursor.moveToNext();
			}
			String code = cursor.getString(cursor.getColumnIndex("ZoneID"));
			// String name =
			// cursor.getString(cursor.getColumnIndex("ZoneName"));
			byte bytes[] = cursor.getBlob(1);
			String name = new String(bytes, "UTF-8");
			MyListItem myListItem = new MyListItem();
			myListItem.setName(name);
			myListItem.setPcode(code);
			listDistrict.add(myListItem);

		} catch (Exception e) {

		}
		dbm_district.closeDatabase();
		db_district.close();
		if (districtdata != null) {
			districtdata = null;
		}
		districtdata = new String[listDistrict.size()];
		for (int i = 0; i < listDistrict.size(); i++) {
			districtdata[i] = listDistrict.get(i).getName();
		}
		mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(
				NewCreateAddressActivity.this, districtdata));
		mViewDistrict.setCurrentItem(0);
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		if (wheel == mViewProvince) {
			if (listProvice != null) {
				initSpinner2(listProvice.get(wheel.getCurrentItem()).getPcode());
			}

		} else if (wheel == mViewCity) {
			if (listCity != null) {
				initSpinner3(listCity.get(wheel.getCurrentItem()).getPcode());
			}

		} else if (wheel == mViewDistrict) {
			if (listDistrict != null) {
				district = listDistrict.get(mViewDistrict.getCurrentItem())
						.getName();
			}
		}
	}

	private void popupWindowShow() {
		if (firstIn) {
			popupWindow = new PopupWindow(mView);
			popupWindow.setWidth(LayoutParams.MATCH_PARENT);
			popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
			popupWindow.setAnimationStyle(R.style.AnimBottomPopup);
			ColorDrawable dw = new ColorDrawable(0xb0000000);
			popupWindow.setBackgroundDrawable(dw);
			popupWindow.setOutsideTouchable(true);

			popupWindow.showAtLocation(findViewById(R.id.parent),
					Gravity.BOTTOM, 0, 0);
			firstIn = false;
		} else {
			popupWindow.showAtLocation(findViewById(R.id.parent),
					Gravity.BOTTOM, 0, 0);
		}
	}

	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		super.onDestroy();
		// dialog = null;
		// mText_Name = null;
		// mText_Phone = null;
		// mText_Detail = null;
		// // mBtn_Time= null;
		// // mBtn_Ok= null;
		// mImageView = null;
		// // time= null;
		// province = null;
		// city = null;
		// district = null;
		// name = null;
		// phonenum = null;
		// // addtime =null;
		// detail = null;
		// isDefault = null;
		// id = null;
		// mViewProvince = null;
		// mViewCity = null;
		// mViewDistrict = null;
		// mBtnConfirm = null;
		// provicedata = null;
		// citydata = null;
		// districtdata = null;
		// listProvice = null;
		// listCity = null;
		// listDistrict = null;
		// mBtn_Provice = null;
		// mBtn_City = null;
		// mBtn_District = null;
		// mView = null;
		// // alertDialog = null;
		// setContentView(R.layout.view_null);
		// this.finish();
		// System.gc();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
