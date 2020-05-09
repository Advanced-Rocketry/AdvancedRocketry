 pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'mkdir -p libs'
                sh 'cp ../../libraries/*.jar ./libs'

                sh './gradlew clean build curseforge236542 --refresh-dependencies'
                archiveArtifacts artifacts: '**build/libs/*.jar', fingerprint: true 
            }
        }
    }
}
