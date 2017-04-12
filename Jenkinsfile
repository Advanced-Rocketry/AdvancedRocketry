 pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                mkdir libs
                cp ../../libraries/*.jar ./
                sh 'gradle build' 
                archiveArtifacts artifacts: '**build/libs/*.jar', fingerprint: true 
            }
        }
    }
}