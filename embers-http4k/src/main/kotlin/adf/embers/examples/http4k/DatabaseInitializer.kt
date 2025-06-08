package adf.embers.examples.http4k

import adf.embers.tools.EmbersDatabase
import javax.sql.DataSource

class DatabaseInitializer {
    private val embersDb = EmbersDatabase("jdbc:hsqldb:mem:embers;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE")

    fun initializeInMemoryH2(): DataSource {
        embersDb.startInMemoryDatabase()
        embersDb.createEmbersDdl()
        return embersDb.dataSource
    }

    fun shutdown() {
        embersDb.shutdownInMemoryDatabase()
    }
}
