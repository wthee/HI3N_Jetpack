package cn.wthee.hi3njetpack.data


class Video(val url: String,
            val title: String,
            val imgurl: String,
            val date: String,
            val uper: String,
            val watchNum: String,
            val danmuNum: String,
            val length: String

){
    override fun equals(other: Any?): Boolean {
        if(other !is Video){
            return false
        }
        var p=other
        return this.title == p.title
    }

}