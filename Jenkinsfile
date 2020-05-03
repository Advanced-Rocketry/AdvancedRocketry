 pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh './gradlew clean build curseforge236542 --refresh-dependencies' 
                archiveArtifacts artifacts: '**build/libs/*.jar', fingerprint: true 
            }
        }
    }
}
