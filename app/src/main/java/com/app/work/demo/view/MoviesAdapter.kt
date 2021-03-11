package com.app.work.demo.view

import BASEURL
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import checkFileExists
import com.app.work.demo.R
import com.app.work.demo.model.Status
import com.app.work.demo.model.Video
import com.bumptech.glide.Glide
import getVideoFileName
import kotlinx.android.synthetic.main.raw_movies.view.*
import java.io.File

class MoviesAdapter(var mContext: Fragment) :
    RecyclerView.Adapter<MoviesAdapter.Holder>() {


    var listOfMovies: ArrayList<Video> = ArrayList()

    var downloadClick: ((Int, Video) -> (Unit))? = null

    fun downloadListener(f: (Int, Video) -> Unit) {
        downloadClick = f
    }

    var playVideo: ((File) -> (Unit))? = null

    fun playVideoListener(f: (File) -> Unit) {
        playVideo = f
    }


    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(R.layout.raw_movies, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listOfMovies.size
    }

    override fun onBindViewHolder(holder: Holder, pos: Int) {
        val model = listOfMovies[pos]
        holder.itemView.tvTitle.text = model.title
        holder.itemView.tvSubTitle.text = model.subtitle.toString()
        val videoPath = model.sources.first()
        Glide
            .with(mContext)
            .load(BASEURL + model.thumb)
            .centerCrop()
            .into(holder.itemView.ivMovieThumb)

        when (model.status) {
            Status.COMPlETED -> {
                holder.itemView.btnStatus.text = "Play"
            }
            Status.DOWNLOADING -> {
                holder.itemView.btnStatus.text = "Downloading..."
            }
            else -> {
                holder.itemView.btnStatus.text = "Tap to Download"

            }
        }

        holder.itemView.btnStatus.setOnClickListener {
            when (model.status) {
                Status.COMPlETED -> {
                    val isExist = mContext.activity?.checkFileExists(videoPath)
                    if (isExist == true) {
                        playVideo?.let { click ->
                            mContext.activity?.getVideoFileName(videoPath)?.let { file ->
                                click(file)
                            }
                        }
                    } else {
                        downloadClick?.let { click -> click(pos, model) }
                    }
                }
                else -> {
                    downloadClick?.let { click -> click(pos, model) }

                }
            }

        }


    }
}

