package io.goldstone.blockchain.common.base.view

import android.view.ViewGroup
import android.support.v4.view.PagerAdapter
import android.view.View


/**
 * @author KaySaith
 * @date  2018/12/02
 * 构造方法，参数是我们的页卡，这样比较方便。
 */
class ViewPagerAdapter(private val views: List<View>) : PagerAdapter() {

	override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
		container.removeView(views[position])//删除页卡
	}

	override fun instantiateItem(container: ViewGroup, position: Int): Any {
		// 添加页卡
		// 这个方法用来实例化页卡
		container.addView(views[position], 0)
		return views[position]
	}

	override fun getCount(): Int {
		return views.size
	}

	override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
		//官方提示这样写
		return arg0 === arg1
	}
}