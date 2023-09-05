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
            steps {
                sh '''
                    nohup ./mvnw spring-boot:run &
                    echo "Captured PID: $!"
                '''
            }
        }
    }
}
