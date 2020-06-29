 pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh './gradlew clean build --refresh-dependencies' 
                archiveArtifacts artifacts: '**build/libs/*.jar', fingerprint: true 
            }
        }
    }
}
