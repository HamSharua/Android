package com.example.challengeme.ui.timeline

import android.graphics.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.challengeme.R
import com.example.challengeme.databinding.ItemTimelineBinding
import com.squareup.picasso.Picasso

class TimelineAdapter(private val timelineItems: List<TimelineItem>) :
    RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val binding = ItemTimelineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimelineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        val timelineItem = timelineItems[position]

        // ユーザー名をセット
        holder.binding.userName.text = timelineItem.userName

        // ユーザーアイコンを丸く表示
        if (!timelineItem.userIcon.isNullOrEmpty()) {
            Picasso.get().load(timelineItem.userIcon)
                .transform(CircleTransform())
                .into(holder.binding.userIcon)
        } else {
            // デフォルトアイコンを表示
            holder.binding.userIcon.setImageResource(R.drawable.default_user_icon)
        }

        // コメントをセット
        holder.binding.commentText.text = timelineItem.comment

        // 画像URLが空でないか確認し、空ならばデフォルト画像を表示
        if (!timelineItem.imageUrl.isNullOrEmpty()) {
            // 投稿画像がある場合
            Picasso.get().load(timelineItem.imageUrl).into(holder.binding.postImage)
        } else {
            // 投稿画像がない場合は、デフォルト画像や透明のビューを表示する処理
            holder.binding.postImage.setImageResource(R.drawable.default_image) // デフォルト画像に差し替えてください
        }
    }


    override fun getItemCount(): Int {
        return timelineItems.size
    }

    inner class TimelineViewHolder(val binding: ItemTimelineBinding) : RecyclerView.ViewHolder(binding.root)

    // Picasso 用の丸い画像変換クラス
    class CircleTransform : com.squareup.picasso.Transformation {
        override fun transform(source: Bitmap): Bitmap {
            val size = Math.min(source.width, source.height)
            val x = (source.width - size) / 2
            val y = (source.height - size) / 2
            val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)
            if (squaredBitmap != source) {
                source.recycle()
            }
            val bitmap = Bitmap.createBitmap(size, size, source.config)
            val canvas = Canvas(bitmap)
            val paint = Paint()
            val shader = BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            paint.shader = shader
            paint.isAntiAlias = true
            val r = size / 2f
            canvas.drawCircle(r, r, r, paint)
            squaredBitmap.recycle()
            return bitmap
        }

        override fun key(): String {
            return "circle"
        }
    }
}
