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
                sh '''
                    eval $(minikube docker-env)
                    docker build -t shopping .
                '''
            }
        }
        stage('Kubernetes Deployment') {
            steps {
                sh 'kubectl apply -f k8s.yaml'
            }
        }
    }
}
