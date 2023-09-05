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
        stage('Deploy') {
            script {
                sh '''
                    nohup ./mvnw spring-boot:run &
                    echo "Captured PID: $!"
                '''
            }
        }
    }
}
