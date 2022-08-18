package ch.skyfy.tomlconfiglib

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import mu.KotlinLogging
import net.peanuuutz.tomlkt.Toml
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.*
import kotlin.reflect.full.createInstance

@Suppress("unused")
object ConfigManager {

    var toml = Toml {
        ignoreUnknownKeys = false
    }

    val LOGGER = KotlinLogging.logger {}

    /**
     * Used to load one or multiple configurations.
     *
     * @param classesToLoad An [Array] of class representing singleton object that contains [ConfigData] fields that need to be loaded
     */
    fun loadConfigs(classesToLoad: Array<Class<*>>) = ConfigUtils.loadClassesByReflection(classesToLoad)

    /**
     * Used to reload a configuration when a TOML file has been modified
     *
     * @param configData A [ConfigData] object that represent a configuration
     * @return A [Boolean] that will be true if the configuration has been successfully reloaded
     */
    inline fun <reified DATA : Validatable> reloadConfig(configData: ConfigData<DATA>): Boolean {
        try {
            configData.`data` = get(configData.relativeFilePath, shouldThrowRuntimeException = false)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    /**
     *
     * This method try to deserialize a TOML file to an object of type [DATA].
     * If the TOML file is not found, a new object will be created provided by the type [DEFAULT]
     * and a new TOML file will be created
     *
     * If the TOML file does not match the TOML standard or your specific implementation that you override in your data classes,
     * a [RuntimeException] will be thrown
     *
     * @param file A [Path] object representing where the configuration file is located
     * @param toml A [Toml] object that is used to serialize and deserialize the file representing the configuration (Already has a default value and does not have to be specified)
     * @return An object of type [DATA] that represent the configuration
     */
    inline fun <reified DATA : Validatable, reified DEFAULT : Defaultable<DATA>> getOrCreateConfig(
        file: Path,
        toml: Toml = ConfigManager.toml,
    ): DATA {
        try {
            val d: DATA = if (file.exists()) get(file, toml, true)
            else save(DEFAULT::class.createInstance().getDefault(), file, toml)
            d.confirmValidate(shouldThrowRuntimeException = true)
            return d
        } catch (e: java.lang.Exception) {
            throw RuntimeException(e)
        }
    }


    /**
     * This method try to deserialize a TOML file to an object of type [DATA]
     * But this time, if the TOML file is not found, the object will be created from another
     * TOML file (a default TOML, stored inside the jar (in resources folder))
     *
     * If the TOML file does not match the TOML standard or your specific implementation that you override in your data classes,
     * a [RuntimeException] will be thrown
     *
     * @param file A [Path] object representing where the configuration file is located
     * @param defaultFile A [String] object representing a path inside a local jar to a default TOML file
     * @param toml A [Toml] object that is used to serialize and deserialize the file representing the configuration (Already has a default value and does not have to be specified)
     * @return An object of type [DATA] that represent the configuration
     */
    inline fun <reified DATA : Validatable> getOrCreateConfig(
        file: Path,
        defaultFile: String,
        toml: Toml = this.toml,
    ): DATA {
        try {
            return if (file.exists()) get(file, toml, true)
            else get(extractResource(file, defaultFile, DATA::class.java.classLoader), toml, true)
        } catch (e: java.lang.Exception) {
            throw RuntimeException(e)
        }
    }

    /**
     * Try to deserialize a TOML file to an object of type [DATA]
     *
     * @param file A [Path] object representing where the configuration file is located
     * @param toml A [Toml] object that is used to serialize and deserialize the file representing the configuration (Already has a default value and does not have to be specified)
     * @param shouldThrowRuntimeException A [Boolean] object which specifies whether to throw a [RuntimeException] if the configuration is considered as invalid
     * @return An object of type [DATA] that represent the configuration
     */
    @Throws(Exception::class)
    inline fun <reified DATA : Validatable> get(file: Path, toml: Toml = ConfigManager.toml, shouldThrowRuntimeException: Boolean): DATA {
        val `data`: DATA = toml.decodeFromString(file.readText())
        if (!`data`.confirmValidate(shouldThrowRuntimeException = shouldThrowRuntimeException)) throw Exception("The json file is not valid !!!")
        return `data`
    }

    /**
     * Try to save a configuration object representing by an object of type [DATA]
     *
     * In a first time, a checking is made to verify if the configuration is valid or not.
     * If it's not valid a [RuntimeException] will be thrown
     *
     * @param config An object of type [DATA] that representing the configuration to save
     * @param file A [Path] object representing where the configuration file is located
     * @param toml A [Toml] object that is used to serialize and deserialize the file representing the configuration (Already has a default value and does not have to be specified)
     * @return An object of type [DATA] that represent the configuration
     */
    @Throws(Exception::class)
    inline fun <reified DATA : Validatable> save(
        config: DATA,
        file: Path,
        toml: Toml = ConfigManager.toml,
    ): DATA {
        config.confirmValidate(shouldThrowRuntimeException = true)
        file.parent.createDirectories()
        if (!file.exists())file.createFile()
        file.writeText(toml.encodeToString(config))
        return config
    }

    /**
     * Try to save a configuration object representing by an object of type [DATA]
     *
     * This fun will generally be used by the developer later in the code when data has been modified and needs to be saved
     *
     * @param configData A [ConfigData] object that represent a configuration
     * @param toml A [Toml] object that is used to serialize and deserialize the file representing the configuration (Already has a default value and does not have to be specified)
     */
    @Throws(Exception::class)
    inline fun <reified DATA : Validatable> save(
        configData: ConfigData<DATA>,
        toml: Toml = ConfigManager.toml
    ) {
        if (!configData.data.confirmValidate(shouldThrowRuntimeException = false)) {
            LOGGER.warn("The data you tried to save has not been saved, because something is not valid")
            return
        }
        configData.relativeFilePath.writeText(toml.encodeToString(configData.`data`))
    }

    /**
     * A simple fun that will be used to extract a file from inside a jar to somewhere outside the jar
     *
     * @param file A [Path] object that represent where the embedded file we be extracted
     * @param resource A [String] object representing a path inside a local jar to a default TOML file
     * @return return A [Path] object representing the extracted file
     */
    fun extractResource(file: Path, resource: String, classLoader: ClassLoader): Path {
        Files.copy(Objects.requireNonNull(classLoader.getResourceAsStream(resource)), file)
        return file
    }

}