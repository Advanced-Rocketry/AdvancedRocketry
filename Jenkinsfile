 pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'gradle clean build curseforge236542'
                archiveArtifacts artifacts: '**build/libs/*.jar', fingerprint: true 
            }
        }
    }
}