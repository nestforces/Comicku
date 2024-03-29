package com.example.comickufinal.repository.datasource

import androidx.paging.PageKeyedDataSource
import com.example.comickufinal.model.PopularMangaResponse
import com.example.comickufinal.network.NetworkState
import com.example.comickufinal.repository.AllContentRepository
import io.reactivex.rxjava3.disposables.CompositeDisposable

class PopularMangaDataSource(
    private val allContentRepository: AllContentRepository,
    private val compositeDisposable: CompositeDisposable
) : PageKeyedDataSource<Int, PopularMangaResponse.Manga>() {

    private var firstPage = 1
    private val networkState = allContentRepository.networkState

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, PopularMangaResponse.Manga>
    ) {
        compositeDisposable.add(
            allContentRepository.getAllPopularManga(firstPage)
                .subscribe({
                    callback.onResult(
                        it.data, null, firstPage + 1
                    )
                    networkState.postValue(NetworkState.LOADED)
                }, {
                    networkState.postValue(NetworkState.ERROR)
                })
        )
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, PopularMangaResponse.Manga>
    ) {
        // who care
    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, PopularMangaResponse.Manga>
    ) {
        compositeDisposable.add(
            allContentRepository.getAllPopularManga(params.key)
                .subscribe({
                    callback.onResult(
                        it.data, params.key + 1
                    )
                    networkState.postValue(NetworkState.LOADED)
                }, {
                    networkState.postValue(NetworkState.ERROR)
                })
        )
    }
}