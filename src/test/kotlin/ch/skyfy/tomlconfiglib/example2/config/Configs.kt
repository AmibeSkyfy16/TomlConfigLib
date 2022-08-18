package ch.skyfy.tomlconfiglib.example2.config

import ch.skyfy.tomlconfiglib.ConfigData
import java.nio.file.Paths

object Configs {
    val PLAYERS_HOMES = ConfigData.invoke<PlayersHomesConfig, DefaultPlayerHomeConfig>(Paths.get("C:\\temp\\players-homes.toml"))
}