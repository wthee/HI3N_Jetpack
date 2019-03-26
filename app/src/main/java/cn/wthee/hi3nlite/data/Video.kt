package cn.wthee.hi3nlite.data


class Video(val url: String,
            val title: String,
            val imgurl: String,
            val imgurlM:String,
            val date: String,
            val uper: String,
            val watchNum: String,
            val danmuNum: String,
            val length: String,
            val intro: String

){
    override fun equals(other: Any?): Boolean {
        if(other !is Video){
            return false
        }
        var p=other
        return this.title == p.title
    }

}