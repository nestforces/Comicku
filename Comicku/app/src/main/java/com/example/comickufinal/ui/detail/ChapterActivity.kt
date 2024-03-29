package com.example.comickufinal.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.comickufinal.R
import com.example.comickufinal.adapter.ChapterImageAdapter
import com.example.comickufinal.model.DetailMangaResponse
import com.example.comickufinal.network.NetworkState
import com.example.comickufinal.ui.viewmodel.DetailViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_chapter.*
import kotlinx.android.synthetic.main.fragment_home.view.*


class ChapterActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_CHAPTER_ENDPOINT = "extra_chapter_endpoint"
        const val EXTRA_MANGA_ENDPOINT = "extra_manga_endpoint"
    }

    private var hasPrevChapter = false
    private var prevChapter = DetailMangaResponse.Chapter("", "")
    private var hasNextChapter = false
    private var nextChapter = DetailMangaResponse.Chapter("", "")
    private lateinit var detailViewModel: DetailViewModel
    private var swipeRefreshHome: SwipeRefreshLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapter)

        swipeRefreshHome = findViewById(R.id.swipeRefresh)

        val title = intent.getStringExtra(EXTRA_TITLE) ?: ""
        val chapterEndpoint = intent.getStringExtra(EXTRA_CHAPTER_ENDPOINT) ?: ""
        val mangaEndpoint = intent.getStringExtra(EXTRA_MANGA_ENDPOINT) ?: ""

        // title
        chapterAct_tvTitle.text = title

        // toolbar
        chapterAct_toolbar.title = ""
        setSupportActionBar(chapterAct_toolbar)
        chapterAct_toolbar.setNavigationOnClickListener {
            finish()
        }

        // adapter
        val adapter = ChapterImageAdapter()
        chapterAct_recyclerView.adapter = adapter

        // view model
        detailViewModel = ViewModelProvider(this).get(DetailViewModel::class.java)
        detailViewModel.setMangaEndpoint(mangaEndpoint)
        detailViewModel.setChapterEndpoint(chapterEndpoint)

        detailViewModel.chapterImage.observe(this) {
            adapter.submitList(it)

            detailViewModel.detailManga.observe(this) { manga ->
                // set history
                val chapter = DetailMangaResponse.Chapter(
                    detailViewModel.getChapterEndpoint(),
                    chapterAct_tvTitle.text.toString()
                )

                detailViewModel.setHistoryManga(manga!!, chapter)


                Flowable.fromIterable(manga.chapter.withIndex())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter { (_, value) ->
                        return@filter value.chapterEndpoint == detailViewModel.getChapterEndpoint()
                    }
                    .subscribe { (index, _) ->
                        try {
                            val nextIndex = index - 1

                            nextChapter = manga.chapter[nextIndex]
                            hasNextChapter = true
                        } catch (e: IndexOutOfBoundsException) {
                            hasNextChapter = false
                        }

                        try {
                            val prevIndex = index + 1

                            prevChapter = manga.chapter[prevIndex]
                            hasPrevChapter = true
                        } catch (e: IndexOutOfBoundsException) {
                            hasPrevChapter = false
                        }

                        // button visibility
                        if (!hasNextChapter) {
                            chapterAct_btnNextChapter.visibility = View.INVISIBLE
                        } else {
                            chapterAct_btnNextChapter.visibility = View.VISIBLE
                        }

                        if (!hasPrevChapter) {
                            chapterAct_btnPrevChapter.visibility = View.INVISIBLE
                        } else {
                            chapterAct_btnPrevChapter.visibility = View.VISIBLE
                        }
                    }
            }
        }

        detailViewModel.networkState.observe(this) {
            if (it == NetworkState.TIMEOUT || it == NetworkState.ERROR) {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }
        }

        // button listener
        chapterAct_btnNextChapter.setOnClickListener(this)
        chapterAct_btnPrevChapter.setOnClickListener(this)

        //swiperefresh

        swipeRefresh.setOnRefreshListener {
            resetData()
            fetchData()     // refresh your list contents somehow
            swipeRefresh.isRefreshing = false   // reset the SwipeRefreshLayout (stop the loading spinner)
        }
    }

    private fun resetData() {
        detailViewModel.reset
    }

    private fun fetchData() {
        detailViewModel.chapterImage.observe(this) {
            ChapterImageAdapter().submitList(it)
        }
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.chapterAct_btnNextChapter -> {
                chapterAct_tvTitle.text = nextChapter.chapterTitle
                detailViewModel.setChapterEndpoint(nextChapter.chapterEndpoint)
            }
            R.id.chapterAct_btnPrevChapter -> {
                chapterAct_tvTitle.text = prevChapter.chapterTitle
                detailViewModel.setChapterEndpoint(prevChapter.chapterEndpoint)
            }
        }
    }
}