package cn.wthee.hi3njetpack.util


import android.app.Activity

import java.lang.ref.WeakReference

/**
 * Created by zhoujian on 2018/1/19.
 */

class ActivityUtil private constructor() {

    private var sCurrentActivityWeakRef: WeakReference<Activity>? = null

    var currentActivity: Activity?
        get() {
            var currentActivity: Activity? = null
            if (sCurrentActivityWeakRef != null) {
                currentActivity = sCurrentActivityWeakRef!!.get()
            }
            return currentActivity
        }
        set(activity) {
            sCurrentActivityWeakRef = WeakReference<Activity>(activity)
        }

    companion object {

        val instance = ActivityUtil()
    }

}