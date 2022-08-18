package ch.skyfy.jsonconfig.example3.config

import ch.skyfy.tomlconfiglib.Defaultable
import ch.skyfy.tomlconfiglib.Validatable
import net.peanuuutz.tomlkt.TomlComment

@kotlinx.serialization.Serializable
data class Config(
    @TomlComment("The day when the player will be able to kill each other")
    val dayOfAuthorizationOfThePvP: Int,

    @TomlComment("The day when the player will be able to go inside the Nether")
    val dayOfAuthorizationOfTheEntryInTheNether: Int,

    @TomlComment("If true, allow player to use EnderPearl to enter inside an enemy base")
    val allowEnderPearlAssault: Boolean
) : Validatable

@Suppress("unused")
class DefaultConfig : Defaultable<Config> {
    override fun getDefault() = Config(2, 4, false)
}