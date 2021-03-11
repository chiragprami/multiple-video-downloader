package com.app.work.demo.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.app.work.demo.R
import com.app.work.demo.base.BaseFragment
import com.app.work.demo.model.Status
import com.app.work.demo.network.DownloadResult
import com.app.work.demo.network.downloadFile
import com.app.work.demo.viewmodel.MainViewModel
import createUri
import io.ktor.client.*
import io.ktor.client.engine.android.*
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import logv
import viewFile


class MainFragment : BaseFragment<MainViewModel>() {

    private val PERMISSIONS = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val PERMISSION_REQUEST_CODE = 1
    private val DOWNLOAD_FILE_CODE = 2
    lateinit var moviesAdapter: MoviesAdapter


    companion object {
        fun newInstance() = MainFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter()
        handleResult()
    }

    private fun adapter() {
        moviesAdapter = MoviesAdapter(mContext = this)
        rvMovies.adapter = moviesAdapter
        moviesAdapter.downloadListener { position, video ->
            this.context?.let { context ->
                val videoUrl = video.sources.first()
                val uri = context.createUri(videoUrl = videoUrl)
                downloadFile(
                    context = context,
                    url = videoUrl,
                    file = uri,
                    index = position
                )
            }
        }
        moviesAdapter.playVideoListener {
            this.activity?.viewFile(it)
        }
        moviesAdapter.notifyDataSetChanged();
    }

    private fun handleResult() {
        viewModel?.observeMovieResponse()?.observe(viewLifecycleOwner, Observer {
            it?.let { data ->
                moviesAdapter.listOfMovies = data
                moviesAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun getViewModel(): Class<MainViewModel> {
        return MainViewModel::class.java
    }


    private fun downloadFile(context: Context, url: String, file: Uri, index: Int) {
        viewModel?.updateStatus(Status.DOWNLOADING, index, "preparing...")
        context.contentResolver.openOutputStream(file)?.let { outputStream ->
            CoroutineScope(Dispatchers.IO).launch {
                HttpClient(Android).downloadFile(outputStream, url).collect {
                    withContext(Dispatchers.Main) {
                        when (it) {
                            is DownloadResult.Success -> {
                                logv("downloaded_file ${file.path}")
                                viewModel?.updateStatus(Status.COMPlETED, index, "(Open))")
                            }

                            is DownloadResult.Error -> {
                                viewModel?.updateStatus(Status.NONE, index, "ERROR")
                                Toast.makeText(
                                    context,
                                    "Error while downloading file",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            is DownloadResult.Progress -> {
                            }
                        }
                    }
                }
            }
        }
    }

    private fun hasPermissions(context: Context?, permissions: List<String>): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
            return permissions.all { permission ->
                ActivityCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            }
        }

        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE && hasPermissions(context, PERMISSIONS)) {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == DOWNLOAD_FILE_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                context?.let { context ->
                }
            }
        }
    }
}


