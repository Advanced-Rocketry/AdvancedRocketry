 pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'mkdir libs'
                sh 'cp ../../libraries/*.jar ./libs'
                sh 'gradle build' 
                archiveArtifacts artifacts: '**build/libs/*.jar', fingerprint: true 
            }
        }
    }
}