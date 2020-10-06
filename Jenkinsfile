 pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh './gradlew clean build deobf --refresh-dependencies' 
                archiveArtifacts artifacts: '**build/libs/*.jar', fingerprint: true 
            }
        }
    }
}
