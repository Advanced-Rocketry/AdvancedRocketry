 pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh './gradlew clean build deobf curseforge236542 --refresh-dependencies' 
                archiveArtifacts artifacts: '**output/*.jar', fingerprint: true 
            }
        }
    }
}
