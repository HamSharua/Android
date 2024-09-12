package com.example.challengeme.ui.timeline

import android.graphics.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
        Picasso.get().load(timelineItem.userIcon)
            .transform(CircleTransform())
            .into(holder.binding.userIcon)

        // コメントをセット
        holder.binding.commentText.text = timelineItem.comment

        // 投稿画像を表示
        // Firebase Storage の URL からはExif情報を取れないので、単純にPicassoを使う
        Picasso.get().load(timelineItem.imageUrl).into(holder.binding.postImage)
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
