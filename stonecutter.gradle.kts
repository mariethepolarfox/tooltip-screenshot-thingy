plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active "26.2"

stonecutter parameters {
    swaps["mod_version"] = "\"" + property("version") + "\";"
    swaps["minecraft"] = "\"" + node.metadata.version + "\";"
}