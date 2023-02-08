package com.example.detect_voice_app.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build


class PendingIntentCompat private constructor(){
    companion object{
        private fun addImmutableFlag(flags: Int): Int {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) flags or PendingIntent.FLAG_IMMUTABLE else flags
        }

        /**
         * Creates a [PendingIntent] to start an activity. It is immutable on API level 23 and
         * greater.
         *
         * @param context     The context in which the activity should be started.
         * @param requestCode The request code
         * @param intent      The Intent of the activity to be launched.
         * @param flags       The flags for the intent.
         * @return The pending intent.
         * @see PendingIntent.getActivity
         */
        fun getActivity(
            context: Context, requestCode: Int,
            intent: Intent, flags: Int
        ): PendingIntent {
            return PendingIntent.getActivity(context, requestCode, intent, addImmutableFlag(flags))
        }

        /**
         * Creates a [PendingIntent] to start a service. It is immutable on API level 23 and
         * greater.
         *
         * @param context     The context in which the service should be started.
         * @param requestCode The request code
         * @param intent      The Intent of the service to be launched.
         * @param flags       The flags for the intent.
         * @return The pending intent.
         * @see PendingIntent.getService
         */
        fun getService(
            context: Context, requestCode: Int,
            intent: Intent, flags: Int
        ): PendingIntent {
            return PendingIntent.getService(context, requestCode, intent, addImmutableFlag(flags))
        }

        /**
         * Creates a [PendingIntent] to perform a broadcast. It is immutable on API level 23 and
         * greater.
         *
         * @param context     The context in which the broadcast should be performed.
         * @param requestCode The request code
         * @param intent      The Intent to be broadcast.
         * @param flags       The flags for the intent.
         * @return The pending intent.
         * @see PendingIntent.getBroadcast
         */
        fun getBroadcast(
            context: Context, requestCode: Int,
            intent: Intent, flags: Int
        ): PendingIntent {
            return PendingIntent.getBroadcast(context, requestCode, intent, addImmutableFlag(flags))
        }
    }
}