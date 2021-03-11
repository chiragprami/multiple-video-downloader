package com.app.work.demo.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.work.demo.data.resource.MovieRepo
import com.app.work.demo.model.Status
import com.app.work.demo.model.Video
import javax.inject.Inject


class MainViewModel @Inject constructor(val movieRepo: MovieRepo) :
    ViewModel() {

    private val listOfVideoLiveData: MutableLiveData<ArrayList<Video>> =
        MutableLiveData<ArrayList<Video>>()

    init {
        movieRepo.let { it.listOfMovies(listOfVideoLiveData) }
    }

    fun observeMovieResponse(): MutableLiveData<ArrayList<Video>> {
        return listOfVideoLiveData
    }

    fun updateStatus(
        status: Status,
        index: Int,
        percentage: String
    ): MutableLiveData<ArrayList<Video>> {
        val list = ArrayList<Video>()
        listOfVideoLiveData.value?.let { list.addAll(it) }
        list[index].status = status;
        list[index].percentage = percentage;
        listOfVideoLiveData.postValue(list)
        return listOfVideoLiveData
    }


}

