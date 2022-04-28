 pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh './gradlew build curseforge236542 publish --refresh-dependencies' 
                archiveArtifacts artifacts: '**build/libs/*.jar', fingerprint: true 
            }
        }
    }
}
