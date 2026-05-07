plugins {
    id("com.wolfyscript.wolfyutils.spigot.java-conventions")
    alias(libs.plugins.goooler.shadow)
}

dependencies {
    compileOnly(group = "de.iani.cubeside", name = "LWC", version = "5.0.16-SNAPSHOT")
    compileOnly(group = "com.sk89q.worldedit", name = "worldedit-bukkit", version = "7.3.10")
    compileOnly(group = "com.sk89q.worldguard", name = "worldguard-bukkit", version = "7.0.13")
    compileOnly(group = "com.plotsquared", name = "PlotSquared-Core", version = "6.4.0")
    compileOnly(group = "com.plotsquared", name = "PlotSquared-Bukkit", version = "6.4.0")
    compileOnly(group = "com.gmail.nossr50.mcMMO", name = "mcMMO", version = "2.2.030") {
        exclude(group = "com.comphenix.protocol")
    }
    compileOnly("net.dmulloy2:ProtocolLib:5.4.0")
    compileOnly(libs.inject.guice)
    compileOnly(libs.reflections)
    compileOnly(libs.javassist)
    compileOnly(libs.adventure.api)
    compileOnly(libs.adventure.platform.bukkit)
    compileOnly(libs.adventure.minimessage)
}

description = "core"


tasks.shadowJar {
    dependencies {
        include(libs.wolfyutils.toString())
    }
}