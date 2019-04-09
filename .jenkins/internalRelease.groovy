#!/usr/bin/env groovy
import net.grandcentrix.tools.GitHubUtils

@Library('gcx@release/1.9') _

agent('linux') {
    stage('checkout and clean') {
        gitCheckout()
    }

    stage('Assemble the jar') {
        android.gradle "assemble"
        // Finally upload the files
        String releaseId = ghUtils.getReleaseId(tag)
        ghUtils.uploadReleaseAssets(releaseId, "build/libs/net.grandcentrix.either-1.0-SNAPSHOT.jar")
    }

    stage('Publish to artifactory') {
        withCredentials([string(credentialsId: "artifactory-deployment", variable: 'API_KEY')]) {
            android.gradle "artifactoryPublish -PpublishToInternal -PartifactoryUser=jenkins -PartifactoryKey=${API_KEY}"
        }
    }
}