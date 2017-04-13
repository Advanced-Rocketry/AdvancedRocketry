 pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'mkdir -p libs'
                sh 'cp ../../libraries/*.jar ./libs'

                sh 'gradle clean' 
                sh 'gradle build' 
                archiveArtifacts artifacts: '**build/libs/*.jar', fingerprint: true 
            }
        }
    }
}