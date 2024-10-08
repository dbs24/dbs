package org.dbs.entity.core.v2.type

enum class Application(
    private val applicationName: String
) {
    CORE("core"),
    CHESS("chess-mgmt"),
    SAND_BOX("sandbox"),
    INDUSTRIAL("industrial")
}
