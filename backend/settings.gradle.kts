pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://repo.spring.io/milestone") }
    }
}

rootProject.name = 'baheka-sentinel'

include(
    'services:sentinel-gateway',
    'services:sentinel-risk',
    'services:sentinel-aml',
    'services:sentinel-compliance',
    'services:sentinel-security',
    'services:sentinel-notification',
    'shared:common',
    'shared:security',
    'shared:kafka'
)
