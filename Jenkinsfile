pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build and Test') {
            steps {
                sh './mvnw clean install'
            }
        }
        stage('Build Docker Image') {
            steps {
                sh 'docker build -t springboot-shopping .'
            }
        }
        stage('Run Docker Container') {
            steps {
                sh 'docker run -p 8080:8080 -d springboot-shopping'
            }
        }
    }
}
