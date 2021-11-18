package com.auth0.android.util

import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.internal.Util.checkOffsetAndCount
import okhttp3.internal.Util.format
import okio.BufferedSink
import java.lang.IllegalArgumentException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8

public fun String.toHttpUrlOrNull(): HttpUrl? {
    return HttpUrl.parse(this)
}

public fun String.toHttpUrl(): HttpUrl {
    return HttpUrl.parse(this)
        ?: throw IllegalArgumentException("failed to build HttpUrl out of $this")
}

public fun String.toMediaType(): MediaType {
    return MediaType.parse(this)
        ?: throw IllegalArgumentException("failed to build MediaType out of $this")
}

public fun String.toRequestBody(mediaType: String): RequestBody {
    return RequestBody.create(mediaType.toMediaType(), this)
}

public fun String.toRequestBody(contentType: MediaType? = null): RequestBody {
    var charset: Charset = UTF_8
    var finalContentType: MediaType? = contentType
    if (contentType != null) {
        val resolvedCharset = contentType.charset()
        if (resolvedCharset == null) {
            charset = UTF_8
            finalContentType = "$contentType; charset=utf-8".toMediaType()
        } else {
            charset = resolvedCharset
        }
    }
    val bytes = toByteArray(charset)
    return bytes.toRequestBody(finalContentType, 0, bytes.size)
}

public fun ByteArray.toRequestBody(
    contentType: MediaType? = null,
    offset: Int = 0,
    byteCount: Int = size
): RequestBody {
    checkOffsetAndCount(size.toLong(), offset.toLong(), byteCount.toLong())
    return object : RequestBody() {
        override fun contentType() = contentType

        override fun contentLength() = byteCount.toLong()

        override fun writeTo(sink: BufferedSink) {
            sink.write(this@toRequestBody, offset, byteCount)
        }
    }
}

public fun Map<String, String>.toHeaders(): Headers {
    return Headers.of(this)
}