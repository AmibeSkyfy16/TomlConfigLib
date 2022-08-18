package ch.skyfy.tomlconfiglib.example4.config

import ch.skyfy.tomlconfiglib.ConfigData
import java.nio.file.Paths

object Configs {
    /**
     * In this example we use a default toml file located inside the jar
     */
    val CONFIG = ConfigData.invoke<Database>(Paths.get("C:\\temp\\database.toml"), "example4/database.toml")
}