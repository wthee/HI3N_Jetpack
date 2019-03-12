package cn.wthee.hi3njetpack.util

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView

//滚动文本

class MarqueTextView : TextView {

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context) : super(context) {}

    override fun isFocused(): Boolean {
        return true
    }
}