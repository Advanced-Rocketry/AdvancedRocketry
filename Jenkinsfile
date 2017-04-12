 pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'mkdir libs'
                sh 'cp ../../libraries/*.jar ./'
                sh 'gradle build' 
                archiveArtifacts artifacts: '**build/libs/*.jar', fingerprint: true 
            }
        }
    }
}