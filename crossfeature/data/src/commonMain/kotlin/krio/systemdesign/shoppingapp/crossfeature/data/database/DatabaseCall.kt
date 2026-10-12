package krio.systemdesign.shoppingapp.crossfeature.data.database

import androidx.room.RoomDatabase
import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import androidx.sqlite.SQLiteException
import co.touchlab.kermit.Logger

// Runs a database operation and wraps a database failure (a full disk, a corrupted file) in Result.
// Only SQLiteException: anything else, such as a failed require, is a bug and must not pass for a database error.
// The database counterpart of networkCall.
internal inline fun <T> databaseCall(block: () -> T): Result<T> = try {
    Result.success(block())
} catch (e: SQLiteException) {
    Logger.e(e) { "Database operation failed" }
    Result.failure(e)
}

// A write transaction. Room's withTransaction exists only on Android; this is its multiplatform counterpart.
internal suspend fun <R> RoomDatabase.writeTransaction(block: suspend () -> R): R =
    useWriterConnection { connection -> connection.immediateTransaction { block() } }
