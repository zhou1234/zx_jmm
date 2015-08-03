package com.jifeng.mlsales.fragment;

import android.support.v4.app.Fragment;

abstract class BaseFragment extends Fragment {

	/** Fragment��ǰ״̬�Ƿ�ɼ� */
	protected boolean isVisible;

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);

		if (getUserVisibleHint()) {
			isVisible = true;
			onVisible();
		} else {
			isVisible = false;
			onInvisible();
		}
	}

	/**
	 * �ɼ�
	 */
	private void onVisible() {
		lazyLoad();
	}

	/**
	 * ���ɼ�
	 */
	private void onInvisible() {

	}

	/**
	 * �ӳټ��� ���������д�˷���
	 */
	protected abstract void lazyLoad();
}