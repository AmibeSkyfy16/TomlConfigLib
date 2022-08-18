package ch.skyfy.tomlconfiglib.example4.config

import ch.skyfy.tomlconfiglib.Defaultable
import ch.skyfy.tomlconfiglib.Validatable
import net.peanuuutz.tomlkt.TomlComment

@kotlinx.serialization.Serializable
data class Database(
    @TomlComment("3306 or 3307 ! nothing either is accepted !")
    val port: Int,

    @TomlComment("The day when the player will be able to go inside the Nether")
    val url: String,
) : Validatable{
    override fun validateImpl(errors: MutableList<String>) {
        if(port != 3306 && port != 3307)
            errors.add("Port doesn't follow the rule")
    }
}

@Suppress("unused")
class DefaultDatabase : Defaultable<Database> {
    override fun getDefault() = Database(3306, "localhost")
}