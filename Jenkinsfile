 pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'gradle build' 
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true 
            }
        }
    }
}