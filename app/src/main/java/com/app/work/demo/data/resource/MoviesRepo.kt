package com.app.work.demo.data.resource

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import checkFileExists
import com.app.work.demo.MyApplication
import com.app.work.demo.model.Response
import com.app.work.demo.model.Status
import com.app.work.demo.model.Video
import com.google.gson.Gson
import org.json.JSONObject
import readXMLinString
import txtResource
import javax.inject.Inject


class MovieRepo @Inject constructor() {


    @SuppressLint("CheckResult")
    fun listOfMovies(
        data: MutableLiveData<ArrayList<Video>>
    ) {
        val json = JSONObject(
            readXMLinString(
                txtResource,
                MyApplication.appContext!!
            )
        )
        Gson().fromJson(json.toString(), Response::class.java)?.let { response ->
            response.categories.first().videos?.forEach { video ->
                val isExist = MyApplication.appContext!!.checkFileExists(video.sources.first())
                video.status = if (isExist) Status.COMPlETED else Status.NONE
            }
            data.value = response?.categories.first().videos
        }
    }

}



