 pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'gradle clean' 
                sh 'gradle build' 
                sh 'gradle curseforge236542'
                archiveArtifacts artifacts: '**build/libs/*.jar', fingerprint: true 
            }
        }
    }
}
