package com.zhengineer.dutchblitzscorer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.zhengineer.dutchblitzscorer.database.dao.GameDao
import com.zhengineer.dutchblitzscorer.database.dao.GamePlayerDao
import com.zhengineer.dutchblitzscorer.database.dao.PlayerNameDao
import com.zhengineer.dutchblitzscorer.database.dao.ScoresDao
import com.zhengineer.dutchblitzscorer.database.entities.Game
import com.zhengineer.dutchblitzscorer.database.entities.GamePlayer
import com.zhengineer.dutchblitzscorer.database.entities.PlayerName
import com.zhengineer.dutchblitzscorer.database.entities.Scores

/**
 * DB as in dutch blitz, but also DB as in database :--)
 */
@Database(
    entities = [Game::class, GamePlayer::class, PlayerName::class, Scores::class],
    version = 1,
    exportSchema = true
)
abstract class DBScoreDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun gamePlayerDao(): GamePlayerDao
    abstract fun playerNameDao(): PlayerNameDao
    abstract fun scoresDao(): ScoresDao

    companion object {
        @Volatile
        private var instance: DBScoreDatabase? = null

        fun getInstance(context: Context): DBScoreDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context,
                    DBScoreDatabase::class.java,
                    "db-score-database"
                ).build().also { instance = it }
            }
        }
    }
}