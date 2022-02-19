package com.example.testing.adapters

import android.content.Context
import android.media.MediaPlayer
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.VideoView
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.example.testing.R
import com.example.testing.activities.PlayerActivity
import com.example.testing.models.RecipeModel
import com.example.testing.models.RecipeType
import com.github.vkay94.dtpv.youtube.YouTubeOverlay
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.TimeBar
import kotlinx.android.synthetic.main.recipe_details2.view.*
import kotlinx.android.synthetic.main.row_video2.view.*
import java.lang.Exception
import java.util.logging.Handler
import java.util.logging.Logger

class AdapterVideo2(
    private var context: Context,
    private var videoArrayList:ArrayList<RecipeType>?): RecyclerView.Adapter<AdapterVideo2.HolderVideo>(){
    private var isLocked: Boolean = false
    private lateinit var runnable: Runnable
    private var player: ExoPlayer = ExoPlayer.Builder(context).build()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderVideo {
        val view = LayoutInflater.from(context).inflate(R.layout.row_video2, parent, false)
        return HolderVideo(view)
    }

    override fun onBindViewHolder(holder: HolderVideo, position: Int) {

        val modelVideo = videoArrayList!![0]

        val title:String? = modelVideo.title
        val video:String? = modelVideo.video

        holder.itemView.video_title.text = title
        holder.itemView.video_title.isSelected = true
        createPlayer(modelVideo, holder)
        initializeBinding(holder)

    }

    private fun createPlayer(modelVideo: RecipeType, holder: HolderVideo){
        doubleTapEnable(holder)
        val videoUri = Uri.parse(modelVideo.video)
        val mediaItem = MediaItem.fromUri(videoUri)
        player.setMediaItem(mediaItem)
        holder.videoView.requestFocus()
        player.prepare()
        PlayVideo(holder)
        player.addListener(object: Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int){
                super.onPlaybackStateChanged(playbackState)
                if(playbackState == Player.STATE_ENDED){
                    player.repeatMode = Player.REPEAT_MODE_ONE
                    createPlayer(modelVideo,holder)
                }
            }
        })
        setVisibility(holder)
    }
    private fun initializeBinding(holder: HolderVideo){
        holder.itemView.playpauseBtn.setOnClickListener(){
            if (holder.itemView.playerView.player!!.isPlaying) PauseVideo(holder)
            else PlayVideo(holder)
        }

        holder.itemView.lockBtn.setOnClickListener(){
            if(!isLocked){
                isLocked = true
                holder.videoView.hideController()
                holder.videoView.useController = false
                holder.itemView.lockBtn.setImageResource(R.drawable.closelock_icon)
            }
            else{
                isLocked = false
                holder.videoView.showController()
                holder.videoView.useController = true
                holder.itemView.lockBtn.setImageResource(R.drawable.lock_open_icon)
            }
        }


    }

    private fun PlayVideo(holder: HolderVideo){
        holder.itemView.playpauseBtn.setImageResource(R.drawable.pause_icon)
        holder.itemView.playerView.player!!.play()
    }

    private fun PauseVideo(holder: HolderVideo){
        holder.itemView.playpauseBtn.setImageResource(R.drawable.play_icon)
        holder.itemView.playerView.player!!.pause()
    }
    private fun setVisibility(holder: HolderVideo){
        runnable = Runnable {
            if (holder.videoView.isControllerVisible)changeVisibility(View.VISIBLE, holder)
            else changeVisibility(View.INVISIBLE,holder)
            android.os.Handler(Looper.getMainLooper()).postDelayed(runnable, 300)
        }
        android.os.Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }
    private fun changeVisibility(visibility: Int, holder: HolderVideo){
        holder.itemView.topController.visibility = visibility
        if(holder.itemView.rootView.bottomController!=null)
            holder.itemView.rootView.bottomController.visibility = visibility
        holder.itemView.playpauseBtn.visibility = visibility
        if( holder.itemView.rootView.backBtn!=null)
        holder.itemView.rootView.backBtn.visibility = visibility

        if( holder.itemView.rootView.showBtn!=null)
        if(PlayerActivity.portrait)
        {
            holder.itemView.rootView.showBtn.visibility = View.INVISIBLE

        }
        else
        {
            holder.itemView.rootView.showBtn.visibility = visibility
        }

            if(isLocked)holder.itemView.lockBtn.visibility = View.VISIBLE
        else holder.itemView.lockBtn.visibility = visibility
    }

    override fun getItemCount(): Int {
        return videoArrayList!!.size
    }

    private fun doubleTapEnable(holder: HolderVideo){
        holder.videoView.playerView.player = player
        holder.itemView.ytOverlay.performListener(object: YouTubeOverlay.PerformListener{
            override fun onAnimationEnd() {
                holder.itemView.ytOverlay.visibility = View.GONE
            }
            override fun onAnimationStart() {
                holder.itemView.ytOverlay.visibility = View.VISIBLE
            }
        })
        holder.itemView.ytOverlay.player(player)
    }

    class HolderVideo(itemView: View) : RecyclerView.ViewHolder(itemView){
        var videoView: PlayerView = itemView.findViewById(R.id.playerView)


        }
}