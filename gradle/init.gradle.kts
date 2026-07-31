allprojects {
    configurations.all {
        resolutionStrategy.cacheDynamicVersionsFor(0, "seconds")
    }
}
