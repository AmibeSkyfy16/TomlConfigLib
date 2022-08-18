package ch.skyfy.tomlconfiglib.example3.config

import ch.skyfy.tomlconfiglib.ConfigData
import java.nio.file.Paths

object Configs {
    val CONFIG = ConfigData.invoke<Config, DefaultConfig>(Paths.get("C:\\temp\\games.toml"))
}