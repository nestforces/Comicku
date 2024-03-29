package com.example.comickufinal.adapter


import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.comickufinal.R
import com.example.comickufinal.adapter.listener.OnSelectedHistoryListener
import com.example.comickufinal.tools.Utilities
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.item_manga_no_card.view.*


class HistoryAdapter(private val historyList: List<Map<String, String>>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private var onSelectedHistoryListener: OnSelectedHistoryListener? = null


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val circularProgressDrawable = Utilities.circularProgressDrawable(itemView.context)

        fun bindTo(map: Map<String, String>) {
            itemView.mangaItem_tvTitle.text = map["title"]
            itemView.mangaItem_tvType.text = map["type"]
            itemView.mangaItem_tvChapter.text = itemView.context.getString(R.string.last_chapter)
            itemView.mangaItem_tvUpdateOn.text = map["chapter"]

            // background type
            itemView.mangaItem_tvType.background =
                Utilities.backgroundType(map["type"].toString(), itemView.context)
            itemView.flag.background =
                Utilities.flagType(map["type"].toString(), itemView.context)


            // set image view height
            val layoutParams = itemView.mangaItem_imageView.layoutParams
            layoutParams.width = itemView.context.resources.getDimension(R.dimen.thumbnail_fav_history_width).toInt()

            Glide.with(itemView.context)
                .load(map["thumb"].toString())
                .placeholder(circularProgressDrawable)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(itemView.mangaItem_imageView)

            itemView.setOnClickListener {
                onSelectedHistoryListener?.onSelectedHistory(map)
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_manga_no_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindTo(historyList[position])
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    fun setOnSelectedHistoryListener(onSelectedHistoryListener: OnSelectedHistoryListener) {
        this.onSelectedHistoryListener = onSelectedHistoryListener
    }

    fun onLongClickHistoryListener(firebaseUser: FirebaseUser) {

        val database = Firebase.database
        val ref = database.getReference("history")
        val applesQuery: Query =
            ref.orderByKey().equalTo(firebaseUser.uid)
        applesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (appleSnapshot in dataSnapshot.children) {
                    appleSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException())
            }
        })
    }

}