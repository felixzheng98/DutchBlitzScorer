package com.zhengineer.dutchblitzscorer.di

import android.content.Context
import com.zhengineer.dutchblitzscorer.database.DBScoreDatabase
import com.zhengineer.dutchblitzscorer.database.dao.GameDao
import com.zhengineer.dutchblitzscorer.database.dao.GamePlayerDao
import com.zhengineer.dutchblitzscorer.database.dao.PlayerNameDao
import com.zhengineer.dutchblitzscorer.database.dao.ScoresDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): DBScoreDatabase {
        return DBScoreDatabase.getInstance(context)
    }

    @Provides
    fun provideGameDao(appDatabase: DBScoreDatabase): GameDao {
        return appDatabase.gameDao()
    }

    @Provides
    fun provideGamePlayerDao(appDatabase: DBScoreDatabase): GamePlayerDao {
        return appDatabase.gamePlayerDao()
    }

    @Provides
    fun providesPlayerNameDao(appDatabase: DBScoreDatabase): PlayerNameDao {
        return appDatabase.playerNameDao()
    }

    @Provides
    fun providesScoresDao(appDatabase: DBScoreDatabase): ScoresDao {
        return appDatabase.scoresDao()
    }
}