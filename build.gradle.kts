plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.androidApplication).apply(false)
    alias(libs.plugins.kotlinAndroid).apply(false)
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    alias(libs.plugins.composeMultiplatform).apply(false)
}

subprojects {
    group = "io.github.zegjoker"
    if (projectDir.canonicalPath.contains("sample")) return@subprojects
    val project = this
    apply<MavenPublishPlugin>()
    apply<SigningPlugin>()
    configure<PublishingExtension> {
        publications.withType(MavenPublication::class.java).configureEach {
            val publication = this
            val javadocJar =
                project.tasks.register("${publication.name}JavadocJar", Jar::class.java) {
                    group = JavaBasePlugin.DOCUMENTATION_GROUP
                    description = "Assembles ${publication.name} Kotlin docs into a Javadoc jar"
                    archiveClassifier.set("javadoc")

                    // https://github.com/gradle/gradle/issues/26091
                    archiveBaseName.set("${archiveBaseName.get()}-${publication.name}")
                }
            artifact(javadocJar)
            pom {
                name.set("Mill")
                description.set("Mill, a compose multiplatform tool for UI state management")
                url.set("https://github.com/ZegJoker/mill")
                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("ZegJoker")
                        name.set("Stanley Xiao")
                        email.set("stanley.coder@gmail.com")
                    }
                }
                scm {
                    url.set("https://github.com/ZegJoker/mill")
                    connection.set("scm:git:https://github.com/ZegJoker/mill.git")
                    developerConnection.set("scm:git:https://github.com/ZegJoker/mill.git")
                }
                issueManagement {
                    url.set("https://github.com/ZegJoker/mill/issues")
                }
            }
        }
        repositories {
            maven {
                name = "OSSRH"
                setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = System.getenv("OSSRH_USER") ?: return@credentials
                    password = System.getenv("OSSRH_PASSWORD") ?: return@credentials
                }
            }
        }
    }

    configure<SigningExtension> {
        val key = System.getenv("SIGNING_KEY")
        val password = System.getenv("SIGNING_PASSWORD")
        val publishing: PublishingExtension by project
        useInMemoryPgpKeys(key, password)
        sign(publishing.publications)
    }
}
