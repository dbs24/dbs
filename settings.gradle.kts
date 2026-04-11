import org.gradle.api.JavaVersion.VERSION_21
import org.gradle.api.JavaVersion.current
import java.text.NumberFormat
import java.util.*

rootProject.name = "dbs"

// include subprojects
val buildSrcFolder = "buildSrc"
val projectSubFolder = "shared"
val deprecatedFolder = "deprecated"
val resourceFolder = "src/main/resources"
val projectColonSubFolder = ":$projectSubFolder"
val buildFile = "build.gradle.kts"
var projectsCount = 0

// register projects and modules
//======================================================================================================================
//  temporary exclusions

val excludeProjectsList = setOf(
    "faked-project",
    "p-cm",
)

val mandatoryProjectsList = setOf(
    "faked-project",
)
//======================================================================================================================

rootDir
    .walk()
    .maxDepth(4)
    .filter { project ->
        project.name != buildSrcFolder
                && (!project.name.contains("sss"))
                && project.isDirectory
                && !project.absolutePath.contains(deprecatedFolder)
                && file("${project.absolutePath}/$buildFile").exists()
                && ((!excludeProjectsList.any { project.absolutePath.contains(it) }
                || mandatoryProjectsList.any { project.absolutePath.contains(it) })
            .also {
                if (!it) {
                    println("### ignore project [${project.name}] (${project.absolutePath})")
                }
            })
    }
    .map { it.absolutePath.substring(rootDir.absolutePath.length).replace("/", ":") }
    .filter { it.isNotEmpty() }
    .sortedBy { (if (it.startsWith(projectColonSubFolder)) "0" else "1").plus(it) }
    .forEach {
        println("include project [${++projectsCount}] $it")
        include(it)
    }

// copy certs to project folders
//======================================================================================================================
val domainName = "k11dev.tech"
val sslSrcFolder = "certs/$domainName"
val sslCertName = "$domainName.jks"
val sslKeyCertName = "$domainName.key"
val sslCertChainName = "$domainName.pem"
val bannerFileName = "banner.txt"
val sslCertSrcFileName = "$rootDir/$sslSrcFolder/$sslCertName"
val sslKeyCertSrcFileName = "$rootDir/$sslSrcFolder/$sslKeyCertName"
val sslCertChainSrcFileName = "$rootDir/$sslSrcFolder/$sslCertChainName"
val sslCertSrcFile = file(sslCertSrcFileName)
val sslKeyCertSrcFile = file(sslKeyCertSrcFileName)
val sslCertChainSrcFile = file(sslCertChainSrcFileName)

fun copyCert(absolutePath: String, srcFile: File) {

    if (!srcFile.exists()) {
        throw NoSuchElementException("certs file does not exists: '$srcFile'")
    }
    //println("source ssl cert: $srcFile, lastModified: ${srcFile.lastModified()}")

    val fileName = srcFile.name
    val sslCertSrcFileLastModified = srcFile.lastModified()
    val dstSslFileName = "$absolutePath/$resourceFolder/$fileName"
    val sslDstCertFile = file(dstSslFileName)
    val needUpdateSslCert = if (sslDstCertFile.exists()) {
        sslDstCertFile.lastModified() != sslCertSrcFileLastModified
    } else {
        true
    }
    if (needUpdateSslCert) {
        println("refresh ssl cert: $dstSslFileName, lastModified: ${sslDstCertFile.lastModified()}")
        if (sslDstCertFile.exists()) {
            sslDstCertFile.delete()
        }
        srcFile.copyTo(sslDstCertFile, true)
        sslDstCertFile.setLastModified(sslCertSrcFileLastModified)
    }
}
//======================================================================================================================

rootDir.walk()
    .maxDepth(3)
    .filter { dir ->
        dir.isDirectory &&
                dir.name != buildSrcFolder &&
                !dir.name.endsWith("-api") &&
                !dir.name.endsWith("-starter") &&
                !dir.name.endsWith("-core") &&
                !dir.name.endsWith("-ets") &&
                !dir.absolutePath.contains(deprecatedFolder) &&
                runCatching { file("${dir.absolutePath}/$resourceFolder").exists() }.getOrDefault(false)
    }
    .forEach { dir ->
        // val dstSslFileName = "${it.absolutePath}/$resourceFolder/$sslCertName"
        listOf(sslCertSrcFile, sslKeyCertSrcFile, sslCertChainSrcFile).forEach { cert ->
            copyCert(dir.absolutePath, cert)
        }
    }

//======================================================================================================================
buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}
