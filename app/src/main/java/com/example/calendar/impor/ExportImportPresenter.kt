package com.example.calendar.impor

import android.content.ContentResolver
import android.net.Uri
import com.arellomobile.mvp.InjectViewState
import com.example.calendar.helpers.BaseMvpSubscribe
import com.example.calendar.repository.server.EventRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


@InjectViewState
class ExportImportPresenter(
    private val eventRepository: EventRepository,
    private val contentResolver: ContentResolver
    ) : BaseMvpSubscribe<LoadingView>() {

    fun onExport(uri: Uri) {
        viewState.showLoading()
        val result = eventRepository.export(uri.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                writeResponseBodyToUri(uri, it)
            }
            .subscribe {
                viewState.stopLoading()
                viewState.showToast("File download name: $uri")
            }
        unsubscribeOnDestroy(result)
    }

    private fun writeResponseBodyToUri(uri: Uri, body: ResponseBody) {
        try {
            contentResolver.openFileDescriptor(uri, "w")?.use {
                // automatically closing the stream
                FileOutputStream(it.fileDescriptor).use {
                    it.write(body.bytes())
                }
            }
        } catch (e: FileNotFoundException) {
            viewState.showToast("Can't save file")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}