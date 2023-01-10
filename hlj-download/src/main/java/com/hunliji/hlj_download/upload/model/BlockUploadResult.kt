package com.hunliji.hlj_download.upload.model

import com.google.gson.annotations.SerializedName

/**
 *
 * ctx	是	本次上传成功后的块级上传控制信息，用于后续上传片(bput)及创建文件(mkfile)。
 * 本字段是只能被七牛服务器解读使用的不透明字段，上传端不应修改其内容。
 * 每次返回的<ctx>都只对应紧随其后的下一个上传数据片，上传非对应数据片会返回701状态码。
 *
 *
 * checksum	是	本块已上传部分的校验码，只能被七牛服务器解读使用。
 *
 *
 * crc32	是	本块已上传部分的CRC32值，上传端可通过此字段对本块已上传部分的完整性进行校验。
 *
 *
 * offset	是	下一个上传片在上传块中的偏移。
 *
 *
 * host	是	后续上传接收地址。
 *
 *
 * Created by wangtao on 2017/8/4.
</ctx> */

class BlockUploadResult {
    @SerializedName("ctx")
    var uploadContext: String = ""
    @SerializedName("checksum")
    var checkSum: String = ""
    @SerializedName("crc32")
    var crc32: Long = 0
    @SerializedName("offset")
    var offset: Long = 0
    @SerializedName("host")
    var host: String = ""
}
