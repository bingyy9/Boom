package com.boom.android.ui.adapter.repo

import com.boom.utils.StringUtils
import java.util.regex.Pattern

data class Resolution(var width: Int = 1, var height: Int = 1, var rate: String = "") {
    constructor(data: String): this(width = 1, height = 1, rate = "") {
        toResolution(data);
    }


    fun save():String{
        return width.toString() + "x" + height.toString() + "_" + rate
    }

    fun toResolution(data: String){
        if(StringUtils.isEmpty(data)){
            //default use screen resolution
            return;
        }
        val regex = "([\\d]+)x([\\d]+)_([\\d+:]+)"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(data)
        while (matcher.find()) {
            this.width = matcher.group(1).toInt()
            this.height = matcher.group(2).toInt()
            this.rate = matcher.group(3)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Resolution

        if (width != other.width) return false
        if (height != other.height) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        return result
    }

    override fun toString(): String {
        return "${width}x${height} ($rate)"
    }


}