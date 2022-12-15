package com.chenzb.recorder_m4a.utils

import com.coremedia.iso.boxes.Container
import com.googlecode.mp4parser.authoring.Movie
import com.googlecode.mp4parser.authoring.Track
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator
import com.googlecode.mp4parser.authoring.tracks.AppendTrack
import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import java.util.*

/**
 * 创建者：Chenzb
 * 创建日期：2022/12/15 16:27
 * 描述：媒体工具类
 */
object MediaUtils {

    fun mergeMediaFiles(isAudio: Boolean, sourceFiles: MutableList<String>, targetFile: String): Boolean {
        return try {
            val mediaKey = if (isAudio) "soun" else "vide"
            val listMovies: MutableList<Movie> = mutableListOf()

            for (filename in sourceFiles) {
                listMovies.add(MovieCreator.build(filename))
            }

            val listTracks: MutableList<Track> = LinkedList()
            for (movie in listMovies) {
                for (track in movie.tracks) {
                    if (track.handler.equals(mediaKey)) {
                        listTracks.add(track)
                    }
                }
            }

            val outputMovie = Movie()
            if (listTracks.isNotEmpty()) {
                outputMovie.addTrack(AppendTrack(*listTracks.toTypedArray()))
            }

            val container: Container = DefaultMp4Builder().build(outputMovie)
            val fileChannel: FileChannel = RandomAccessFile(String.format(targetFile), "rw").channel

            container.writeContainer(fileChannel)
            fileChannel.close()

            true
        } catch (e: Exception) {
            false
        }
    }
}