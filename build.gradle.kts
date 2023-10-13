plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    alias(libs.plugins.composeMultiplatform).apply(false)
}

subprojects {
    group = "coder.stanley.mill"
    plugins.withId("maven-publish") {
        configure<PublishingExtension> {
            repositories {
                maven {
                    name = "MillRepo"
                    setUrl("https://maven.pkg.github.com/ZegJoker/mill")
                    credentials {
                        username = System.getenv("MILL_REPO_USERNAME")
                        password = System.getenv("MILL_REPO_PUBLISH_KEY")
                    }
                }
            }
        }
    }
}
